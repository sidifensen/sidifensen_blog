<template>
  <el-menu :default-active="activeIndex" class="pc-menu" mode="horizontal" @select="handleSelect" :ellipsis="false" :class="{ hidden: !isVisible }">
    <!-- 移动端菜单按钮 -->
    <div class="mobile-menu-button" @click="toggleMobileMenu">
      <svg-icon name="menu" width="50px" height="50px" cursor="pointer" />
    </div>

    <router-link class="logo" to="/"><el-text size="large">斯蒂芬森的博客</el-text></router-link>
    <el-menu-item index="1" class="menu-item">
      <router-link to="/">
        <el-icon><House /></el-icon>
        <span class="menu-text">首页</span>
      </router-link>
    </el-menu-item>
    <el-menu-item index="2" class="menu-item">
      <el-icon><Message /></el-icon>
      <span class="menu-text">文章</span>
    </el-menu-item>
    <el-menu-item index="3" class="menu-item">
      <router-link to="/albumSquare">
        <el-icon><Picture /></el-icon>
        <span class="menu-text">相册</span>
      </router-link>
    </el-menu-item>
    <el-sub-menu index="4" class="menu-item">
      <template #title>
        <el-icon><Files /></el-icon>
        <span class="menu-text">归档</span>
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
    <el-sub-menu index="5" class="menu-item">
      <template #title>
        <el-icon><Menu /></el-icon>
        <span class="menu-text">其他</span>
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
    <el-menu-item index="/link" class="menu-item">
      <el-icon><Link /></el-icon>
      <span class="menu-text">友链</span>
    </el-menu-item>
    <div class="right">
      <div class="search">
        <el-icon size="29px" color="#3d92eb"><Search /></el-icon>
      </div>
      <Dark />
      <div v-if="user.nickname" class="user-info">
        <el-text size="large" class="nickname">{{ user.nickname }}</el-text>
        <el-dropdown>
          <el-avatar style="cursor: pointer" :size="45" :src="user.avatar" />
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

  <!-- 移动端菜单 -->
  <teleport to="body">
    <transition name="slide-fade">
      <div v-show="isMobileMenuVisible" class="mobile-menu-overlay" @click="closeMobileMenu">
        <el-menu class="mobile-menu" router @click.stop @select="closeMobileMenu">
          <el-menu-item index="/" class="menu-item">
            <el-icon><House /></el-icon>
            <span class="menu-text">首页</span>
          </el-menu-item>
          <el-menu-item index="/article" class="menu-item">
            <el-icon><Message /></el-icon>
            <span class="menu-text">文章</span>
          </el-menu-item>
          <el-menu-item index="/albumSquare" class="menu-item">
            <el-icon><Picture /></el-icon>
            <span class="menu-text">相册</span>
          </el-menu-item>
          <el-sub-menu index="/archive" class="menu-item">
            <template #title>
              <el-icon><Files /></el-icon>
              <span class="menu-text">归档</span>
            </template>
            <el-menu-item index="/category">
              <el-icon> <DocumentCopy /></el-icon>分类
            </el-menu-item>
            <el-menu-item index="/tag">
              <el-icon><PriceTag /></el-icon>标签
            </el-menu-item>
            <el-menu-item index="/timeline">
              <el-icon> <Clock /> </el-icon>时间轴
            </el-menu-item>
          </el-sub-menu>
          <el-sub-menu index="/other" class="menu-item">
            <template #title>
              <el-icon><Menu /></el-icon>
              <span class="menu-text">其他</span>
            </template>
            <el-menu-item index="/confession">
              <el-icon><Fries /></el-icon>树洞
            </el-menu-item>
            <el-menu-item index="/message-board">
              <el-icon><Postcard /></el-icon>留言板
            </el-menu-item>
            <el-menu-item index="/about">
              <el-icon><UserFilled /></el-icon>关于
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item index="/link" class="menu-item">
            <el-icon><Link /></el-icon>
            <span class="menu-text">友链</span>
          </el-menu-item>
        </el-menu>
      </div>
    </transition>
  </teleport>
</template>

<script setup>
import Dark from "./Dark.vue";
import { useUserStore } from "@/stores/userStore.js";
import { ref, onMounted, onBeforeUnmount } from "vue";
import { storeToRefs } from "pinia";

const userStore = useUserStore();
const { user } = storeToRefs(userStore);

import { RemoveJwt } from "@/utils/Auth";
import { info } from "@/api/user";

const getUserInfo = async () => {
  const res = await info();
  user.value = res.data.data;
};

const logout = () => {
  RemoveJwt();
  user.value = {};
};

