<template>
  <div class="user-growth-chart">
    <el-skeleton v-if="loading" :rows="6" animated />
    <div v-show="!loading" ref="chartRef" class="chart-container"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch, nextTick } from 'vue'
import { Chart } from '@antv/g2'
import { useDarkStore } from '@/stores/darkStore'
import { storeToRefs } from 'pinia'

const props = defineProps({
  data: {
    type: Array,
    default: () => [],
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
let resizeObserver = null

const renderChart = () => {
  if (!chartRef.value || !props.data?.length) return

  if (chartInstance) {
    chartInstance.destroy()
    chartInstance = null
  }

  const chartData = props.data.map((item) => ({
    date: item.date,
    value: item.userCount || 0,
  }))

  chartInstance = new Chart({
    container: chartRef.value,
    autoFit: true,
    height: 280,
    theme: isDark.value ? 'classicDark' : 'light',
  })

  chartInstance
    .interval()
    .data(chartData)
    .encode('x', 'date')
    .encode('y', 'value')
    // 深蓝色柱状图样式
    .style('fill', '#1d4ed8')
    .style('fillOpacity', 0.85)
    .style('stroke', null)
    .style('lineWidth', 0)
    .scale('x', {
      tickCount: 7,
    })
    .scale('y', {
      min: 0,
      nice: true,
    })
    .axis('x', {
      title: '日期',
      labelFormatter: (v) => {
        const date = new Date(v)
        return `${date.getMonth() + 1}/${date.getDate()}`
      },
    })
    .axis('y', {
      labelFormatter: (v) => (v >= 1000 ? `${(v / 1000).toFixed(1)}k` : v),
      title: '用户数',
      titleTransform: 'rotate(0)',
    })
    .legend(false)
    .tooltip({
      title: '日期',
      items: [
        {
          channel: 'y',
          name: '新增',
          valueFormatter: (v) => `${v} 用户`,
        },
      ],
    })
    .interaction([{ type: 'element-active' }])

  chartInstance.render()
}

const destroyChart = () => {
  if (chartInstance) {
    chartInstance.destroy()
    chartInstance = null
  }
}

const handleResize = () => {
  if (chartInstance) {
    chartInstance.destroy()
    chartInstance = null
  }
  renderChart()
}

onMounted(async () => {
  await nextTick()
  renderChart()
  if (chartRef.value) {
    resizeObserver = new ResizeObserver(() => {
      handleResize()
    })
    resizeObserver.observe(chartRef.value)
  }
})

onBeforeUnmount(() => {
  destroyChart()
  resizeObserver?.disconnect()
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

watch(
  () => props.data,
  () => {
    if (props.data?.length) {
      renderChart()
    }
  },
  { deep: true },
)

watch(isDark, () => {
  renderChart()
})
</script>

<style lang="scss" scoped>
.user-growth-chart {
  width: 100%;
  min-height: 200px;

  .chart-container {
    width: 100%;
    height: 100%;
  }
}
</style>
