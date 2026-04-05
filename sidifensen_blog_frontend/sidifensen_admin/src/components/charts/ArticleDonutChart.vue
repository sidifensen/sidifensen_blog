<template>
  <div class="article-donut-chart">
    <el-skeleton v-if="loading" :rows="4" animated />
    <div v-show="!loading" ref="chartRef" class="chart-container"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch } from 'vue'
import { Chart } from '@antv/g2'
import { useDarkStore } from '@/stores/darkStore'
import { storeToRefs } from 'pinia'

const props = defineProps({
  data: {
    type: Object,
    default: () => ({}),
  },
  loading: {
    type: Boolean,
    default: false,
  },
})

const darkStore = useDarkStore()
const { isDark } = storeToRefs(darkStore)

const chartRef = ref(null)
let chartInstance = null

const renderChart = () => {
  if (!chartRef.value || !props.data) return

  if (chartInstance) {
    chartInstance.destroy()
    chartInstance = null
  }

  // 计算总数
  const total =
    (props.data.publishedCount || 0) +
    (props.data.reviewingCount || 0) +
    (props.data.draftCount || 0) +
    (props.data.garbageCount || 0)

  const data = [
    { item: '已发布', count: props.data.publishedCount || 0 },
    { item: '审核中', count: props.data.reviewingCount || 0 },
    { item: '草稿箱', count: props.data.draftCount || 0 },
    { item: '回收站', count: props.data.garbageCount || 0 },
  ].filter((d) => d.count > 0)

  chartInstance = new Chart({
    container: chartRef.value,
    autoFit: true,
    height: 320,
  })

  chartInstance.options({
    type: 'interval',
    data,
    coordinate: { type: 'theta', outerRadius: 0.9 },
    transform: [{ type: 'stackY' }],
    encode: {
      y: 'count',
      color: 'item',
    },
    theme: isDark.value ? 'classicDark' : 'light',
    legend: { color: { position: 'bottom' } },
    labels: [
      {
        text: 'item',
        position: 'outside',
        fontSize: 12,
      },
      {
        text: (d) => {
          const percent = total > 0 ? ((d.count / total) * 100).toFixed(1) : 0
          return `${percent}%`
        },
        position: 'inside',
        fontSize: 14,
        fontWeight: 'bold',
        fill: '#fff',
      },
    ],
    interaction: {
      elementHoverScale: true,
    },
    tooltip: {
      title: 'item',
      items: [
        (datum) => ({
          name: '占比',
          value: `${total > 0 ? ((datum.count / total) * 100).toFixed(3) : 0}%`,
        }),
        (datum) => ({
          name: '数量',
          value: datum.count,
        }),
      ],
    },
  })

  chartInstance.render()
}

const destroyChart = () => {
  if (chartInstance) {
    chartInstance.destroy()
    chartInstance = null
  }
}

onMounted(() => {
  renderChart()
})

onBeforeUnmount(() => {
  destroyChart()
})

watch(
  () => props.loading,
  async () => {
    if (!props.loading) {
      await new Promise((resolve) => setTimeout(resolve, 0))
      renderChart()
    }
  },
)

watch(isDark, () => {
  renderChart()
})
</script>

<style lang="scss" scoped>
.article-donut-chart {
  width: 100%;
  min-height: 200px;

  .chart-container {
    width: 100%;
    height: 100%;
  }
}
</style>
