<template>
  <DetailCard title="VIP 统计" :icon="DataAnalysis">
    <VipStatisticsChart :data="data" :loading="loading" />
  </DetailCard>
</template>

<script setup>
import { DataAnalysis } from '@element-plus/icons-vue'
import DetailCard from '@/components/cards/DetailCard.vue'
import VipStatisticsChart from '@/components/charts/VipStatisticsChart.vue'

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
