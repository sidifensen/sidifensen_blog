<template>
  <DetailCard title="用户增长趋势" :icon="User">
    <div class="trend-chart-container">
      <UserGrowthChart :data="data" :loading="loading" />
    </div>
  </DetailCard>
</template>

<script setup>
import { defineAsyncComponent } from 'vue'
import { User } from '@element-plus/icons-vue'
import DetailCard from '@/components/cards/DetailCard.vue'

// 图表组件懒加载
const UserGrowthChart = defineAsyncComponent(() => import('@/components/charts/UserGrowthChart.vue'))

defineProps({
  data: {
    type: Array,
    default: () => [],
  },
  loading: {
    type: Boolean,
    default: false,
  },
})
</script>

<style lang="scss" scoped>
.trend-chart-container {
  width: 100%;
}
</style>
