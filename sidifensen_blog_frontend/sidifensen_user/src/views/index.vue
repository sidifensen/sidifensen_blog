<template>
  <el-menu :default-active="activeIndex" class="el-menu" mode="horizontal" @select="handleSelect" :ellipsis="false">
    <router-link class="logo" to="/">斯蒂芬森的博客</router-link>
    <el-menu-item index="1">
      <el-icon><HomeFilled /></el-icon>首页
    </el-menu-item>
    <el-sub-menu index="2">
      <template #title>
        <el-icon><Files /></el-icon>归档
      </template>
      <el-menu-item index="2-1">
        <el-icon> <DocumentCopy /></el-icon>分类
      </el-menu-item>
      <el-menu-item index="2-2">
        <el-icon><PriceTag /></el-icon>标签
      </el-menu-item>
      <el-menu-item index="2-3">
        <el-icon> <Clock /> </el-icon>时间轴
      </el-menu-item>
    </el-sub-menu>
    <el-sub-menu index="3">
      <template #title>
        <el-icon><IceCreamRound /></el-icon>其他
      </template>
      <el-menu-item index="3-1">
        <el-icon><Fries /></el-icon>树洞
      </el-menu-item>
      <el-menu-item index="3-2">
        <el-icon><Postcard /></el-icon>留言板
      </el-menu-item>
      <el-menu-item index="3-3">
        <el-icon><UserFilled /></el-icon>关于
      </el-menu-item>
    </el-sub-menu>
    <el-menu-item index="/link">
      <el-icon><Link /></el-icon>友链
    </el-menu-item>
    <div class="right">
      <div class="search">
        <el-icon size="20px" color="#3d92eb"><Search /></el-icon>
      </div>
      <div class="login">
        <router-link to="/login">登录</router-link>
      </div>
    </div>
  </el-menu>
</template>

<script setup>
import { info } from "@/api/user";
info().then((res) => {
  console.log(res);
});
</script>

<style lang="scss" scoped>
.el-menu {
  display: flex;
  align-items: center;
  height: 40px;
  width: 100%;
  padding: 0 10px 0 10px;
  justify-content: center;
  align-items: center;
  .logo {
    margin-right: 10px;
  }
  .right {
    display: flex;
    margin-left: auto;
    justify-content: center;
    align-items: center;
    .search {
      display: flex;
      margin-right: 20px;
      cursor: pointer;
    }
    .login {
      display: flex;
      justify-content: center;
      align-items: center;
      width: 35px;
      height: 35px;
      margin-right: 30px;
      background-color: #f0f0f0;
      font-size: 12px;
      border-radius: 50%;
      cursor: pointer;
    }
  }
}
</style>
