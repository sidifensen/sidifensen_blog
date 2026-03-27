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
    <view class="spinner"></view>
  </view>
</template>

<style lang="scss" scoped>
.loading-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;

  .spinner {
    width: 24px;
    height: 24px;
    border: 2px solid var(--border, #e2e8f0);
    border-top-color: var(--color-primary, #0891b2);
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
