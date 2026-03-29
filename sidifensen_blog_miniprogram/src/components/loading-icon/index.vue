<script setup>
const props = defineProps({
  mode: {
    type: String,
    default: 'circle'
  },
  color: {
    type: String,
    default: '#909399'
  },
  size: {
    type: String,
    default: '24px'
  }
})
</script>

<template>
  <view class="loading-icon">
    <view class="spinner" :style="{
      width: size,
      height: size,
      borderWidth: `2px`,
      borderColor: 'var(--border, #e2e8f0)',
      borderTopColor: props.color || 'var(--color-primary, #0891b2)'
    }"></view>
  </view>
</template>

<style lang="scss" scoped>
.loading-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;

  .spinner {
    border-style: solid;
    border-radius: 50%;
    animation: spin 0.8s linear infinite;
  }
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>
