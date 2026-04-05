<template>
  <div class="stat-card" :class="[`stat-card--${type}`]">
    <div class="stat-card__header">
      <span class="stat-card__label">{{ label }}</span>
      <div class="stat-card__icon">
        <el-icon><component :is="icon" /></el-icon>
      </div>
    </div>
    <div class="stat-card__value" v-if="!loading">
      <span class="number">{{ displayValue }}</span>
    </div>
    <el-skeleton v-else :rows="1" animated />
    <div class="stat-card__trend">
      <span class="trend-indicator" :class="[`trend-indicator--${trendType}`]">
        <el-icon><component :is="trendIcon" /></el-icon>
        {{ trendText }}
      </span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { Top, Bottom, Right } from '@element-plus/icons-vue'

/**
 * StatCard 统计卡片组件
 * @description 用于展示单个统计指标的数据卡片
 */
const props = defineProps({
  /**
   * 统计标签
   */
  label: {
    type: String,
    required: true,
  },
  /**
   * 统计数值
   */
  value: {
    type: [Number, String],
    default: 0,
  },
  /**
   * 图标
   */
  icon: {
    type: [Object, String],
    required: true,
  },
  /**
   * 卡片类型（用于样式区分）
   */
  type: {
    type: String,
    default: 'default',
  },
  /**
   * 加载状态
   */
  loading: {
    type: Boolean,
    default: false,
  },
  /**
   * 趋势类型：positive（上涨）、negative（下跌）、neutral（持平）
   */
  trendType: {
    type: String,
    default: 'neutral',
  },
  /**
   * 趋势文本
   */
  trendText: {
    type: String,
    default: '',
  },
})

const displayValue = computed(() => {
  if (typeof props.value === 'number') {
    return props.value.toLocaleString()
  }
  return props.value
})

const trendIcon = computed(() => {
  if (props.trendType === 'positive') return Top
  if (props.trendType === 'negative') return Bottom
  return Right
})
</script>

<style lang="scss" scoped>
.stat-card {
  position: relative;
  overflow: hidden;
  padding: 20px;
  border: 1px solid var(--border);
  border-radius: 18px;
  background: var(--bg-card);
  box-shadow: var(--shadow-card);
  transition:
    transform 0.2s ease,
    border-color 0.2s ease,
    box-shadow 0.2s ease;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 3px;
    background: var(--card-accent);
  }

  &:hover {
    transform: translateY(-2px);
    border-color: var(--chart-line);
    box-shadow: var(--shadow-hover);
  }

  &.stat-card--user {
    --card-accent: linear-gradient(90deg, #64748b, #94a3b8);
  }

  &.stat-card--article {
    --card-accent: linear-gradient(90deg, #f093fb, #f5576c);
  }

  &.stat-card--visits {
    --card-accent: linear-gradient(90deg, #475569, #64748b);
  }

  &.stat-card--today {
    --card-accent: linear-gradient(90deg, #43e97b, #38f9d7);
  }

  &.stat-card--default {
    --card-accent: linear-gradient(90deg, #64748b, #94a3b8);
  }

  .stat-card__header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 14px;

    .stat-card__label {
      font-size: 13px;
      font-weight: 500;
      color: var(--text-muted);
    }

    .stat-card__icon {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      width: 32px;
      height: 32px;
      border-radius: 10px;
      background: var(--bg-card-muted);
      color: var(--text-muted);
      line-height: 0;
    }
  }

  .stat-card__value {
    margin-bottom: 14px;

    .number {
      font-size: 32px;
      font-weight: 700;
      line-height: 1;
      letter-spacing: -0.04em;
      color: var(--text-primary);
    }
  }

  .stat-card__trend {
    .trend-indicator {
      display: inline-flex;
      align-items: center;
      gap: 4px;
      padding: 4px 8px;
      border-radius: 999px;
      font-size: 12px;
      font-weight: 600;

      &.trend-indicator--positive {
        color: var(--success);
        background: var(--trend-positive-bg);
      }

      &.trend-indicator--negative {
        color: var(--danger);
        background: var(--trend-negative-bg);
      }

      &.trend-indicator--neutral {
        color: var(--text-muted);
        background: var(--trend-neutral-bg);
      }
    }
  }
}
</style>
