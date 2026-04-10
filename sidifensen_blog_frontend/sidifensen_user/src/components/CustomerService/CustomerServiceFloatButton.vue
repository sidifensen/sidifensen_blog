<template>
  <!-- 智能客服悬浮按钮 -->
  <div
    class="customer-service-float-btn"
    :class="{ 'is-dragging': isDragging }"
    :style="{ right: buttonPosition.right + 'px', bottom: buttonPosition.bottom + 'px' }"
    @mousedown="handleMouseDown"
    @mouseup="handleMouseUp"
    @mousemove="handleMouseMove"
    @mouseleave="handleMouseUp"
    @touchstart="handleTouchStart"
    @touchend="handleTouchEnd"
    @touchmove="handleTouchMove"
    @click="handleClick"
  >
    <div class="service-btn">
      <!-- 机器人图标 -->
      <svg
        class="service-icon"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        stroke-width="2"
        stroke-linecap="round"
        stroke-linejoin="round"
      >
        <rect x="3" y="11" width="18" height="10" rx="2" />
        <circle cx="12" cy="5" r="2" />
        <path d="M12 7v4" />
        <line x1="8" y1="16" x2="8" y2="16" />
        <line x1="16" y1="16" x2="16" y2="16" />
      </svg>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

// 按钮位置
const buttonPosition = ref({
  right: 24,
  bottom: 100,
})

// 拖拽状态
const isDragging = ref(false)

// 拖拽起始位置
const dragStart = {
  x: 0,
  y: 0,
}

// 按钮起始位置
const buttonStart = {
  right: 0,
  bottom: 0,
}

// 边缘距离
const EDGE_DISTANCE = 24

// 处理鼠标按下
const handleMouseDown = (e) => {
  isDragging.value = true
  dragStart.x = e.clientX
  dragStart.y = e.clientY
  buttonStart.right = buttonPosition.value.right
  buttonStart.bottom = buttonPosition.value.bottom
  e.preventDefault()
}

// 处理鼠标松开
const handleMouseUp = () => {
  isDragging.value = false
}

// 处理鼠标移动
const handleMouseMove = (e) => {
  if (!isDragging.value) return

  const deltaX = dragStart.x - e.clientX
  const deltaY = dragStart.y - e.clientY

  // 计算新位置
  let newRight = buttonStart.right + deltaX
  let newBottom = buttonStart.bottom + deltaY

  // 边界限制
  const maxRight = window.innerWidth - 60
  const maxBottom = window.innerHeight - 60

  newRight = Math.max(-buttonStart.right + 60, Math.min(maxRight, newRight))
  newBottom = Math.max(-buttonStart.bottom + 60, Math.min(maxBottom, newBottom))

  buttonPosition.value = {
    right: newRight,
    bottom: newBottom,
  }
}

// 处理触摸开始
const handleTouchStart = (e) => {
  if (e.touches.length !== 1) return
  const touch = e.touches[0]
  isDragging.value = true
  dragStart.x = touch.clientX
  dragStart.y = touch.clientY
  buttonStart.right = buttonPosition.value.right
  buttonStart.bottom = buttonPosition.value.bottom
}

// 处理触摸结束
const handleTouchEnd = () => {
  isDragging.value = false
}

// 处理触摸移动
const handleTouchMove = (e) => {
  if (!isDragging.value || e.touches.length !== 1) return
  e.preventDefault()
  const touch = e.touches[0]
  handleMouseMove({ clientX: touch.clientX, clientY: touch.clientY })
}

// 点击事件
const emit = defineEmits(['click'])

const handleClick = () => {
  if (!isDragging.value) {
    emit('click')
  }
}

// 暴露方法给父组件
defineExpose({
  // 可扩展方法
})
</script>

<style lang="scss" scoped>
// 悬浮按钮容器
.customer-service-float-btn {
  position: fixed;
  z-index: 1000;
  cursor: pointer;
  user-select: none;
  transition: opacity 0.2s;

  &.is-dragging {
    cursor: grabbing;

    .service-btn {
      box-shadow: 0 8px 24px var(--shadow);
      transform: scale(1.1);
    }
  }

  &:active:not(.is-dragging) {
    .service-btn {
      transform: scale(0.95);
    }
  }

  // 悬停效果
  &:hover {
    .service-btn {
      box-shadow: 0 8px 24px var(--shadow);
      transform: translateY(-2px);
    }
  }
}

// 按钮样式
.service-btn {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--accent) 0%, var(--accent-hover) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  box-shadow: 0 4px 16px var(--shadow);
  transition: all 0.3s;

  .service-icon {
    width: 26px;
    height: 26px;
  }
}

// 适配移动端
@media (max-width: 768px) {
  .customer-service-float-btn {
    bottom: 100px !important;
    right: 16px !important;
  }

  .service-btn {
    width: 52px;
    height: 52px;

    .service-icon {
      width: 24px;
      height: 24px;
    }
  }
}
</style>
