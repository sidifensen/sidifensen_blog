<template>
  <div class="stats-card" :class="{ 'stats-card--compact': compact }">
    <div class="stats-list" :style="{ gridTemplateColumns: `repeat(${columns}, 1fr)` }">
      <div v-for="(stat, index) in stats" :key="index" class="stat-item">
        <div class="stat-value">
          <CountTo v-if="animated" :end="stat.value" :duration="2000" />
          <span v-else>{{ stat.value }}</span>
        </div>
        <div class="stat-label">{{ stat.label }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import CountTo from '@/components/CountTo.vue'

// 定义 props
const props = defineProps({
  // 统计数据列表
  stats: {
    type: Array,
    required: true,
    default: () => [],
  },
  // 是否显示数字动画
  animated: {
    type: Boolean,
    default: true,
  },
  // 是否紧凑模式
  compact: {
    type: Boolean,
    default: false,
  },
  // 列数
  columns: {
    type: Number,
    default: 2,
    validator: (value) => [2, 4].includes(value),
  },
})
</script>

<style lang="scss" scoped>
.stats-card {
  width: 100%;

  // 统计列表网格布局
  .stats-list {
    display: grid;
    gap: 16px;
  }

  // 统计项
  .stat-item {
    text-align: center;
    padding: 16px;
    background: var(--bg-page);
    border-radius: 8px;
  }

  // 统计数值
  .stat-value {
    font-size: 1.5rem;
    font-weight: 700;
    color: var(--accent);
    margin-bottom: 4px;
  }

  // 统计标签
  .stat-label {
    font-size: 0.75rem;
    color: var(--text-muted);
  }
}

// 紧凑模式
.stats-card--compact {
  .stat-item {
    padding: 12px;
  }

  .stat-value {
    font-size: 1.25rem;
    margin-bottom: 2px;
  }

  .stat-label {
    font-size: 0.7rem;
  }
}

// 4列布局调整
@media (max-width: 768px) {
  .stats-card .stats-list {
    grid-template-columns: repeat(2, 1fr) !important;
  }
}
</style>

<style lang="scss">
// 黑夜模式
html.dark {
  .stats-card {
    .stat-item {
      background: var(--bg-page);
    }
  }
}
</style>
