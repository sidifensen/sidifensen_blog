<template>
  <transition name="page-loading">
    <div v-if="visible" class="page-loading">
      <span class="brand">sidifensen</span>
      <div class="dots">
        <span></span>
        <span></span>
        <span></span>
      </div>
      <span class="text">Loading...</span>
    </div>
  </transition>
</template>

<script setup>
defineProps({
  visible: {
    type: Boolean,
    default: false,
  },
})
</script>

<style lang="scss" scoped>
.page-loading {
  position: fixed;
  inset: 0;
  z-index: 9998;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16px;
  background: var(--bg-page, #0f172a);

  .brand {
    font-size: 28px;
    font-weight: 700;
    color: var(--text-primary, #f1f5f9);
    letter-spacing: -0.5px;
    animation: breathe 2s ease-in-out infinite;
  }

  .dots {
    display: flex;
    gap: 6px;

    span {
      width: 6px;
      height: 6px;
      background: var(--text-muted, #64748b);
      border-radius: 50%;
      animation: dotBounce 1.2s ease-in-out infinite;

      &:nth-child(2) {
        animation-delay: 0.2s;
      }

      &:nth-child(3) {
        animation-delay: 0.4s;
      }
    }
  }

  .text {
    color: var(--text-muted, #64748b);
    font-size: 13px;
  }
}

@keyframes breathe {
  0%,
  100% {
    transform: scale(1);
    opacity: 0.7;
  }
  50% {
    transform: scale(1.04);
    opacity: 1;
  }
}

@keyframes dotBounce {
  0%,
  100% {
    transform: translateY(0);
    opacity: 0.5;
  }
  50% {
    transform: translateY(-6px);
    opacity: 1;
  }
}

/* 过渡动画 */
.page-loading-enter-active,
.page-loading-leave-active {
  transition: opacity 200ms ease;
}
.page-loading-enter-from,
.page-loading-leave-to {
  opacity: 0;
}
</style>
