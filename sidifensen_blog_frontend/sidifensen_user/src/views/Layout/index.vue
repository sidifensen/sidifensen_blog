<template>
  <Header></Header>
  <router-view class="router-view"></router-view>
</template>

<script setup>
import Header from "@/components/Header.vue";
</script>

<style lang="scss" scoped>
.router-view {
  // margin-top: 48px;
  padding-top: 48px;
  min-height: calc(100vh - 48px);
  background: url("@/assets/img/th.jpg") no-repeat center center / cover fixed;
  overflow-y: auto;
}
</style>
