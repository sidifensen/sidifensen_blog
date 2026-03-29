<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  src: {
    type: String,
    default: ''
  },
  mode: {
    type: String,
    default: 'aspectFill'
  }
})

const currentSrc = ref(props.src)
const hasError = ref(false)

// 监听 src 变化
watch(() => props.src, (newVal) => {
  currentSrc.value = newVal
  hasError.value = false
})

function onError() {
  hasError.value = true
  currentSrc.value = '/static/default-cover.svg'
}
</script>

<template>
  <image
    :src="currentSrc"
    :mode="mode"
    @error="onError"
  />
</template>
