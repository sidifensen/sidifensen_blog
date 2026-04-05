<template>
  <el-container class="layout-container">
    <el-header class="header">
      <CreationHeader />
    </el-header>
    <el-container class="menu">
      <el-aside class="sidebar">
        <el-menu
          class="pc-menu"
          :default-active="activeIndex"
          router
          @select="handleSelect"
          :default-openeds="['/creation/manage']"
        >
          <div class="create">
            <a href="/editor" target="_blank">
              <div class="create-button">
                <el-icon class="create-icon"><Plus /></el-icon>创作
              </div>
            </a>
          </div>
          <el-menu-item index="/" class="menu-item">
            <el-icon><House /></el-icon>
            <span class="menu-text">网站首页</span>
          </el-menu-item>
          <el-menu-item index="/creation" class="menu-item">
            <el-icon><HomeFilled /></el-icon>
            <span class="menu-text">首页</span>
          </el-menu-item>
          <el-sub-menu index="/creation/manage" class="menu-item">
            <template #title>
              <el-icon><Files /></el-icon>
              <span class="menu-text">管理</span>
            </template>
            <el-menu-item index="/creation/articlemanage"> 文章管理 </el-menu-item>
            <el-menu-item index="/creation/columnmanage"> 专栏管理 </el-menu-item>
            <el-menu-item index="/creation/commentmanage"> 评论管理 </el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-aside>
      <el-main class="main">
        <router-view class="router-view"></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>
<script setup>
import CreationHeader from '@/components/CreationHeader.vue'
import { ref, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Plus, House, Files, HomeFilled } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const activeIndex = ref('/creation')

// 计算属性自动同步当前路由
const computedActiveIndex = computed(() => {
  return route.path
})

// 监听computed属性变化，更新activeIndex
watch(
  computedActiveIndex,
  (newPath) => {
    activeIndex.value = newPath
  },
  { immediate: true },
)
</script>
<style lang="scss" scoped>
.layout-container {
  height: 100vh;
  display: flex;
  background: var(--el-bg-color-page);

  .header {
    height: 48px;
    background: var(--el-bg-color);
    border-bottom: 1px solid var(--el-border-color-lighter);
  }

  .menu {
    height: calc(100vh - 48px);

    .sidebar {
      width: 200px;
      height: calc(100vh - 48px);
      background: var(--el-bg-color);
      border-right: 1px solid var(--el-border-color-lighter);

      @media screen and (max-width: 768px) {
        display: none;
      }

      .pc-menu {
        height: 100%;
        background: transparent;

        .create {
          padding: 10px;
          height: 60px;

          .create-button {
            width: 100%;
            border-radius: 10px;
            height: 40px;
            font-size: 20px;
            display: flex;
            align-items: center;
            background-color: var(--el-fill-color-light);
            cursor: pointer;
            transition: background-color 0.2s ease;

            &:hover {
              background-color: var(--el-fill-color);
            }

            .create-icon {
              margin: 0 5px 0 12px;
              color: var(--el-color-primary);
            }
          }
        }

        .menu-text {
          font-size: 18px;
          color: var(--el-text-color-regular);
        }

        .el-menu-item {
          color: var(--el-text-color-regular);

          &:hover {
            background-color: var(--el-fill-color-light);
          }

          &.is-active {
            background-color: var(--el-color-primary-light-9);
            color: var(--el-color-primary);
          }
        }
      }
    }

    .main {
      background: var(--el-bg-color-page);

      @media screen and (max-width: 768px) {
        padding: 10px;
      }

      .router-view {
        height: 100%;
      }
    }
  }
}
</style>
