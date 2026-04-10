<template>
  <DetailCard title="用户活跃度" :icon="DataAnalysis">
    <UserActivityChart :active-user-count="activeUserCount" :total-user-count="totalUserCount" :loading="loading" />
  </DetailCard>
</template>

<script setup>
import { defineAsyncComponent } from 'vue'
import { DataAnalysis } from '@element-plus/icons-vue'
import DetailCard from '@/components/cards/DetailCard.vue'

// 图表组件懒加载
const UserActivityChart = defineAsyncComponent(() => import('@/components/charts/UserActivityChart.vue'))

defineProps({
  activeUserCount: {
    type: Number,
    default: 0,
  },
  totalUserCount: {
    type: Number,
    default: 0,
  },
  loading: {
    type: Boolean,
    default: false,
  },
})
</script>
