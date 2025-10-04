<template>
  <el-menu :default-active="activeIndex" router class="pc-menu" mode="horizontal" @select="handleSelect" :ellipsis="false" :class="{ hidden: !isVisible }">
    <!-- 移动端菜单按钮 -->
    <div class="mobile-menu-button" @click="toggleMobileMenu">
      <svg-icon name="menu" width="50px" height="50px" cursor="pointer" />
    </div>
    <router-link class="logo" to="/"><el-text size="large" class="logo-text">sidifensen</el-text></router-link>
    <el-menu-item index="/" class="menu-item">
      <el-icon><House /></el-icon>
      <span class="menu-text">首页</span>
    </el-menu-item>
    <el-menu-item index="/article" class="menu-item">
      <el-icon><Message /></el-icon>
      <span class="menu-text">文章</span>
    </el-menu-item>
    <el-menu-item index="/album" class="menu-item">
      <el-icon><Picture /></el-icon>
      <span class="menu-text">相册</span>
    </el-menu-item>
    <el-menu-item index="/link" class="menu-item">
      <el-icon><Link /></el-icon>
      <span class="menu-text">友链</span>
    </el-menu-item>
    <el-menu-item index="/creation" class="menu-item">
      <el-icon><MagicStick /></el-icon>
      <span class="menu-text">创作中心</span>
    </el-menu-item>
    <div class="right">
      <div class="search">
        <el-icon size="29px" color="#3d92eb"><Search /></el-icon>
      </div>
      <Dark />
      <div v-if="user" class="user-info">
        <el-text size="large" class="nickname">{{ user.nickname }}</el-text>
        <el-dropdown placement="bottom-end">
          <el-avatar v-if="user.avatar" style="cursor: pointer" :size="40" :src="user.avatar" />
          <el-avatar v-else style="cursor: pointer" :size="40" :icon="UserFilled" />
          <template #dropdown>
            <el-dropdown-menu class="user-dropdown-menu">
              <!-- 用户信息卡片 -->
              <div class="user-info-section">
                <!-- 用户名 -->
                <div class="user-name">
                  <span class="nickname">{{ user.nickname }}</span>
                </div>

                <!-- 统计数据 -->
                <div class="user-stats">
                  <div class="stat-item">
                    <span class="stat-number">{{ user.fansCount || 0 }}</span>
                    <span class="stat-label">粉丝</span>
                  </div>
                  <div class="stat-item">
                    <span class="stat-number">{{ user.followCount || 0 }}</span>
                    <span class="stat-label">关注</span>
                  </div>
                </div>
              </div>

              <!-- 分割线 -->
              <el-divider style="margin: 12px 0" />

              <!-- 操作按钮 -->
              <el-dropdown-item @click="goToUserHomepage" class="action-item">
                <el-icon><User /></el-icon>
                <span>个人主页</span>
              </el-dropdown-item>
              <el-dropdown-item @click="goToSetting" class="action-item">
                <el-icon><Setting /></el-icon>
                <span>个人设置</span>
              </el-dropdown-item>
              <el-dropdown-item @click="logout" class="action-item">
                <el-icon><SwitchButton /></el-icon>
                <span>退出登录</span>
              </el-dropdown-item>
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
        <el-menu class="mobile-menu" router @click.stop @select="closeMobileMenu">
          <el-menu-item index="/" class="menu-item">
            <el-icon><House /></el-icon>
            <span class="menu-text">首页</span>
          </el-menu-item>
          <el-menu-item index="/article" class="menu-item">
            <el-icon><Message /></el-icon>
            <span class="menu-text">文章</span>
          </el-menu-item>
          <el-menu-item index="/album/square" class="menu-item">
            <el-icon><Picture /></el-icon>
            <span class="menu-text">相册</span>
          </el-menu-item>
          <el-menu-item index="/creation" class="menu-item">
            <el-icon><MagicStick /></el-icon>
            <span class="menu-text">创作中心</span>
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
import { useRoute, useRouter } from "vue-router";
import { info, oauthLogin } from "@/api/user";
import { SetJwt } from "@/utils/Auth";
// 引入ElMessage
import { ElMessage } from "element-plus";
import { UserFilled, User, Setting, SwitchButton } from "@element-plus/icons-vue";

const userStore = useUserStore();
const { user } = storeToRefs(userStore);
const router = useRouter();
const route = useRoute();

// 当前激活的菜单索引
const activeIndex = ref("/");

