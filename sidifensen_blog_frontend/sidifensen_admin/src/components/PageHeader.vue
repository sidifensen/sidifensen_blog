<template>
  <div class="page-header">
    <div class="header-content">
      <div>
        <h1 class="page-title">{{ title }}</h1>
        <p v-if="description" class="page-description">{{ description }}</p>
      </div>
      <div v-if="$slots.actions" class="header-actions">
        <slot name="actions" />
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * PageHeader 页面标题组件
 * @description 提供统一的页面标题区域结构，包含标题、描述和操作按钮
 */
defineProps({
  /**
   * 页面标题
   */
  title: {
    type: String,
    required: true
  },
  /**
   * 页面描述
   */
  description: {
    type: String,
    default: ''
  }
})
</script>

<style lang="scss" scoped>
.page-header {
  margin-bottom: 24px;

  .header-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 16px;
    padding: 18px 20px;
    border: 1px solid var(--border);
    border-radius: 18px;
    background: var(--bg-card);
    box-shadow: var(--shadow-card);
  }

  .page-title {
    margin: 0 0 6px;
    font-size: 22px;
    font-weight: 700;
    color: var(--text-primary);
    letter-spacing: -0.03em;
  }

  .page-description {
    margin: 0;
    font-size: 13px;
    color: var(--text-muted);
  }

  .header-actions {
    display: flex;
    align-items: center;
  }
}

// 移动端响应式
@media (max-width: 768px) {
  .page-header {
    .header-content {
      flex-direction: column;
      align-items: stretch;

      .header-actions {
        width: 100%;
      }
    }
  }
}
</style>
