<template>
  <div class="empty-state">
    <!-- 自定义插图 -->
    <div class="empty-illustration">
      <svg
        v-if="type === 'article'"
        viewBox="0 0 120 100"
        fill="none"
        xmlns="http://www.w3.org/2000/svg"
      >
        <!-- 文档背景 -->
        <rect
          x="20"
          y="15"
          width="60"
          height="75"
          rx="4"
          fill="var(--empty-illustration-bg)"
          stroke="var(--empty-illustration-border)"
          stroke-width="2"
        />
        <!-- 文档线条 -->
        <line
          x1="32"
          y1="35"
          x2="68"
          y2="35"
          stroke="var(--empty-illustration-line)"
          stroke-width="2"
          stroke-linecap="round"
        />
        <line
          x1="32"
          y1="47"
          x2="58"
          y2="47"
          stroke="var(--empty-illustration-line)"
          stroke-width="2"
          stroke-linecap="round"
        />
        <line
          x1="32"
          y1="59"
          x2="52"
          y2="59"
          stroke="var(--empty-illustration-line)"
          stroke-width="2"
          stroke-linecap="round"
        />
        <!-- 空白标签 -->
        <circle cx="85" cy="30" r="20" fill="var(--empty-illustration-accent)" opacity="0.15" />
        <path
          d="M85 20V40M75 30H95"
          stroke="var(--empty-illustration-accent)"
          stroke-width="2.5"
          stroke-linecap="round"
        />
      </svg>

      <svg
        v-else-if="type === 'comment'"
        viewBox="0 0 120 100"
        fill="none"
        xmlns="http://www.w3.org/2000/svg"
      >
        <!-- 气泡背景 -->
        <path
          d="M20 25C20 21.6863 22.6863 19 26 19H74C77.3137 19 80 21.6863 80 25V55C80 58.3137 77.3137 61 74 61H45L30 76V61H26C22.6863 61 20 58.3137 20 55V25Z"
          fill="var(--empty-illustration-bg)"
          stroke="var(--empty-illustration-border)"
          stroke-width="2"
        />
        <!-- 气泡点 -->
        <circle cx="35" cy="38" r="3" fill="var(--empty-illustration-line)" />
        <circle cx="50" cy="38" r="3" fill="var(--empty-illustration-line)" />
        <circle cx="65" cy="38" r="3" fill="var(--empty-illustration-line)" />
        <!-- 感叹号 -->
        <circle cx="90" cy="75" r="15" fill="var(--empty-illustration-accent)" opacity="0.15" />
        <path
          d="M90 68V78M90 82V83"
          stroke="var(--empty-illustration-accent)"
          stroke-width="2.5"
          stroke-linecap="round"
        />
      </svg>

      <svg
        v-else-if="type === 'message'"
        viewBox="0 0 120 100"
        fill="none"
        xmlns="http://www.w3.org/2000/svg"
      >
        <!-- 信封背景 -->
        <rect
          x="20"
          y="25"
          width="80"
          height="55"
          rx="4"
          fill="var(--empty-illustration-bg)"
          stroke="var(--empty-illustration-border)"
          stroke-width="2"
        />
        <!-- 信封折痕 -->
        <path d="M20 35L60 58L100 35" stroke="var(--empty-illustration-border)" stroke-width="2" />
        <path
          d="M60 58V80"
          stroke="var(--empty-illustration-line)"
          stroke-width="2"
          stroke-linecap="round"
        />
        <!-- 空白标记 -->
        <circle cx="95" cy="20" r="15" fill="var(--empty-illustration-accent)" opacity="0.15" />
        <path
          d="M95 13V27M87 20H103"
          stroke="var(--empty-illustration-accent)"
          stroke-width="2.5"
          stroke-linecap="round"
        />
      </svg>

      <svg
        v-else-if="type === 'search'"
        viewBox="0 0 120 100"
        fill="none"
        xmlns="http://www.w3.org/2000/svg"
      >
        <!-- 放大镜圆形 -->
        <circle
          cx="50"
          cy="45"
          r="28"
          fill="var(--empty-illustration-bg)"
          stroke="var(--empty-illustration-border)"
          stroke-width="2.5"
        />
        <!-- 放大镜手柄 -->
        <line
          x1="70"
          y1="65"
          x2="90"
          y2="85"
          stroke="var(--empty-illustration-border)"
          stroke-width="2.5"
          stroke-linecap="round"
        />
        <!-- 镜面高光 -->
        <path
          d="M38 35C38 35 42 31 50 31C58 31 62 35 62 35"
          stroke="var(--empty-illustration-accent)"
          stroke-width="2"
          stroke-linecap="round"
          opacity="0.6"
        />
        <!-- 问号 -->
        <circle cx="90" cy="25" r="15" fill="var(--empty-illustration-accent)" opacity="0.15" />
        <path
          d="M90 18C90 18 96 18 96 24C96 28 93 29 90 30M90 34V36"
          stroke="var(--empty-illustration-accent)"
          stroke-width="2"
          stroke-linecap="round"
        />
      </svg>

      <svg v-else viewBox="0 0 120 100" fill="none" xmlns="http://www.w3.org/2000/svg">
        <!-- 通用圆形 -->
        <circle
          cx="60"
          cy="50"
          r="35"
          fill="var(--empty-illustration-bg)"
          stroke="var(--empty-illustration-border)"
          stroke-width="2"
        />
        <!-- 内圈 -->
        <circle
          cx="60"
          cy="50"
          r="20"
          stroke="var(--empty-illustration-line)"
          stroke-width="2"
          stroke-dasharray="4 4"
        />
        <!-- 中心点 -->
        <circle cx="60" cy="50" r="5" fill="var(--empty-illustration-accent)" />
      </svg>
    </div>

    <!-- 描述文字 -->
    <p class="empty-description">{{ displayDescription }}</p>

    <!-- 插槽内容（按钮等） -->
    <slot />
  </div>
