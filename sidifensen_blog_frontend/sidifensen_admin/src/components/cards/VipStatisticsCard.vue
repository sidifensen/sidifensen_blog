<template>
  <DetailCard title="VIP 统计" :icon="DataAnalysis">
    <VipStatisticsChart :data="data" :loading="loading" />
  </DetailCard>
</template>

<script setup>
import { defineAsyncComponent } from 'vue'
import { DataAnalysis } from '@element-plus/icons-vue'
import DetailCard from '@/components/cards/DetailCard.vue'

// 图表组件懒加载，减少首屏加载时间
const VipStatisticsChart = defineAsyncComponent(() => import('@/components/charts/VipStatisticsChart.vue'))

defineProps({
  data: {
    type: Object,
    default: () => ({}),
  },
  loading: {
    type: Boolean,
    default: false,
  },
})
</script>
