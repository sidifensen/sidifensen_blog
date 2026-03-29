<template>
  <DetailCard title="文章状态分布" :icon="Document">
    <div class="trend-chart-container">
      <ArticleDonutChart :data="data" :loading="loading" />
    </div>
  </DetailCard>
</template>

<script setup>
import { Document } from "@element-plus/icons-vue"
import DetailCard from "@/components/cards/DetailCard.vue"
import ArticleDonutChart from "@/components/charts/ArticleDonutChart.vue"

defineProps({
  data: {
    type: Object,
    default: () => ({})
  },
  loading: {
    type: Boolean,
    default: false
  }
})
</script>

<style lang="scss" scoped>
// 移除固定高度，让图表自适应撑满
.trend-chart-container {
  width: 100%;
  min-height: 280px;
}
</style>
