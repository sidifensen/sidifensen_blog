<template>
  <div class="article-catalog" :class="{ 'is-fixed': isFixed }">
    <div class="catalog-header">
      <el-icon>
        <Document />
      </el-icon>
      <span>目录</span>
    </div>

    <!-- 目录列表 -->
    <div class="catalog-list" v-if="catalog.length > 0">
      <div v-for="(item, index) in catalog" :key="index" class="catalog-item" :class="[`level-${item.level}`, { active: currentHeading === item.text }]" @click="scrollToHeading(item.text)">
        {{ item.text }}
      </div>
    </div>

    <!-- 无目录提示 -->
    <el-empty v-else description="暂无目录" :image-size="100" />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from "vue";
import { Document } from "@element-plus/icons-vue";

// Props 定义
const props = defineProps({
  content: {
    type: String,
    required: true,
  },
});

// 目录数据
const catalog = ref([]);
const currentHeading = ref("");
const isFixed = ref(false);

// 解析文章内容中的标题（HTML格式）
const parseHeadings = () => {
  if (!props.content) {
    catalog.value = [];
    return;
  }

  // 创建DOMParser实例用于解析HTML字符串
  const parser = new DOMParser();
  // 将HTML字符串解析为DOM文档对象
  const doc = parser.parseFromString(props.content, "text/html");
  // 获取所有标题元素(h1-h6)
  const headings = doc.querySelectorAll("h1, h2, h3, h4, h5, h6");

  catalog.value = Array.from(headings).map((heading) => ({
    level: parseInt(heading.tagName.substring(1)), // 提取标题级别(1-6)
    text: heading.textContent.trim(), // 获取标题文本内容
  }));
};

// 滚动到指定标题
const scrollToHeading = (headingText) => {
  const headings = document.querySelectorAll(".article-body h1, .article-body h2, .article-body h3, .article-body h4, .article-body h5, .article-body h6");

  for (const heading of headings) {
    if (heading.textContent.trim() === headingText) {
      heading.scrollIntoView({ behavior: "smooth", block: "start" });
      break;
    }
  }
};

// 监听滚动，高亮当前标题和判断是否固定
const handleScroll = () => {
  const headings = document.querySelectorAll(".article-body h1, .article-body h2, .article-body h3, .article-body h4, .article-body h5, .article-body h6");
  const scrollPosition = window.scrollY;

  // 判断是否应该固定目录（当滚动超过文章开始位置时）
  const articleContent = document.querySelector(".article-content");
  if (articleContent) {
    const articleTop = articleContent.offsetTop;
    const shouldBeFixed = scrollPosition > articleTop - 100;

    // 如果状态发生变化，记录原始位置
    if (shouldBeFixed && !isFixed.value) {
      const catalogElement = document.querySelector(".article-catalog");
      if (catalogElement) {
        // 获取目录元素的原始位置
        const rect = catalogElement.getBoundingClientRect();

        // 设置 CSS 变量来保持原始位置
        catalogElement.style.setProperty("--catalog-left", `${rect.left}px`);
        catalogElement.style.setProperty("--catalog-width", `${rect.width}px`);
      }
    }

    isFixed.value = shouldBeFixed;
  }

  for (const heading of headings) {
    const position = heading.offsetTop;

    if (scrollPosition >= position - 100) {
      currentHeading.value = heading.textContent.trim();
    }
  }
};

// 生命周期钩子
onMounted(() => {
  parseHeadings();
  window.addEventListener("scroll", handleScroll);
});

onUnmounted(() => {
  window.removeEventListener("scroll", handleScroll);
});
</script>

<style lang="scss" scoped>
// 文章目录容器
.article-catalog {
  background-color: var(--el-bg-color);
  border-radius: 8px;
  padding: 20px;
  box-shadow: var(--el-box-shadow-light);
  transition: all 0.3s ease;

  // 固定状态样式
  &.is-fixed {
    position: fixed;
    top: 90px;
    left: var(--catalog-left, auto);
    width: var(--catalog-width, 280px);
    max-height: calc(100vh - 120px);
    z-index: 100;
    backdrop-filter: blur(10px);
    background-color: rgba(var(--el-bg-color-rgb), 0.9);
    border: 1px solid var(--el-border-color);
  }

  // 目录头部
  .catalog-header {
    display: flex;
    align-items: center;
    gap: 8px;
    padding-bottom: 16px;
    margin-bottom: 16px;
    font-size: 16px;
    font-weight: 600;
    color: var(--el-text-color-primary);
    border-bottom: 1px solid var(--el-border-color-lighter);
  }

  // 目录列表
  .catalog-list {
    max-height: calc(100vh - 200px);
    overflow-y: auto;

    // 固定状态下的目录列表高度调整
    .is-fixed & {
      max-height: calc(100vh - 160px);
    }

    // 目录项
    .catalog-item {
      padding: 8px 0;
      cursor: pointer;
      color: var(--el-text-color-regular);
      font-size: 14px;
      transition: all 0.3s ease;

      &:hover {
        color: var(--el-color-primary);
      }

      &.active {
        color: var(--el-color-primary);
        font-weight: 500;
      }

      // 不同级别标题的缩进
      &.level-1 {
        padding-left: 0;
      }

      &.level-2 {
        padding-left: 16px;
      }

      &.level-3 {
        padding-left: 32px;
      }

      &.level-4 {
        padding-left: 48px;
      }

      &.level-5 {
        padding-left: 64px;
      }

      &.level-6 {
        padding-left: 80px;
      }
    }

    // 滚动条样式
    &::-webkit-scrollbar {
      width: 4px;
    }

    &::-webkit-scrollbar-thumb {
      background-color: var(--el-border-color);
      border-radius: 2px;
    }

    &::-webkit-scrollbar-track {
      background-color: transparent;
    }
  }
}

// 响应式设计
@media (max-width: 1024px) {
  .article-catalog {
    // 平板端固定位置调整
    &.is-fixed {
      width: 260px;
    }
  }
}

@media (max-width: 768px) {
  .article-catalog {
    // 移动端隐藏固定目录功能
    &.is-fixed {
      position: static;
      width: auto;
      max-height: none;
      backdrop-filter: none;
      background-color: var(--el-bg-color);
    }
  }
}
</style>
