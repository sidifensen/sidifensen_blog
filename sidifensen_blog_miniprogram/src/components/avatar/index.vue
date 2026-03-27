<script setup>
import { ref } from 'vue'

const props = defineProps({
  src: {
    type: String,
    default: ''
  },
  size: {
    type: String,
    default: '40px'
  },
  shape: {
    type: String,
    default: 'circle'
  }
})
</script>

<template>
  <view class="custom-avatar" :style="{ width: size, height: size }">
    <image v-if="src" class="avatar-img" :src="src" mode="aspectFill" />
    <text v-else class="avatar-placeholder">U</text>
  </view>
</template>

<style lang="scss" scoped>
.custom-avatar {
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;

  .avatar-img {
    width: 100%;
    height: 100%;
  }

  .avatar-placeholder {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background: var(--color-primary, #0891b2);
    color: #fff;
    font-size: 12px;
  }
}
</style>
