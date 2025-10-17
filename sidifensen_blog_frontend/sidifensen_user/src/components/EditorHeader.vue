<template>
  <div class="pc-menu" @select="handleSelect">
    <a class="logo" href="/"><el-text size="large" class="logo-text">sidifensen</el-text></a>
    <a href="/creation"
      ><el-text class="creation-center"
        ><el-icon><ArrowLeftBold /></el-icon></el-text
    ></a>
    <el-text class="publish-article">发布文章</el-text>
    <div class="right">
      <Dark />
      <div v-if="user" class="user-info">
        <el-text size="large" class="nickname">{{ user.nickname }}</el-text>
        <el-dropdown
          :popper-options="{
            modifiers: [
              {
                name: 'offset',
                options: {
                  offset: [-20, 8],
                },
              },
            ],
          }"
        >
          <el-avatar v-if="user.avatar" style="cursor: pointer" :size="40" :src="user.avatar" />
          <el-avatar v-else style="cursor: pointer" :size="40" :icon="UserFilled" />
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="goToProfile">个人主页</el-dropdown-item>
              <el-dropdown-item @click="goToSettings">个人设置</el-dropdown-item>
              <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
      <div class="login" v-else @click="handleLoginClick">登录</div>
    </div>
  </div>
</template>

<script setup>
import Dark from "./Dark.vue";
import { useUserStore } from "@/stores/userStore.js";
import { ref, onMounted } from "vue";
import { storeToRefs } from "pinia";
import { info } from "@/api/user";
import { UserFilled } from "@element-plus/icons-vue";

const userStore = useUserStore();
const { user } = storeToRefs(userStore);

const getUserInfo = async () => {
  const res = await info();
  user.value = res.data.data;
};

const logout = () => {
  userStore.clearUser();
};

// 跳转到登录页面
const handleLoginClick = () => {
  window.location.href = "/login";
};

// 跳转到个人主页
const goToProfile = () => {
  location.href = `/user/${user.value.id}`;
};

// 跳转到个人设置页面
const goToSettings = () => {
  console.log("点击个人设置，准备跳转");
  // 直接使用 location.href
  location.href = "/setting";
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
  z-index: 1000;
  transition: transform 0.5s ease;
  background-color: var(--el-bg-color);
  border: none;

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
    display: flex;
    font-size: 20px;
  }
  .publish-article {
    font-size: 20px;
    margin-left: 10px;
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
        color: var(--el-text-color-primary);
        margin-left: 10px;
        margin-right: 10px;
        transition: all 0.3s ease;

        &:hover {
          color: var(--el-color-primary);
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
}

// 响应式设计 - 移动端
@media (max-width: 870px) {
  .pc-menu {
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
