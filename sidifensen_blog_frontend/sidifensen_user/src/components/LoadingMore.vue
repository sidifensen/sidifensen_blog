<script setup lang="ts">
/**
 * 加载更多组件
 * @description 用于列表底部显示加载状态
 */
interface Props {
  /** 是否显示加载中状态 */
  loading: boolean
  /** 加载中的提示文字 */
  text?: string
}

withDefaults(defineProps<Props>(), {
  text: '加载更多...',
})
</script>

<template>
  <div class="loading-more">
    <template v-if="loading">
      <div class="loading-spinner"></div>
      <span>{{ text }}</span>
    </template>
    <template v-else>
      <span class="no-more">没有更多了</span>
    </template>
  </div>
</template>

<style lang="scss" scoped>
// 加载更多
.loading-more {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 0 20px;

  span {
    font-size: 12px;
    color: var(--text-muted);
  }

  .no-more {
    color: var(--text-muted);
  }

  .loading-spinner {
    width: 18px;
    height: 18px;
    border: 2px solid var(--border-light);
    border-top-color: var(--accent-color);
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin-right: 10px;
  }
}

// 加载动画
@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}
</style>
