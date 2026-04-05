import fg from 'fast-glob'
import fs from 'node:fs/promises'
import path from 'node:path'

const VIRTUAL_MODULE_ID = 'virtual:svg-icons-register'
const RESOLVED_VIRTUAL_MODULE_ID = `\0${VIRTUAL_MODULE_ID}`

const normalizePath = (filePath) => filePath.replaceAll('\\', '/')

const buildSymbolId = (filePath, iconDir, pattern) => {
  const relativePath = normalizePath(path.relative(iconDir, filePath))
  const parsedPath = path.parse(relativePath)
  const dirName = parsedPath.dir ? parsedPath.dir.replaceAll('/', '-') : ''

  return pattern.replaceAll('[name]', parsedPath.name).replaceAll('[dir]', dirName)
}

const convertSvgToSymbol = (svgContent, symbolId) => {
  const cleanedSvg = svgContent
    .replace(/<\?xml[\s\S]*?\?>/gi, '')
    .replace(/<!doctype[\s\S]*?>/gi, '')
    .trim()

  const svgMatch = cleanedSvg.match(/<svg\b([^>]*)>([\s\S]*?)<\/svg>/i)
  if (!svgMatch) {
    return ''
  }

  const [, svgAttributes, svgBody] = svgMatch
  const viewBoxMatch = svgAttributes.match(/\bviewBox=(["'])(.*?)\1/i)
  const viewBoxAttribute = viewBoxMatch ? ` viewBox="${viewBoxMatch[2]}"` : ''

  return `<symbol id="${symbolId}"${viewBoxAttribute}>${svgBody.trim()}</symbol>`
}

const loadSpriteMarkup = async (iconDirs, symbolIdPattern) => {
  const symbolTasks = iconDirs.map(async (iconDir) => {
    const resolvedIconDir = path.resolve(iconDir)
    const svgFiles = await fg('**/*.svg', {
      absolute: true,
      cwd: resolvedIconDir,
      onlyFiles: true,
    })

    const symbolList = await Promise.all(
      svgFiles.map(async (filePath) => {
        const svgContent = await fs.readFile(filePath, 'utf8')
        const symbolId = buildSymbolId(filePath, resolvedIconDir, symbolIdPattern)

        return convertSvgToSymbol(svgContent, symbolId)
      }),
    )

    return symbolList.filter(Boolean).join('')
  })

  const symbols = (await Promise.all(symbolTasks)).join('')
  return `<svg xmlns="http://www.w3.org/2000/svg" style="position:absolute;width:0;height:0;overflow:hidden" aria-hidden="true">${symbols}</svg>`
}

// 用本地虚拟模块替代第三方 svg 精灵图插件，避免旧依赖链带来的安全告警
export const createSvgSpritePlugin = ({ iconDirs = [], symbolId = '[name]' } = {}) => {
  const resolvedIconDirs = iconDirs.map((iconDir) => path.resolve(iconDir))

  return {
    name: 'vite:svg-sprite-register',
    configureServer(server) {
      server.watcher.add(resolvedIconDirs)
    },
    resolveId(source) {
      if (source === VIRTUAL_MODULE_ID) {
        return RESOLVED_VIRTUAL_MODULE_ID
      }

      return null
    },
    async load(id) {
      if (id !== RESOLVED_VIRTUAL_MODULE_ID) {
        return null
      }

      const spriteMarkup = await loadSpriteMarkup(resolvedIconDirs, symbolId)
      return `
const spriteMarkup = ${JSON.stringify(spriteMarkup)};
const spriteNodeId = "__sidifensen_svg_sprite__";

const mountSprite = () => {
  if (typeof document === "undefined" || document.getElementById(spriteNodeId)) {
    return;
  }

  const container = document.createElement("div");
  container.innerHTML = spriteMarkup;

  const spriteNode = container.firstElementChild;
  if (!spriteNode) {
    return;
  }

  spriteNode.id = spriteNodeId;
  document.body.prepend(spriteNode);
};

if (typeof document !== "undefined") {
  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", mountSprite, { once: true });
  } else {
    mountSprite();
  }
}

export default mountSprite;
`
    },
    handleHotUpdate(ctx) {
      if (!ctx.file.endsWith('.svg')) {
        return
      }

      const normalizedFile = normalizePath(ctx.file)
      const isSvgIconFile = resolvedIconDirs.some((iconDir) => {
        return normalizedFile.startsWith(normalizePath(iconDir))
      })

      if (!isSvgIconFile) {
        return
      }

      const virtualModule = ctx.server.moduleGraph.getModuleById(RESOLVED_VIRTUAL_MODULE_ID)
      if (!virtualModule) {
        return
      }

      ctx.server.moduleGraph.invalidateModule(virtualModule)
      return [virtualModule]
    },
  }
}
