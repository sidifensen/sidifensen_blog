<script setup>
const props = defineProps({
  name: {
    type: String,
    required: true
  },
  color: {
    type: String,
    default: '#999'
  },
  size: {
    type: String,
    default: '20px'
  }
})

// 简单图标映射，使用 Unicode 字符或 SVG
const icons = {
  'arrow-right': '\u203A',
  'crown': '\u265B',
  'file-text': '\u2630',
  'star': '\u2605',
  'photo': '\u2600',
  'bell': '\u266A',
  'setting': '\u2699',
  'search': '\u26B2'
}

const icon = icons[props.name] || props.name
</script>

<template>
  <text class="icon" :style="{ color: color, fontSize: size }">{{ icon }}</text>
</template>

<style lang="scss" scoped>
.icon {
  font-family: sans-serif;
  font-weight: bold;
}
</style>
