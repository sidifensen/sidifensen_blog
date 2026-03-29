<template>
  <DetailCard title="访问趋势" :icon="TrendCharts">
    <template #actions>
      <div class="trend-days">
        <button
          :class="['trend-days__btn', days === 7 ? 'is-active' : '']"
          @click="handleDaysChange(7)"
        >7 天</button>
        <button
          :class="['trend-days__btn', days === 14 ? 'is-active' : '']"
          @click="handleDaysChange(14)"
        >14 天</button>
        <button
          :class="['trend-days__btn', days === 30 ? 'is-active' : '']"
          @click="handleDaysChange(30)"
        >30 天</button>
      </div>
    </template>
    <div class="trend-chart-container">
      <VisitorTrendChart :data="data" :loading="loading" />
    </div>
  </DetailCard>
</template>

<script setup>
import { TrendCharts } from "@element-plus/icons-vue"
import DetailCard from "@/components/cards/DetailCard.vue"
import VisitorTrendChart from "@/components/charts/VisitorTrendChart.vue"

const props = defineProps({
  data: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  },
  days: {
    type: Number,
    default: 7
  }
})

const emit = defineEmits(['update:days', 'days-change'])

const handleDaysChange = (newDays) => {
  emit('update:days', newDays)
  emit('days-change', newDays)
}
</script>

<style lang="scss" scoped>
.trend-chart-container {
  width: 100%;
  height: 100%;
  min-height: 200px;
}

.trend-days {
  display: flex;
  gap: 4px;
  padding: 4px;
  border-radius: 12px;
  background: var(--toggle-bg);

  .trend-days__btn {
    padding: 6px 14px;
    border: none;
    border-radius: 8px;
    background: transparent;
    color: var(--text-regular);
    font-size: 13px;
    font-weight: 600;
    cursor: pointer;
    transition: background-color 0.2s ease, color 0.2s ease;

    &:hover {
      background: var(--toggle-hover-bg);
      color: var(--text-primary);
    }

    &.is-active {
      background: var(--chart-line);
      color: var(--toggle-active-text);
    }
  }
}
</style>
