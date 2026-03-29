<template>
  <DetailCard title="互动趋势" :icon="ChatLineRound">
    <div class="trend-chart-container">
      <InteractionTrendChart :data="data" :loading="loading" />
    </div>
  </DetailCard>
</template>

<script setup>
import { ChatLineRound } from "@element-plus/icons-vue"
import DetailCard from "@/components/cards/DetailCard.vue"
import InteractionTrendChart from "@/components/charts/InteractionTrendChart.vue"

defineProps({
  data: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  }
})
</script>

<style lang="scss" scoped>
.trend-chart-container {
  width: 100%;
  min-height: 200px;
}
</style>
