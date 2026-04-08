<template>
  <div class="interaction-trend-chart">
    <el-skeleton v-if="loading" :rows="6" animated />
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

/**
 * 转换数据格式为 G2 所需的双轴数据格式
 * 将 { date, commentCount, likeCount, favoriteCount } 转换为多系列数据
 */
const transformData = (data) => {
  const result = []
  data.forEach((item) => {
    if (item.commentCount !== undefined) {
      result.push({ 日期: item.date, 类型: '评论数', 数值: item.commentCount })
    }
    if (item.likeCount !== undefined) {
      result.push({ 日期: item.date, 类型: '点赞数', 数值: item.likeCount })
    }
    if (item.favoriteCount !== undefined) {
      result.push({ 日期: item.date, 类型: '收藏数', 数值: item.favoriteCount })
    }
  })
  return result
}

const renderChart = () => {
  if (!chartRef.value || !props.data?.length) return

  if (chartInstance) {
    chartInstance.destroy()
    chartInstance = null
  }

  const transformedData = transformData(props.data)

  chartInstance = new Chart({
    container: chartRef.value,
    autoFit: true,
    height: 340,
  })

  chartInstance.options({
    data: transformedData,
    xField: '日期',
    yField: '数值',
    seriesField: '类型',
    theme: isDark.value ? 'classicDark' : 'light',
    legend: {
      position: 'bottom',
    },
    color: ['#10b981', '#0ea5e9', '#f59e0b'],
    smooth: true,
    tooltip: {
      title: (d) => d.日期,
      items: [
        {
          channel: 'y',
          valueFormatter: (d) => `${d.数值} 次`,
        },
      ],
    },
    interaction: [{ type: 'tooltip', enable: true }],
  })

  chartInstance.line().encode('x', '日期').encode('y', '数值').encode('color', '类型').encode('shape', 'smooth').style({ lineWidth: 2.5 })

  chartInstance
    .point()
    .encode('x', '日期')
    .encode('y', '数值')
    .encode('color', '类型')
    .encode('shape', 'circle')
    .style({ size: 5, stroke: isDark.value ? '#1a1a1a' : '#ffffff', lineWidth: 2 })
    .axis('y', {
      title: '数值',
      titleTransform: 'rotate(0)',
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
.interaction-trend-chart {
  width: 100%;
  height: 340px;

  .chart-container {
    width: 100%;
    height: 100%;
  }
}
</style>
