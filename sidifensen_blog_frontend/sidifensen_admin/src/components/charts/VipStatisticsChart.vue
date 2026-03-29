<template>
  <div class="vip-bullet-chart">
    <el-skeleton v-if="loading" :rows="4" animated />
    <div v-show="!loading" ref="chartRef" class="chart-container"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch } from "vue";
import { Chart } from "@antv/g2";
import { useDarkStore } from "@/stores/darkStore";
import { storeToRefs } from "pinia";

const props = defineProps({
  data: {
    type: Object,
    default: () => ({}),
  },
  loading: {
    type: Boolean,
    default: false,
  },
  targets: {
    type: Object,
    default: () => ({
      vipPercent: 10,     // 目标 VIP 占比 10%
      totalVip: 100,      // 目标 VIP 总数 100
      newVip30Days: 20,   // 目标本月新增 20
    }),
  },
});

const darkStore = useDarkStore();
const { isDark } = storeToRefs(darkStore);

const chartRef = ref(null);
let chartInstance = null;

const renderChart = () => {
  if (!chartRef.value || !props.data) return;

  if (chartInstance) {
    chartInstance.destroy();
    chartInstance = null;
  }

  const vipCount = props.data.totalVipCount || 0;
  const normalCount = props.data.normalUserCount || 0;
  const newVip30Days = props.data.newVipCount30Days || 0;
  const total = vipCount + normalCount;
  const vipPercent = total > 0 ? +((vipCount / total) * 100).toFixed(1) : 0;

  const chart = new Chart({
    container: chartRef.value,
    autoFit: true,
    height: 260,
  });

  // 子弹图数据：实际值 vs 目标值
  const data = [
    { name: "VIP占比", type: "实际", value: vipPercent },
    { name: "VIP占比", type: "目标", value: props.targets.vipPercent },
    { name: "VIP总数", type: "实际", value: vipCount },
    { name: "VIP总数", type: "目标", value: props.targets.totalVip },
    { name: "本月新增", type: "实际", value: newVip30Days },
    { name: "本月新增", type: "目标", value: props.targets.newVip30Days },
  ];

  chart.options({
    type: "interval",
    data,
    encode: {
      x: "name",
      y: "value",
      color: "type",
    },
    transform: [{ type: "dodgeX" }],
    coordinate: { transform: [{ type: "transpose" }] },
    scale: {
      color: {
        domain: ["实际", "目标"],
        title: null,
      },
      y: { nice: true, title: null },
      value: { title: null },
    },
    legend: {
      color: {
        position: "bottom",
        title: null,
      },
    },
    animate: { enter: { type: "waveIn", duration: 1000 } },
    axis: {
      x: { title: null, tick: false },
      y: { domain: [0, 1], tick: false, title: null },
    },
    tooltip: {
      items: [
        (d) => ({
          name: "VIP数",
          value: d.value + (d.color === "实际" && d.x === "VIP占比" ? "%" : ""),
        }),
      ],
    },
    theme: isDark.value ? "classicDark" : "classic",
  });

  chart.render();
  chartInstance = chart;
};

const destroyChart = () => {
  if (chartInstance) {
    chartInstance.destroy();
    chartInstance = null;
  }
};

onMounted(() => {
  renderChart();
});

onBeforeUnmount(() => {
  destroyChart();
});

watch(
  () => props.loading,
  async () => {
    if (!props.loading) {
      await new Promise((resolve) => setTimeout(resolve, 0));
      renderChart();
    }
  }
);

watch(isDark, () => {
  renderChart();
});
</script>

<style lang="scss" scoped>
.vip-bullet-chart {
  width: 100%;
  min-height: 200px;

  .chart-container {
    width: 100%;
    height: 100%;
  }
}
</style>
