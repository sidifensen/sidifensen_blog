<template>
  <span
    class="vip-badge"
    :class="[`vip-badge--${type}`, { 'vip-badge--glow': glow }]"
    :style="customStyle"
    @click="handleClick"
    >VIP</span
  >
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'

// VIP 标签组件
// type: simple(简洁样式) | card(卡片样式) | header(头部样式)
// glow: 是否启用发光动画效果
// height: 高度 (默认 22px)
// padding: 内边距 (默认 "0 8px")
const props = defineProps({
  type: {
    type: String,
    default: 'simple',
    validator: (v) => ['simple', 'card', 'header'].includes(v),
  },
  glow: {
    type: Boolean,
    default: false,
  },
  height: {
    type: [Number, String],
    default: 22,
  },
  padding: {
    type: String,
    default: '0 8px',
  },
})

const router = useRouter()

const customStyle = computed(() => ({
  height: typeof props.height === 'number' ? `${props.height}px` : props.height,
  padding: props.padding,
}))

const handleClick = () => {
  router.push('/vip')
}
</script>

<style lang="scss" scoped>
// 基础样式
.vip-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 22px;
  padding: 0 8px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
  line-height: 1;
  white-space: nowrap;
  background: rgba(var(--el-color-warning-rgb, 230, 162, 60), 0.14);
  color: var(--el-color-warning);
  cursor: pointer;
  user-select: none;
}

// 简洁样式（用于卡片等场景）
.vip-badge--simple {
  // 基础样式已包含
}

// 卡片样式（用于用户信息卡片）
.vip-badge--card {
  position: relative;
  background: rgba(var(--el-color-warning-rgb, 230, 162, 60), 0.14);
  border: 1px solid rgba(var(--el-color-warning-rgb, 245, 158, 11), 0.4);
  color: var(--el-color-warning);
  overflow: hidden;
  animation: vip-glow 2s ease-in-out infinite;

  &::after {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.15), transparent);
    transition: left 0.4s ease;
  }

  &:hover::after {
    left: 100%;
  }
}

// 头部样式（用于导航栏用户名称旁）
.vip-badge--header {
  position: relative;
  background: rgba(245, 158, 11, 0.12);
  border: 1px solid rgba(245, 158, 11, 0.4);
  color: #d97706;
  overflow: hidden;

  &::after {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.15), transparent);
    transition: left 0.4s ease;
  }

  &:hover::after {
    left: 100%;
  }
}

// 发光动画（用于头部样式）
.vip-badge--glow {
  animation: vip-glow 2s ease-in-out infinite;
}

@keyframes vip-glow {
  0%,
  100% {
    border-color: rgba(245, 158, 11, 0.3);
    background: rgba(245, 158, 11, 0.08);
    box-shadow: 0 0 4px rgba(245, 158, 11, 0.2);
  }
  50% {
    border-color: rgba(245, 158, 11, 0.8);
    background: rgba(245, 158, 11, 0.18);
    box-shadow: 0 0 12px rgba(245, 158, 11, 0.5);
  }
}
</style>

<style lang="scss">
// 非 scoped 全局样式，处理 dark 模式
.vip-badge--header {
  html.dark & {
    background: rgba(245, 158, 11, 0.15);
    border-color: rgba(245, 158, 11, 0.5);
    color: #fbbf24;
  }
}
</style>
