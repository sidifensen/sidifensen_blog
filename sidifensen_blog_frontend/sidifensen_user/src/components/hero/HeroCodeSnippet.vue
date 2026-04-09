<template>
  <div class="hero-code">
    <div class="code-window">
      <div class="code-header">
        <span class="dot red"></span>
        <span class="dot yellow"></span>
        <span class="dot green"></span>
        <span class="code-title">{{ currentCode.title }}</span>
      </div>
      <div class="code-content">
        <pre><code>{{ displayedCode }}<span v-if="!isTyping" class="cursor">|</span></code></pre>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const codes = [
  {
    title: 'project.json',
    plain: `{
  "name": "sidifensen_blog",
  "description": "技术博客系统",
  "author": "sidifensen",
  "tech": ["Vue3", "Spring Boot", "MySQL"],
  "features": [
    "文章发布与管理",
    "用户社区互动",
    "相册展示功能"
  ]
}`,
    highlighted: `{<span class="comment">  "name"</span>: <span class="string">"sidifensen_blog"</span>,<span class="comment">
  "description"</span>: <span class="string">"技术博客系统"</span>,<span class="comment">
  "author"</span>: <span class="string">"sidifensen"</span>,<span class="comment">
  "tech"</span>: [<span class="string">"Vue3"</span>, <span class="string">"Spring Boot"</span>, <span class="string">"MySQL"</span>],<span class="comment">
  "features"</span>: [<span class="comment">
    "文章发布与管理"</span>,<span class="comment">
    "用户社区互动"</span>,<span class="comment">
    "相册展示功能"</span>
  ]
}`,
  },
  {
    title: 'stack.sh',
    plain: `# 前端技术栈
Vue3 + Composition API
Element Plus UI
SCSS 样式管理
Vite 构建工具

# 后端技术栈
Spring Boot 框架
MyBatis-Plus ORM
JWT 身份认证
Redis 缓存加速`,
    highlighted: `<span class="comment"># 前端技术栈</span>
<span class="variable">Vue3</span> + <span class="variable">Composition API</span>
<span class="variable">Element Plus UI</span>
<span class="variable">SCSS</span> 样式管理
<span class="variable">Vite</span> 构建工具

<span class="comment"># 后端技术栈</span>
<span class="variable">Spring Boot</span> 框架
<span class="variable">MyBatis-Plus</span> ORM
<span class="variable">JWT</span> 身份认证
<span class="variable">Redis</span> 缓存加速`,
  },
  {
    title: 'author.md',
    plain: `# 关于作者

Name: sidifensen
Focus: 前端开发 / 全栈技术
From: 中国

# 技术博客
分享前端工程化，性能优化、
Vue/React 生态、Node.js 后端开发

# 建站时间
2024 年上线，持续更新中...`,
    highlighted: `<span class="comment"># 关于作者</span>
<span class="property">Name</span>: <span class="string">sidifensen</span>
<span class="property">Focus</span>: <span class="string">前端开发 / 全栈技术</span>
<span class="property">From</span>: <span class="string">中国</span>

<span class="comment"># 技术博客</span>
分享<span class="string">前端工程化，性能优化、</span>
<span class="string">Vue/React 生态、Node.js 后端开发</span>

<span class="comment"># 建站时间</span>
<span class="number">2024</span> 年上线，持续更新中...`,
  },
]

const currentCode = ref(codes[0])
const displayedCode = ref('')
const isTyping = ref(true)
let charIndex = 0
let codeIndex = 0
let isDeleting = false
let typeTimer = null

const TYPE_SPEED = 40
const DELETE_SPEED = 20
const PAUSE_AFTER_TYPE = 2500
const PAUSE_AFTER_DELETE = 400

const typeCode = () => {
  if (isDeleting) {
    if (charIndex > 0) {
      displayedCode.value = currentCode.value.plain.substring(0, charIndex - 1)
      charIndex--
      typeTimer = setTimeout(typeCode, DELETE_SPEED)
    } else {
      isDeleting = false
      isTyping.value = true
      codeIndex = (codeIndex + 1) % codes.length
      currentCode.value = codes[codeIndex]
      typeTimer = setTimeout(typeCode, PAUSE_AFTER_DELETE)
    }
  } else {
    if (charIndex < currentCode.value.plain.length) {
      displayedCode.value = currentCode.value.plain.substring(0, charIndex + 1)
      charIndex++
      typeTimer = setTimeout(typeCode, TYPE_SPEED)
    } else {
      isDeleting = true
      typeTimer = setTimeout(typeCode, PAUSE_AFTER_TYPE)
    }
  }
}

onMounted(() => {
  setTimeout(typeCode, 800)
})

onUnmounted(() => {
  clearTimeout(typeTimer)
})
</script>

<style lang="scss" scoped>
.hero-code {
  position: absolute;
  right: 8%;
  top: 50%;
  transform: translateY(-50%);
  width: 480px;
  pointer-events: none;

  @media (max-width: 1200px) {
    display: none;
  }

  .code-window {
    background: var(--bg-card);
    border: 1px solid var(--border);
    border-radius: 12px;
    overflow: hidden;
    box-shadow: var(--shadow-md);
  }

  .code-header {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 12px 16px;
    background: var(--bg-subtle);
    border-bottom: 1px solid var(--border);

    .dot {
      width: 10px;
      height: 10px;
      border-radius: 50%;

      &.red {
        background: #ff5f57;
      }
      &.yellow {
        background: #febc2e;
      }
      &.green {
        background: #28c840;
      }
    }

    .code-title {
      margin-left: auto;
      font-size: 0.75rem;
      color: var(--text-muted);
      font-family: 'Berkeley Mono', 'Fira Code', monospace;
    }
  }

  .code-content {
    padding: 20px;
    font-family: 'Berkeley Mono', 'Fira Code', monospace;
    font-size: 0.85rem;
    line-height: 1.8;
    min-height: 360px;

    pre {
      margin: 0;
    }

    code {
      color: var(--text-secondary);
    }

    .cursor {
      animation: blink 0.8s step-end infinite;
    }
  }
}

@keyframes blink {
  0%,
  100% {
    opacity: 1;
  }
  50% {
    opacity: 0;
  }
}

html.dark {
  .code-content {
    code {
      color: #abb2bf;
    }
  }
}
</style>
