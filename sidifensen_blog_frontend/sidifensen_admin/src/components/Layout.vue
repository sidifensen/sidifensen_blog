<template>
  <el-watermark content="sidifensen_blog">
    <el-container class="layout-container">
      <!-- 侧边栏 -->
      <el-aside class="sidebar">
        <div class="logo-container">
          <div class="logo-icon"></div>
          <h3 class="logo-text">管理员后台</h3>
        </div>
        <!-- pc端菜单 -->
        <el-menu :default-active="activeMenu" class="el-menu-pc" background-color="#1e293b" text-color="#cbd5e1" active-text-color="#4ade80" :router="true">
          <template v-for="menu in menus" :key="menu.id">
            <el-sub-menu v-if="menu.children && menu.children.length > 0" :index="menu.path">
              <template #title>
                <el-icon :size="18"><component :is="menu.icon || 'Menu'" /></el-icon>
                <span>{{ menu.name }}</span>
              </template>
              <template v-for="child in menu.children" :key="child.id">
                <el-sub-menu v-if="child.children && child.children.length > 0" :index="child.path">
                  <template #title>
                    <el-icon :size="18"><component :is="child.icon || 'Menu'" /></el-icon>
                    <span>{{ child.name }}</span>
                  </template>
                  <template v-for="grandchild in child.children" :key="grandchild.id">
                    <el-menu-item :index="grandchild.path">
                      <el-icon :size="16"><component :is="grandchild.icon || 'Menu'" /></el-icon>
                      <span>{{ grandchild.name }}</span>
                    </el-menu-item>
                  </template>
                </el-sub-menu>
                <el-menu-item v-else :index="child.path">
                  <el-icon :size="18"><component :is="child.icon || 'Menu'" /></el-icon>
                  <span>{{ child.name }}</span>
                </el-menu-item>
              </template>
            </el-sub-menu>
            <el-menu-item v-else :index="menu.path">
              <el-icon :size="18"><component :is="menu.icon || 'Menu'" /></el-icon>
              <span>{{ menu.name }}</span>
            </el-menu-item>
          </template>
        </el-menu>
      </el-aside>

      <!-- 移动端菜单 -->
      <transition name="slide-fade">
        <div v-show="isMobileMenuVisible" class="mobile-menu-overlay" @click="closeMobileMenu">
          <el-menu :default-active="activeMenu" class="el-menu-mobile" background-color="#1e293b" text-color="#cbd5e1" active-text-color="#4ade80" :router="true" @click.stop @select="closeMobileMenu">
            <template v-for="menu in menus" :key="menu.id">
              <el-sub-menu v-if="menu.children && menu.children.length > 0" :index="menu.path">
                <template #title>
                  <el-icon :size="18"><component :is="menu.icon || 'Menu'" /></el-icon>
                  <span>{{ menu.name }}</span>
                </template>
                <template v-for="child in menu.children" :key="child.id">
                  <el-sub-menu v-if="child.children && child.children.length > 0" :index="child.path">
                    <template #title>
                      <el-icon :size="16"><component :is="child.icon || 'Menu'" /></el-icon>
                      <span>{{ child.name }}</span>
                    </template>
                    <template v-for="grandchild in child.children" :key="grandchild.id">
                      <el-menu-item :index="grandchild.path">
                        <el-icon :size="16"><component :is="grandchild.icon || 'Menu'" /></el-icon>
                        <span>{{ grandchild.name }}</span>
                      </el-menu-item>
                    </template>
                  </el-sub-menu>
                  <el-menu-item v-else :index="child.path">
                    <el-icon :size="16"><component :is="child.icon || 'Menu'" /></el-icon>
                    <span>{{ child.name }}</span>
                  </el-menu-item>
                </template>
              </el-sub-menu>
              <el-menu-item v-else :index="menu.path">
                <el-icon :size="18"><component :is="menu.icon || 'Menu'" /></el-icon>
                <span>{{ menu.name }}</span>
              </el-menu-item>
            </template>
          </el-menu>
        </div>
      </transition>

      <!-- 主内容区域 -->
      <el-container class="main-content">
        <!-- 顶部导航栏 -->
        <el-header class="header">
          <!-- 移动端菜单按钮 -->
          <div class="mobile-menu-button" @click="toggleMobileMenu">
            <svg-icon name="menu" width="35px" height="35px" cursor="pointer" />
          </div>
          <div class="header-right">
            <Dark />
            <el-dropdown trigger="hover">
              <span class="user-info">
                <el-icon><User /></el-icon>
                <span>{{ user.username }}</span>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>
        <!-- 内容区域 -->
        <el-main class="content">
          <RouterView />
        </el-main>
      </el-container>
    </el-container>
  </el-watermark>
</template>

<script setup>
import { ref, computed } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/userStore";
import { ElMessage } from "element-plus";
import Dark from "./Dark.vue";

const userStore = useUserStore();
const router = useRouter();

const menus = computed(() => {
  return userStore.menus;
});

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

// 当前激活的菜单
const activeMenu = computed(() => {
  return router.currentRoute.value.path;
});

// 用户信息
const user = computed(() => {
  return userStore.user;
});

// 退出登录
const handleLogout = () => {
  userStore.clearUser();
  router.push("/login");
  ElMessage.success("退出登录成功");
};
</script>

