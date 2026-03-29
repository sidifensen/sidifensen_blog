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

const hasError = ref(false)

function onError() {
  hasError.value = true
}
</script>

<template>
  <view class="custom-avatar" :style="{ width: size, height: size }">
    <image v-if="props.src && !hasError" class="avatar-img" :src="props.src" mode="aspectFill" @error="onError" />
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
    background: var(--u-type-primary);
    color: #fff;
    font-size: 12px;
  }
}
</style>
