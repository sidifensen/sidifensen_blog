<template>
  <div class="loading-container">
    <div class="spinner"></div>
    <div class="loading-text">{{ text }}</div>
  </div>
</template>

<script setup>
import { defineProps } from "vue";

// 定义组件props
const props = defineProps({
  // 加载文本，默认为'加载中...'
  text: {
    type: String,
    default: "加载中...",
  },
  // 加载动画颜色
  color: {
    type: String,
    default: "#409eff",
  },
  // 加载动画大小
  size: {
    type: String,
    default: "medium", // small, medium, large
  },
  // 最小高度
  minHeight: {
    type: String,
    default: "300px",
  },

});
</script>

<style lang="scss" scoped>
// 加载动画容器
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  width: 100%;
  min-height: v-bind("minHeight"); //最小高度
  // 旋转动画
  .spinner {
    // 根据size属性设置不同的大小
    width: v-bind('size === "small" ? "30px" : size === "medium" ? "50px" : "70px"');
    height: v-bind('size === "small" ? "30px" : size === "medium" ? "50px" : "70px"');
    border: 5px solid #f3f3f3;
    border-top: 5px solid v-bind("color");
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin-bottom: 15px;
  }

  // 加载文本
  .loading-text {
    font-size: v-bind('size === "small" ? "14px" : size === "medium" ? "18px" : "22px"');
    color: #606266;
  }

  // 旋转动画定义
  @keyframes spin {
    0% {
      transform: rotate(0deg);
    }
    100% {
      transform: rotate(360deg);
    }
  }
}

/* 外部容器居中样式指南:
   要使 LoadingAnimation 组件在外部容器中垂直水平居中，外部容器需要设置:
   1. display: flex;
   2. align-items: center;
   3. justify-content: center;
   4. 明确的高度 (如 height: 100%; 或具体像素值)

   示例:
   .outer-container {
     display: flex;
     align-items: center;
     justify-content: center;
     height: 300px; // 具体高度根据需求设置 
     width: 100%;
   }
*/
</style>
