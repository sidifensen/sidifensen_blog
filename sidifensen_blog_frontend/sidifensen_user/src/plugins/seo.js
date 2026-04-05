/**
 * SEO 工具 - 动态管理页面 Meta 信息
 */
import { useHead } from '@vueuse/head'

/**
 * 设置页面 SEO 信息
 * @param {Object} options
 * @param {string} options.title - 页面标题
 * @param {string} options.description - 页面描述
 * @param {string} [options.keywords] - 页面关键词
 * @param {string} [options.image] - 分享图片
 * @param {string} [options.url] - 页面 URL
 */
export function useSeoMeta({ title, description, keywords, image, url }) {
  const siteName = '斯蒂芬森社区'
  const defaultDescription = '斯蒂芬森社区是一个分享技术文章、生活感悟和创意作品的多元化社区平台'
  const fullTitle = title ? `${title} - ${siteName}` : siteName

  useHead({
    title: fullTitle,
    meta: [
      { name: 'description', content: description || defaultDescription },
      { name: 'keywords', content: keywords || '技术博客,程序员,前端,后端,全栈,编程,开发者社区' },
      { property: 'og:title', content: fullTitle },
      { property: 'og:description', content: description || defaultDescription },
      { property: 'og:type', content: 'website' },
      { property: 'og:site_name', content: siteName },
      ...(image ? [{ property: 'og:image', content: image }] : []),
      ...(url ? [{ property: 'og:url', content: url }] : []),
      { name: 'twitter:title', content: fullTitle },
      { name: 'twitter:description', content: description || defaultDescription },
      ...(image ? [{ name: 'twitter:image', content: image }] : []),
    ],
    link: url ? [{ rel: 'canonical', href: url }] : [],
  })
}