// 头部是否可见
const isVisible = ref(true);
// 上次滚动位置
const lastScrollY = ref(0);

const handleScroll = () => {
  const currentScrollY = window.scrollY;
  // 向下滚动隐藏头部，向上滚动显示头部
  // 如果当前滚动位置大于上次记录的位置，并且当前滚动位置超过100px，则隐藏
  if (currentScrollY > lastScrollY.value && currentScrollY > 100) {
    isVisible.value = false;
    // 如果当前滚动位置小于上次记录的位置（即向上滚动），则显示
  } else if (currentScrollY < lastScrollY.value) {
    isVisible.value = true;
  }
  // 更新上次记录的位置
  lastScrollY.value = currentScrollY;
};

// 移动端菜单是否可见
const isMobileMenuVisible = ref(false);
// 切换移动端菜单
const toggleMobileMenu = () => {
  isMobileMenuVisible.value = !isMobileMenuVisible.value;
};

// 关闭移动端菜单
const closeMobileMenu = () => {
  isMobileMenuVisible.value = false;
};

// 组件挂载时添加监听滚动事件
onMounted(() => {
  window.addEventListener("scroll", handleScroll);
  // 如果pinia有userid再获取用户信息
  if (user.value.id) {
    getUserInfo();
  }
});

// 组件销毁时移除监听事件
onBeforeUnmount(() => {
  window.removeEventListener("scroll", handleScroll);
});
</script>

<style lang="scss" scoped>
.el-menu {
  height: 60px;
  width: 100%;
  padding: 0 10px 0 10px;
  display: flex;
  justify-content: center;
  align-items: center;
  position: fixed;
  top: 0;
  left: 0;
  z-index: 1000;
  transition: transform 0.5s ease;
  background-color: var(--el-bg-color);
  border-bottom: 1px solid var(--el-border-color-light);

  &.hidden {
    transform: translateY(-100%);
  }

  .logo {
    margin-right: 10px;
    font-weight: bold;
    white-space: nowrap;
  }

  .menu-item {
    .menu-text {
      margin-left: 5px;
    }
  }

  /* 头部右侧内容 */
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
    .user-info {
      display: flex;
      align-items: center;
      .nickname {
        margin-left: 10px;
        margin-right: 10px;
        @media (max-width: 930px) {
          margin-left: 0;
          display: none;
        }
      }
    }
    .login {
      display: flex;
      justify-content: center;
      align-items: center;
      width: 45px;
      height: 45px;
      margin-left: 5px;
      background-color: #f0f0f0;
      font-size: 12px;
      border-radius: 50%;
      cursor: pointer;
    }
  }
  // 移动端菜单按钮
  .mobile-menu-button {
    margin-right: 10px;
    display: none;
    cursor: pointer;
  }
}

// 移动端菜单覆盖层
.mobile-menu-overlay {
  position: fixed; // 将遮罩层固定在视窗中，不随页面滚动
  top: 0; // 使遮盖层覆盖整个视窗
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.3);
  z-index: 999;
  display: flex;
  .mobile-menu {
    width: 150px;
    height: 100vh;
    display: flex;
    flex-direction: column;
    align-items: flex-start; // 菜单项水平左对齐
    background-color: var(--el-bg-color);
    .el-menu-item {
      width: 100%;
    }
  }
}

/* 移动端菜单动画 */
// 进入前和离开后的状态（初始和结束状态）
.slide-fade-enter-from,
.slide-fade-leave-to {
  background-color: rgba(0, 0, 0, 0);
  .mobile-menu {
    transform: translateX(-100%); // 菜单在左侧，向右滑入
    opacity: 0;
  }
}

// 进入和离开过程中的动画属性
.slide-fade-enter-active,
.slide-fade-leave-active {
  transition: all 0.5s ease;
  .mobile-menu {
    transition: all 0.5s ease;
  }
}

// 进入后和离开前的状态（目标和初始状态）
.slide-fade-enter-to,
.slide-fade-leave-from {
  background-color: rgba(0, 0, 0, 0.3); // 半透明背景
  .mobile-menu {
    transform: translateX(0); // 菜单在原位
    opacity: 1;
  }
}

// 响应式设计 - 平板及以下尺寸
@media (max-width: 1130px) {
  .pc-menu {
    .menu-item {
      .menu-text {
        display: none; // 隐藏菜单文字
      }
    }
  }
}

// 响应式设计 - 移动端
@media (max-width: 768px) {
  .pc-menu {
    .menu-item {
      display: none; // 隐藏PC端菜单
    }
    .mobile-menu-button {
      display: block; // 显示移动端菜单按钮
    }
    .user-info {
    }
  }
}
</style>
