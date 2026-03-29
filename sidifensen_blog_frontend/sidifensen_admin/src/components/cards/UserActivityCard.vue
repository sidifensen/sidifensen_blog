<template>
  <DetailCard title="用户活跃度" :icon="DataAnalysis">
    <UserActivityChart
      :active-user-count="activeUserCount"
      :total-user-count="totalUserCount"
      :loading="loading"
    />
  </DetailCard>
</template>

<script setup>
import { DataAnalysis } from "@element-plus/icons-vue";
import DetailCard from "@/components/cards/DetailCard.vue";
import UserActivityChart from "@/components/charts/UserActivityChart.vue";

defineProps({
  activeUserCount: {
    type: Number,
    default: 0
  },
  totalUserCount: {
    type: Number,
    default: 0
  },
  loading: {
    type: Boolean,
    default: false
  }
});
</script>
