<template>
  <el-menu :default-active="activeIndex" router class="pc-menu" mode="horizontal" @select="handleSelect" :ellipsis="false" :class="{ hidden: !isVisible }">
    <!-- 移动端菜单按钮 -->
    <div class="mobile-menu-button" @click="toggleMobileMenu">
      <svg-icon name="menu" width="50px" height="50px" cursor="pointer" />
    </div>
    <a class="logo" href="/"><el-text size="large" class="logo-text">sidifensen</el-text></a>
    <a href="/creation"><el-text  class="creation-center">创作中心</el-text></a>
    <div class="right">
      <Dark />
      <div v-if="user" class="user-info">
        <el-text size="large" class="nickname">{{ user.nickname }}</el-text>
        <el-dropdown>
          <el-avatar v-if="user.avatar" style="cursor: pointer" :size="40" :src="user.avatar" />
          <el-avatar v-else style="cursor: pointer" :size="40" :icon="UserFilled" />
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item>个人设置</el-dropdown-item>
              <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
      <div class="login" v-else @click="handleLoginClick">登录</div>
    </div>
  </el-menu>

  <!-- 移动端菜单 -->
  <teleport to="body">
    <transition name="slide-fade">
      <div v-show="isMobileMenuVisible" class="mobile-menu-overlay" @click="closeMobileMenu">
        <el-menu class="mobile-menu" router @click.stop @select="closeMobileMenu" :default-openeds="['/creation/manage']">
          <el-menu-item index="/creation" class="menu-item">
            <el-icon><HomeFilled /></el-icon>
            <span class="menu-text">首页</span>
          </el-menu-item>
          <el-sub-menu index="/creation/manage" class="menu-item">
            <template #title>
              <el-icon><Management /></el-icon>
              <span class="menu-text">管理</span>
            </template>
            <el-menu-item index="/creation/contentmanage"> 内容管理 </el-menu-item>
            <el-menu-item index="/creation/columnmanage"> 专栏管理 </el-menu-item>
            <el-menu-item index="/creation/commentmanage"> 评论管理 </el-menu-item>
          </el-sub-menu>
        </el-menu>
      </div>
    </transition>
  </teleport>
</template>

<script setup>
import Dark from "./Dark.vue";
import { useUserStore } from "@/stores/userStore.js";
import { ref, onMounted } from "vue";
import { storeToRefs } from "pinia";
import { useRouter } from "vue-router";
import { info } from "@/api/user";
import { UserFilled } from "@element-plus/icons-vue";

const userStore = useUserStore();
const { user } = storeToRefs(userStore);
const router = useRouter();
// 控制header显示状态，默认为true确保header可见
const isVisible = ref(true);

// 当前激活的菜单索引
const activeIndex = ref("/creation");

// 监听路由变化，更新激活的菜单
router.afterEach((to) => {
  activeIndex.value = to.path;
});

// 处理菜单选择事件
const handleSelect = (index) => {
  router.push(index);
};

const handleLoginClick = () => {
  // 根据路由名称跳转
  router.push({ name: "Account" });
};

const getUserInfo = async () => {
  const res = await info();
  user.value = res.data.data;
};

const logout = () => {
  userStore.clearUser();
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

onMounted(() => {
  if (user.value) {
    getUserInfo();
  }
});
</script>

<style lang="scss" scoped>
.pc-menu {
  height: 48px;
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
    /* Logo文字 */
    .logo-text {
      margin-right: 4px;
      font-size: 26px;
      color: #3d92eb;
      text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
      letter-spacing: 1px;
      position: relative;
      transition: all 0.3s ease;
      &::after {
        content: "";
        position: absolute;
        bottom: -5px;
        left: 0;
        width: 100%;
        height: 3px;
        background: linear-gradient(90deg, #3d92eb, #6f42c1);
        border-radius: 3px;
        transform: scaleX(0);
        transform-origin: right;
        transition: transform 0.3s ease;
      }
      &:hover {
        color: #6f42c1;
        text-shadow: 2px 2px 8px rgba(0, 0, 0, 0.2);

        &::after {
          transform: scaleX(1);
          transform-origin: left;
        }
      }
    }
  }
  .creation-center {
    font-size: 20px;
  }

  .menu-item {
    width: 100% !important;
    .menu-text {
      font-size: 18px;
      margin-left: 5px;
    }
  }

  /* 头部右侧内容 */
  .right {
    display: flex;
    margin-left: auto;
    justify-content: center;
    align-items: center;
    .user-info {
      display: flex;
      align-items: center;
      .nickname {
        font-size: 18px !important;
        font-weight: 600;
        color: #6f42c1;
        text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.15);
        margin-left: 10px;
        margin-right: 10px;
        transition: all 0.3s ease;

        &:hover {
          color: #8a6cdd;
          text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.25);
          transform: translateY(-2px);
        }

        @media (max-width: 1314px) {
          margin-left: 0;
          display: none;
        }
      }
    }
    .login {
      display: flex;
      justify-content: center;
      align-items: center;
      width: 40px;
      height: 40px;
      margin-left: 5px;
      background-color: var(--el-border-color-light);
      border: 1px solid var(--el-border-color);
      font-size: 15px;
      border-radius: 50%;
      cursor: pointer;
    }
  }
  // 移动端菜单按钮
  .mobile-menu-button {
    // margin-right: 10px;
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
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: flex-start; // 菜单项水平左对齐
    background-color: var(--el-bg-color);
    .el-menu-item {
      width: 100%;
    }
    :deep(.el-sub-menu) {
      width: 100%;
    }
  }
}

// 响应式设计 - 移动端
@media (max-width: 870px) {
  .pc-menu {
    padding: 0 5px 0 0;
    .mobile-menu-button {
      display: block; // 显示移动端菜单按钮
    }
    .logo {
      margin-right: 0;
      .logo-text {
        font-size: 20px;
      }
      .creation-center {
        font-size: 20px;
      }
    }
    .right {
      .login {
        margin-left: 0;
      }
    }
  }
}
</style>
