<template>
  <el-menu :default-active="activeIndex" class="el-menu" mode="horizontal" @select="handleSelect" :ellipsis="false">
    <router-link class="logo" to="/"><el-text size="large">斯蒂芬森的博客</el-text></router-link>
    <el-menu-item index="1">
      <router-link to="/">
        <el-icon><HomeFilled /></el-icon>首页
      </router-link>
    </el-menu-item>
    <el-menu-item index="2">
      <el-icon><Message /></el-icon>文章
    </el-menu-item>
    <el-menu-item index="3">
      <router-link to="/album">
        <el-icon><Picture /></el-icon>相册
      </router-link>
    </el-menu-item>
    <el-sub-menu index="4">
      <template #title>
        <el-icon><Files /></el-icon>归档
      </template>
      <el-menu-item index="4-1">
        <el-icon> <DocumentCopy /></el-icon>分类
      </el-menu-item>
      <el-menu-item index="4-2">
        <el-icon><PriceTag /></el-icon>标签
      </el-menu-item>
      <el-menu-item index="4-3">
        <el-icon> <Clock /> </el-icon>时间轴
      </el-menu-item>
    </el-sub-menu>
    <el-sub-menu index="5">
      <template #title>
        <el-icon><Menu /></el-icon>其他
      </template>
      <el-menu-item index="5-1">
        <el-icon><Fries /></el-icon>树洞
      </el-menu-item>
      <el-menu-item index="5-2">
        <el-icon><Postcard /></el-icon>留言板
      </el-menu-item>
      <el-menu-item index="5-3">
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
      <Dark />
      <div v-if="user.nickname">
        <span style="line-height: 40px">
          {{ user.nickname }}
        </span>
        <el-dropdown>
          <span>
            <el-avatar style="margin-left: 10px; margin-right: 20px" :size="40" :src="user.avatar" />
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item>个人设置</el-dropdown-item>
              <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
      <div class="login" v-else>
        <router-link to="/account">登录</router-link>
      </div>
    </div>
  </el-menu>
</template>

<script setup>
import Dark from "./Dark.vue";
import { useUserStore } from "@/stores/userStore.js";
import { ref } from "vue";
import { storeToRefs } from "pinia";

const userStore = useUserStore();
const { user } = storeToRefs(userStore);

import { RemoveJwt } from "@/utils/Auth";
import { info } from "@/api/user";
// info()

const logout = () => {
  RemoveJwt();
  user.value = {};
};
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

.el-dropdown {
  border: none;
}
</style>
