<template>
  <div class="not-found-container">
    <div class="not-found-content">
      <h1 class="error-code">404</h1>
      <p class="error-message">页面不存在</p>
      <p class="suggestion">抱歉，您访问的页面不存在或已被删除</p>
      <button class="back-home" @click="goToHome">返回首页</button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from "vue-router";

// 初始化路由
const router = useRouter();

// 返回首页函数
const goToHome = () => {
  router.push("/");
};
</script>

<style lang="scss" scoped>
// 颜色变量定义
$bg-color: #f5f5f5;
$card-bg: #ffffff;
$text-primary: #333333;
$text-secondary: #555555;
$text-tertiary: #777777;
$button-color: #42b983;
$button-hover-color: #3aa876;

// 尺寸变量定义
$card-padding: 40px;
$border-radius: 10px;
$button-padding: 10px 20px;

.not-found-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  width: 100%;
  overflow: hidden; // 防止出现滚动条
  background-color: $bg-color;
}

.not-found-content {
  text-align: center;
  padding: $card-padding;
  background-color: $card-bg;
  border-radius: $border-radius;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  max-width: 500px;
  width: 100%;
  @media screen and (max-width: 768px) {
    max-width: 90%;
  }

  .error-code {
    font-size: 100px;
    font-weight: bold;
    margin: 0;
    color: $text-primary;
  }

  .error-message {
    font-size: 24px;
    color: $text-secondary;
    margin-top: 10px;
  }

  .suggestion {
    font-size: 16px;
    color: $text-tertiary;
    margin-top: 10px;
    margin-bottom: 30px;
  }

  .back-home {
    padding: $button-padding;
    background-color: $button-color;
    color: white;
    border: none;
    border-radius: 4px;
    font-size: 16px;
    cursor: pointer;
    transition: background-color 0.3s;

    &:hover {
      background-color: $button-hover-color;
    }
  }
}
</style>