// 监听路由变化，更新激活的菜单
router.afterEach((to) => {
  // 如果路由路径以 "/album" 开头，则激活相册菜单
  if (to.path.startsWith("/album")) {
    activeIndex.value = "/album";
  } else {
    activeIndex.value = to.path;
  }
});

// 处理菜单选择事件
const handleSelect = (index) => {
  // 对于创作中心路由，使用原生页面跳转以避免白屏问题
  if (index === "/creation") {
    window.location.href = "/creation";
  } else {
    // 对于其他路由，使用普通的push方法
    router.push(index);
  }
};

const handleLoginClick = () => {
  // 根据路由名称跳转
  router.push({ name: "Account" });
};

const oauth = () => {
  const user_name = route.query.user_name;
  const access_token = route.query.access_token;
  const login_type = route.query.login_type;
  // 如果地址中有参数，则进行登录
  if (user_name && access_token) {
    oauthLogin({ type: login_type, username: user_name, password: access_token }).then(async (res) => {
      // 去除url上面的参数
      await router.replace({ query: {} });
      ElMessage.success("登录成功");
      // 将jwt存储到localStorage
      SetJwt(res.data.data);
      info().then((res) => {
        userStore.user = res.data.data;
        router.push({ name: "index" });
      });
    });
  }
};

oauth();

const getUserInfo = async () => {
  const res = await info();
  user.value = res.data.data;
};

const logout = () => {
  userStore.clearUser();
};

// 跳转到用户主页
const goToUserHomepage = () => {
  if (user.value?.id) {
    router.push(`/user/${user.value.id}`);
  }
};

// 跳转到个人设置
const goToSetting = () => {
  router.push("/setting");
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
const closeMobileMenu = (index) => {
  isMobileMenuVisible.value = false;
  handleSelect(index);
};

// 组件挂载时添加监听滚动事件
onMounted(() => {
  window.addEventListener("scroll", handleScroll);
  // 如果pinia有userid再获取用户信息
  if (user.value) {
    getUserInfo();
  }
});

// 组件销毁时移除监听事件
onBeforeUnmount(() => {
  window.removeEventListener("scroll", handleScroll);
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
  border: none;

  /* 90% 透明背景 + 毛玻璃效果 */
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);

  --el-menu-bg-color: transparent;
  --el-menu-hover-bg-color: rgba(0, 0, 0, 0.05);

  &.hidden {
    transform: translateY(-100%);
  }

  .logo {
    margin-right: 10px;
    font-weight: bold;
    white-space: nowrap;

    /* Logo文字 */
    .logo-text {
      font-size: 26px !important;
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

  .menu-item {
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
    .search {
      display: flex;
      margin-right: 20px;
      cursor: pointer;
    }
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
      background-color: var(--el-bg-color);
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

.user-dropdown-menu {
  min-width: 220px !important;
  padding: 15px !important;

  // 用户信息区域
  .user-info-section {
    // 用户名区域
    .user-name {
      text-align: center;

      .nickname {
        font-size: 18px;
        font-weight: 600;
        color: var(--el-text-color-primary);
      }
    }

    // 统计数据区域
    .user-stats {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 60px;
      padding: 16px 0;

      .stat-item {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 4px;

        .stat-number {
          font-size: 20px;
          font-weight: 700;
          color: var(--el-text-color-primary);
        }

        .stat-label {
          font-size: 14px;
          color: var(--el-text-color-secondary);
        }
      }
    }
  }

  // 分割线样式调整
  .el-divider {
    border-color: var(--el-border-color-lighter);
  }

  // 操作按钮样式
  .action-item {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 8px 12px;
    transition: all 0.2s ease;
    cursor: pointer;

    &:hover {
      background-color: var(--el-bg-color-page);
      color: var(--el-color-primary);
    }

    .el-icon {
      font-size: 16px;
    }

    span {
      font-size: 14px;
    }
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
  }
}

// 响应式设计 - 平板及以下尺寸
@media (max-width: 1270px) {
  .pc-menu {
    .menu-item {
      .menu-text {
        display: none; // 隐藏菜单文字
      }
    }
  }
}

// 响应式设计 - 移动端
@media (max-width: 870px) {
  .pc-menu {
    padding: 0 5px 0 0;
    .menu-item {
      display: none; // 隐藏PC端菜单
    }
    .mobile-menu-button {
      display: block; // 显示移动端菜单按钮
    }
    .logo {
      margin-right: 0;
      .logo-text {
        font-size: 20px !important;
      }
    }
    .right {
      .search {
        margin-right: 5px;
      }
      .login {
        margin-left: 0;
      }
    }
  }
}
</style>
