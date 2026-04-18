---
type: module_card
title: sidifensen-user-homepage
summary: 用户端首页由单文件页面负责 Hero、文章列表、侧边栏和页脚组合，并通过接口加载真实内容。
tags:
  - frontend
  - homepage
  - vue
owned_paths:
  - sidifensen_blog_frontend/sidifensen_user/src/views/Home/index.vue
  - sidifensen_blog_frontend/sidifensen_user/src/components/hero/HeroCosmicRing.vue
related_docs:
  - docs/superpowers/memory/index.md
entrypoints:
  - sidifensen_blog_frontend/sidifensen_user/src/views/Home/index.vue
  - sidifensen_blog_frontend/sidifensen_user/src/components/hero/HeroCosmicRing.vue
last_verified_commit: ddbfd42
status: active
---

# 首页模块卡片

## Responsibilities

- 负责用户端首页首屏 Hero、文章列表区、侧边栏与页脚的整体拼装。
- 通过 `getAllArticleList`、`getHotTags`、`getCommunityStats` 拉取真实数据，禁止假数据占位。
- 维护首页特有的滚动入场、打字机文案、统计数字动画等纯展示逻辑。

## Entry points

- [index.vue](D:/code/sidifensen_blog/sidifensen_blog_frontend/sidifensen_user/src/views/Home/index.vue)
- [HeroCosmicRing.vue](D:/code/sidifensen_blog/sidifensen_blog_frontend/sidifensen_user/src/components/hero/HeroCosmicRing.vue)
- [router/index.js](D:/code/sidifensen_blog/sidifensen_blog_frontend/sidifensen_user/src/router/index.js)

## Invariants

- 首页路由入口固定为 `/`，由 `Layout` 子路由加载 `views/Home/index.vue`。
- 首页文章、标签、社区统计必须来自接口返回，不能改成静态演示数据。
- 黑夜模式依赖页面内部 CSS 变量和 `html.dark` 覆盖。
- `HeroCosmicRing` 在 `1200px` 以下隐藏，当前首页桌面端与移动端视觉存在明显分流。

## Extension points

- 可以继续在 `Home/index.vue` 中新增页面级滚动视觉逻辑，但应避免破坏接口请求和已有入场动画。
- Hero 视觉装饰应优先复用 `components/hero/` 目录，而不是把复杂装饰继续塞回首页文件。
- 如需新增整页滚动状态，优先以统一状态源驱动多个层的 CSS 变量。

## Common pitfalls

- 首页单文件已经较大，新增交互时容易继续堆叠在 `index.vue` 中，需警惕职责继续膨胀。
- 当前样式含多处组件内颜色硬编码，与仓库“优先全局变量”的规范并不完全一致，新增样式不要继续扩大这个问题面。
- 现有 `observeElements` 直接查询 `.article-card`，如果未来文章卡片结构调整，滚动入场可能失效。
