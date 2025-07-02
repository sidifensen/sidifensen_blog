<template>
  <div>index</div>
</template>

<script setup>
import { info } from '@/api/user';
info().then(res => {
  console.log(res);
});
</script>

<style lang="scss" scoped>
</style>