</template>

<script setup>
import { computed } from 'vue'

// 定义组件props
const props = defineProps({
  // 空状态类型
  type: {
    type: String,
    default: 'custom',
    validator: (value) => ['article', 'comment', 'message', 'search', 'custom'].includes(value),
  },
  // 自定义描述文字
  description: {
    type: String,
    default: '',
  },
})

// 类型对应的默认描述
const typeDescriptions = {
  article: '暂无文章',
  comment: '暂无评论',
  message: '暂无消息',
  search: '未找到搜索结果',
}

// 显示的描述文字
const displayDescription = computed(() => {
  if (props.description) {
    return props.description
  }
  return typeDescriptions[props.type] || ''
})
</script>

<style lang="scss" scoped>
// 空状态容器
.empty-state {
  --empty-text-color: #606266;
  --empty-secondary-color: #909399;
  --empty-illustration-bg: #f5f7fa;
  --empty-illustration-border: #dcdfe6;
  --empty-illustration-line: #c0c4cc;
  --empty-illustration-accent: #409eff;

  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  animation: fadeIn 0.4s ease-out;

  // 黑夜模式适配
  html.dark & {
    --empty-text-color: #cbd5e1;
    --empty-secondary-color: #94a3b8;
    --empty-illustration-bg: rgba(255, 255, 255, 0.05);
    --empty-illustration-border: #475569;
    --empty-illustration-line: #64748b;
    --empty-illustration-accent: #60a5fa;
  }

  // 插图容器
  .empty-illustration {
    width: 120px;
    height: 100px;
    margin-bottom: 24px;
    animation: floatIn 0.6s ease-out;

    svg {
      width: 100%;
      height: 100%;
    }
  }

  // 描述文字
  .empty-description {
    margin: 0;
    font-size: 15px;
    color: var(--empty-secondary-color);
    text-align: center;
    line-height: 1.6;
  }
}

// 动画
@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes floatIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
