<template>
  <div class="user-activity-chart">
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

const darkStore = useDarkStore()
const { isDark } = storeToRefs(darkStore)

const chartRef = ref(null)
let chartInstance = null

const renderChart = () => {
  if (!chartRef.value) return

  if (chartInstance) {
    chartInstance.destroy()
    chartInstance = null
  }

  const activeCount = props.activeUserCount || 0
  const inactiveCount = (props.totalUserCount || 0) - activeCount
  const total = props.totalUserCount || 0

  const data = [
    { item: '活跃用户', count: activeCount },
    { item: '非活跃用户', count: inactiveCount },
  ].filter((d) => d.count >= 0)

  chartInstance = new Chart({
    container: chartRef.value,
    autoFit: true,
  })

  chartInstance.options({
    type: 'interval',
    data,
    coordinate: { type: 'theta', innerRadius: 0.6, outerRadius: 0.95 },
    transform: [{ type: 'stackY' }],
    encode: {
      y: 'count',
      color: 'item',
    },
    theme: isDark.value ? 'classicDark' : 'light',
    legend: {
      color: {
        position: 'bottom',
        itemLabelFill: isDark.value ? '#e2e8f0' : '#475569',
      },
    },
    labels: [
      {
        text: 'item',
        position: 'outside',
        fontSize: 12,
        fill: isDark.value ? '#e2e8f0' : '#475569',
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
          name: '用户数',
          value: datum.count,
        }),
        (datum) => ({
          name: '占比',
          value: `${total > 0 ? ((datum.count / total) * 100).toFixed(2) : 0}%`,
        }),
      ],
    },
    scale: {
      color: {
        range: ['#3b82f6', '#94a3b8'],
      },
    },
    animate: { enter: { type: 'waveIn', duration: 800 } },
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
.user-activity-chart {
  width: 100%;
  height: 100%;
  min-height: 240px;

  .chart-container {
    width: 100%;
    height: 100%;
  }
}
</style>
