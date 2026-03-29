<template>
  <div class="detail-card">
    <!-- 卡片头部 -->
    <div class="detail-card__header">
      <div class="detail-card__title">
        <span class="title-icon">
          <el-icon><component :is="icon" /></el-icon>
        </span>
        <span>{{ title }}</span>
      </div>
      <div v-if="$slots.actions" class="detail-card__actions">
        <slot name="actions" />
      </div>
    </div>
    <!-- 卡片内容 -->
    <div class="detail-card__content">
      <slot />
    </div>
  </div>
</template>

<script setup>
/**
 * DetailCard 通用卡片容器组件
 * @description 用于包裹图表、统计数据等内容卡片，提供统一的标题区和内容区结构
 */
defineProps({
  /**
   * 卡片标题
   */
  title: {
    type: String,
    required: true
  },
  /**
   * 卡片图标（Element Plus 图标组件）
   */
  icon: {
    type: [Object, String],
    required: true
  }
})
</script>

<style lang="scss" scoped>
// 卡片样式变量
$card-radius: 18px;

.detail-card {
  display: flex;
  flex-direction: column;
  min-height: 280px;
  border: 1px solid var(--border);
  border-radius: $card-radius;
  background: var(--bg-card);
  box-shadow: var(--shadow-card);
  overflow: hidden;

  .detail-card__header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 12px;
    padding: 16px 20px;
    border-bottom: 1px solid var(--border-light);

    .detail-card__title {
      display: inline-flex;
      align-items: center;
      gap: 10px;
      font-size: 15px;
      font-weight: 600;
      color: var(--text-primary);

      .title-icon {
        display: inline-flex;
        align-items: center;
        justify-content: center;
        width: 30px;
        height: 30px;
        border-radius: 10px;
        background: var(--title-icon-bg);
        color: var(--chart-line);
        line-height: 0;
      }
    }

    .detail-card__actions {
      display: flex;
      align-items: center;
      gap: 8px;
    }
  }

  .detail-card__content {
    flex: 1;
    padding: 20px;
    overflow: hidden;
  }
}

// 移动端响应式
@media screen and (max-width: 768px) {
  .detail-card {
    min-height: auto;

    .detail-card__header {
      flex-direction: column;
      align-items: flex-start;
      padding: 14px 16px;
    }

    .detail-card__content {
      padding: 16px;
    }
  }
}
</style>
