<template>
  <div>主页</div>
</template>

<script setup>
import { info } from "@/api/user";
import { onMounted } from "vue";

onMounted(() => {
  info().then((res) => {
    console.log(res);
  });
});
</script>

<style lang="scss" scoped></style>