<style lang="scss" scoped>
.layout-container {
  height: 100vh;
  display: flex;
  overflow: hidden;

  // 侧边栏样式
  .sidebar {
    width: 240px !important;
    height: 100%;
    background-color: #0f172a;
    color: #e2e8f0;
    transition: width 0.3s ease;
    box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);
    z-index: 999;
    flex-shrink: 0;

    .logo-container {
      display: flex;
      align-items: center;
      padding: 16px;
      height: 60px;

      .logo-icon {
        width: 40px;
        height: 40px;
        background: linear-gradient(135deg, #42b983 0%, #3aa17e 100%);
        border-radius: 50%;
        margin-right: 12px;
        position: relative;
        box-shadow: 0 5px 15px rgba(66, 185, 131, 0.4);

        // 白色圆环
        &::after {
          content: "";
          position: absolute;
          top: 50%;
          left: 50%;
          transform: translate(-50%, -50%);
          width: 20px;
          height: 20px;
          border: 2px solid white;
          border-radius: 50%;
        }
      }

      .logo-text {
        font-size: 18px;
        font-weight: 600;
        color: #f8fafc;
      }
    }

    // 菜单样式
    .el-menu-pc {
      border-right: none;
      height: calc(100% - 60px);
      overflow-y: auto;
      overflow-x: hidden;

      .el-menu-item {
        padding: 12px 20px;
        margin: 4px 0;
        border-radius: 8px;
        transition: all 0.2s ease;

        &:hover {
          background-color: #1e293b;
        }

        &.is-active {
          background-color: rgba(74, 222, 128, 0.1);
        }
      }

      // 子菜单样式
      .el-sub-menu {
        .el-sub-menu__title {
          padding: 12px 20px;
          border-radius: 8px;
          transition: all 0.2s ease;

          &:hover {
            background-color: #1e293b;
          }
        }

        // 子菜单下的菜单项缩进
        .el-menu-item {
          padding-left: 40px !important;
        }

        // 孙子菜单样式
        .el-sub-menu {
          // 孙子菜单下的菜单项额外缩进
          .el-menu-item {
            padding-left: 60px !important;
          }
        }
      }
    }
  }

  // 主内容区域
  .main-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    height: 100%;
    overflow: hidden;

    // 顶部导航栏
    .header {
      height: 60px;
      background-color: var(--el-bg-color);
      border-bottom: 1px solid var(--el-border-color-light);
      box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
      display: flex;
      justify-content: flex-end;
      align-items: center;
      padding: 0 24px;
      position: relative;
      z-index: 1000;

      .mobile-menu-button {
        display: none;
      }

      .header-right {
        display: flex;
        align-items: center;

        .user-info {
          display: flex;
          align-items: center;
          cursor: pointer;
          padding: 8px 12px;
          border-radius: 8px;
          transition: background-color 0.2s ease;

          &:hover {
            background-color: #f1f5f9
          }

          .el-icon {
            margin-right: 8px;
            color: #64748b;
          }
        }
      }
    }

    // 内容区域
    .content {
      flex: 1;
      padding: 10px;
      background-color: var(--el-bg-color);
      overflow-y: auto;
      :deep(.menu-management-container) {
        padding: 0px;
      }
    }
  }

  // 响应式 - 手机端
  @media screen and (max-width: 768px) {
    .mobile-menu-overlay {
      position: fixed; // 将遮罩层固定在视窗中，不随页面滚动
      top: 0; // 使遮盖层覆盖整个视窗
      left: 0;
      right: 0;
      bottom: 0;
      background-color: rgba(0, 0, 0, 0.3);
      z-index: 999;
      display: flex;
      .el-menu-mobile {
        position: absolute;
        border: 0;
        top: 50px;
        background-color: #0f172a;
        height: 100vh;
        display: flex;
        flex-direction: column;
        align-items: flex-start; // 菜单项水平左对齐

        // 移动端子菜单样式
        .el-sub-menu {
          width: 100%;

          // 移动端子菜单下的菜单项缩进
          .el-menu-item {
            padding-left: 40px !important;
          }

          // 移动端孙子菜单样式
          .el-sub-menu {
            // 移动端孙子菜单下的菜单项额外缩进
            .el-menu-item {
              padding-left: 60px !important;
            }
          }
        }
      }
    }

    // 侧边栏默认收起
    .sidebar {
      display: none;
    }

    // 主内容区域自适应
    .main-content {
      // 顶部导航栏
      .header {
        justify-content: space-between;
        padding: 0 16px;
        height: 50px;
        .mobile-menu-button {
          display: block;
        }
        .user-info {
          span {
            // display: none;
          }
        }
      }
    }
  }

  /* 移动端菜单动画 */
  // 进入前和离开后的状态（初始和结束状态）
  .slide-fade-enter-from,
  .slide-fade-leave-to {
    background-color: rgba(0, 0, 0, 0);
    .el-menu-mobile {
      transform: translateX(-100%); // 菜单在左侧，向右滑入
      opacity: 0;
    }
  }

  // 进入和离开过程中的动画属性
  .slide-fade-enter-active,
  .slide-fade-leave-active {
    transition: all 0.5s ease;
    .el-menu-mobile {
      transition: all 0.5s ease;
    }
  }

  // 进入后和离开前的状态（目标和初始状态）
  .slide-fade-enter-to,
  .slide-fade-leave-from {
    background-color: rgba(0, 0, 0, 0.3); // 半透明背景
    .el-menu-mobile {
      transform: translateX(0); // 菜单在原位
      opacity: 1;
    }
  }
}
</style>
