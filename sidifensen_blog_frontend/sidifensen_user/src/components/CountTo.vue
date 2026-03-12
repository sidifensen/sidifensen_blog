<template>
  <span class="count-to">{{ displayValue }}</span>
</template>

<script setup>
import { ref, computed, watch, onMounted } from "vue";
import { formatCompactNumber } from "@/utils/formatNumber";

// 定义 props
const props = defineProps({
  start: {
    type: Number,
    default: 0,
  },
  end: {
    type: Number,
    default: 0,
  },
  duration: {
    type: Number,
    default: 2000,
  },
  decimals: {
    type: Number,
    default: 0,
  },
  prefix: {
    type: String,
    default: "",
  },
  suffix: {
    type: String,
    default: "",
  },
  separator: {
    type: String,
    default: ",",
  },
  compact: {
    type: Boolean,
    default: false,
  },
});

// 当前显示的值
const currentValue = ref(props.start);

// 格式化显示值
const displayValue = computed(() => {
  if (props.compact) {
    return props.prefix + formatCompactNumber(currentValue.value) + props.suffix;
  }

  const formatted = currentValue.value.toFixed(props.decimals);
  const parts = formatted.split(".");
  parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, props.separator);
  return props.prefix + parts.join(".") + props.suffix;
});

// 动画计数函数
const animate = () => {
  const startTime = Date.now();
  const startValue = props.start;
  const endValue = props.end;
  const duration = props.duration;

  const tick = () => {
    const now = Date.now();
    const progress = Math.min((now - startTime) / duration, 1);

    // 使用 easeOutExpo 缓动函数
    const easeProgress = progress === 1 ? 1 : 1 - Math.pow(2, -10 * progress);

    currentValue.value = startValue + (endValue - startValue) * easeProgress;

    if (progress < 1) {
      requestAnimationFrame(tick);
    } else {
      currentValue.value = endValue;
    }
  };

  requestAnimationFrame(tick);
};

// 监听 end 值变化，重新执行动画
watch(
  () => props.end,
  (newEnd) => {
    if (newEnd !== props.start) {
      currentValue.value = props.start;
      setTimeout(animate, 100);
    }
  },
  { immediate: true }
);

// 组件挂载时执行动画
onMounted(() => {
  if (props.end !== props.start) {
    setTimeout(animate, 100);
  }
});
</script>

<style lang="scss" scoped>
.count-to {
  display: inline-block;
  font-variant-numeric: tabular-nums;
  font-feature-settings: "tnum";
}
</style>
