<template>
  <!-- svg:图标外层容器节点,内部需要与use标签结合使用 -->
  <svg :style="{ width, height }">
    <!-- xlink:href执行用哪一个图标,属性值务必#icon-图标名字 -->
    <!-- use标签fill属性可以设置图标的颜色 -->
    <use :xlink:href="prefix + name" :fill="color"></use>
  </svg>
</template>

<script setup>
/**
 * SvgIcon SVG 图标组件
 *
 * 功能说明：
 * - 通过 SVG symbol + use 标签的方式引用图标
 * - 支持自定义颜色、大小、前缀
 * - 图标名称通过 name 属性传入
 *
 * 使用方式：
 * ```vue
 * <SvgIcon name="github" width="20px" height="20px" color="#999" />
 * ```
 */

//接受父组件传递过来的参数
defineProps({
  //xlink:href属性值前缀
  prefix: {
    type: String,
    default: '#',
  },
  //提供使用的图标名字
  name: String,
  //接受父组件传递颜色
  color: {
    type: String,
    default: '',
  },
  //接受父组件传递过来的图标的宽度
  width: {
    type: String,
    default: '16px',
  },
  //接受父组件传递过来的图标的高度
  height: {
    type: String,
    default: '16px',
  },
})
</script>

<style lang="scss" scoped></style>
