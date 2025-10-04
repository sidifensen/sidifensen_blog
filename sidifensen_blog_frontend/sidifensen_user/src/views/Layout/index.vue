<template>
  <Header></Header>
  <router-view class="router-view"></router-view>
</template>

<script setup>
import Header from "@/components/Header.vue";
</script>

<style lang="scss" scoped>
.router-view {
  padding-top: 48px;
  min-height: 100vh;
  background: url("https://bing.img.run/1920x1080.php") no-repeat center center / cover fixed;
  overflow-y: auto;
}
</style>
