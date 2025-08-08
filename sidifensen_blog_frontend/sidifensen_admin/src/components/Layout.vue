<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside class="sidebar">
      <div class="logo-container">
        <div class="logo-icon"></div>
        <h3 class="logo-text">管理员后台</h3>
      </div>
      <el-menu :default-active="activeMenu" class="el-menu-vertical" background-color="#1e293b" text-color="#cbd5e1" active-text-color="#4ade80" :router="true">
        <template v-for="menu in menus" :key="menu.id">
          <el-sub-menu v-if="menu.children && menu.children.length > 0" :index="menu.path">
            <template #title>
              <el-icon :size="18"><component :is="menu.icon || 'Menu'" /></el-icon>
              <span>{{ menu.name }}</span>
            </template>
            <template v-for="child in menu.children" :key="child.id">
              <el-menu-item :index="child.path">
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
    </el-aside>
    <!-- 主内容区域 -->
    <el-container class="main-content">
      <!-- 顶部导航栏 -->
      <el-header class="header">
        <div class="header-right">
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
</template>

<script setup>
import { ref, computed } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/userStore";
import { ElMessage } from "element-plus";

const userStore = useUserStore();
const router = useRouter();

const menus = computed(() => {
  return userStore.menus;
});

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
      height: 64px;

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
    .el-menu-vertical {
      border-right: none;
      height: calc(100% - 64px);
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
      height: 64px;
      background-color: #ffffff;
      box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
      display: flex;
      justify-content: flex-end;
      align-items: center;
      padding: 0 24px;
      position: relative;
      z-index: 5;

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
            background-color: #f1f5f9;
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
      background-color: #f8fafc;
      overflow-y: auto;
      :deep(.menu-management-container) {
        padding: 0px;
      }
    }
  }

  // 响应式 - 手机端
  @media screen and (max-width: 768px) {
    // 侧边栏默认收起
    .sidebar {
      width: 64px !important;
      position: fixed;
      height: 100vh;

      .logo-container {
        justify-content: center;
        align-items: center;
        padding: 0;
      }

      // 确保logo图标在手机端不被拉伸
      .logo-icon {
        width: 40px !important;
        height: 40px !important;
        margin-right: 0 !important;
        flex-shrink: 0;
      }
    }

    // 隐藏logo文本
    .logo-text {
      display: none;
    }

    // 菜单文本默认隐藏
    .el-menu-vertical {
      .el-menu-item span,
      .el-sub-menu .el-sub-menu__title span {
        display: none;
      }

      // 菜单图标居中
      .el-menu-item,
      .el-sub-menu .el-sub-menu__title {
        display: flex;
        justify-content: center;
        padding: 12px 0;
        .el-icon {
          margin-right: 0;
        }
      }

      // 父菜单的右侧箭头图标
      :deep(.el-sub-menu__icon-arrow) {
        right: 10px;
      }

      // 手机端子菜单不缩进
      .el-sub-menu .el-menu-item {
        padding-left: 0 !important;
      }
    }

    // 主内容区域自适应
    .main-content {
      margin-left: 64px;

      // 顶部导航栏
      .header {
        padding: 0 16px;

        .user-info {
          span {
            // display: none;
          }

          .el-icon {
            margin-right: 0;
          }
        }
      }
    }

    // 侧边栏悬停时展开
    .sidebar:hover {
      width: 240px !important;

      .logo-icon {
        margin-right: 12px !important;
      }

      .logo-text {
        display: block;
      }

      .el-menu-vertical {
        .el-menu-item span,
        .el-sub-menu .el-sub-menu__title span {
          display: inline;
        }

        .el-menu-item,
        .el-sub-menu .el-sub-menu__title {
          justify-content: flex-start;
        }
      }
    }
  }
}
</style>
