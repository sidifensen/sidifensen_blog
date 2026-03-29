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

// UV UI 图标映射（Unicode 备用字符）
const unicodeIcons = {
  'arrow-right': '\u203A',
  'crown': '\u265B',
  'file-text': '\u2630',
  'star': '\u2605',
  'photo': '\u2600',
  'bell': '\u266A',
  'setting': '\u2699',
  'search': '\u26B2',
  'like': '\u2764',
  'favorite': '\u2605',
  'share': '\u27A4',
  'fabulous': '\u2764',
  'upload': '\u27A4',
  'close-circle': '\u2715',
  'flash': '\u26A1',
  'chat': '\u2709',
  'grid': '\u229E'
}

// 判断是否为 UV UI 图标名称（以 uv- 开头）
const isUvIcon = props.name.startsWith('uv-')
// UV UI 图标 id（去掉 uv- 前缀后用于 symbol id）
const uvIconId = props.name
const icon = isUvIcon ? null : (unicodeIcons[props.name] || props.name)
</script>

<template>
  <!-- UV UI 图标：使用 SVG symbol -->
  <svg v-if="isUvIcon" class="uv-icon-svg" :style="{ color: color, fontSize: size }" aria-hidden="true">
    <use :xlink:href="'#' + uvIconId" />
  </svg>
  <!-- 普通图标：使用 Unicode 字符 -->
  <text v-else class="icon" :style="{ color: color, fontSize: size }">{{ icon }}</text>
</template>

<style lang="scss" scoped>
.icon {
  font-family: sans-serif;
  font-weight: bold;
}

.uv-icon-svg {
  width: 1em;
  height: 1em;
  fill: currentColor;
  vertical-align: -0.1em;
}
</style>
