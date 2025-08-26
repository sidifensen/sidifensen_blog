<template>
  <Header></Header>
  <router-view class="router-view"></router-view>
</template>

<script setup>
import Header from '@/components/Header.vue';

</script>

<style lang="scss" scoped>
.router-view {
  margin-top: 48px;
  height: calc(100vh - 48px);
}
</style>
