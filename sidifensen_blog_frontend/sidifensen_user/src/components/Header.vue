<template>
  <el-menu :default-active="activeIndex" router class="pc-menu" mode="horizontal" @select="handleSelect" :ellipsis="false" :class="{ hidden: !isVisible }">
    <!-- 移动端菜单按钮 -->
    <div class="mobile-menu-button" @click="toggleMobileMenu">
      <svg-icon name="menu" width="40px" height="40px" />
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
    <el-menu-item index="/vip" class="menu-item">
      <el-icon><Star /></el-icon>
      <span class="menu-text">会员中心</span>
    </el-menu-item>
    <el-menu-item index="/creation" class="menu-item">
      <el-icon><MagicStick /></el-icon>
      <span class="menu-text">创作中心</span>
    </el-menu-item>
    <div class="right">
      <div class="search" @click="handleSearch">
        <el-icon size="29px" color="var(--el-text-color-primary)"><Search /></el-icon>
      </div>
      <div class="message-icon" @click="goToMessage" v-if="user">
        <el-badge :value="messageStore.totalUnreadCount" :max="99" :hidden="messageStore.totalUnreadCount === 0">
          <el-icon size="32px" color="var(--el-text-color-primary)"><ChatDotRound /></el-icon>
        </el-badge>
      </div>
      <div class="notification-icon" @click="goToNotification" v-if="user">
        <el-badge :value="notificationUnreadCount" :max="99" :hidden="notificationUnreadCount === 0">
          <el-icon size="31px" color="var(--el-text-color-primary)"><Bell /></el-icon>
        </el-badge>
      </div>
      <Dark />
      <div v-if="user" class="user-info">
        <div class="user-name-group" @click="goToUserHomepage">
          <el-text size="large" class="nickname">{{ user.nickname }}</el-text>
          <VipBadge v-if="user.isVip" type="header" :glow="true" />
        </div>
        <el-dropdown placement="bottom-end">
          <el-avatar class="user-avatar" v-if="user.avatar" :size="40" :src="user.avatar" />
          <el-avatar class="user-avatar" v-else :size="40" :icon="UserFilled" />
          <template #dropdown>
            <el-dropdown-menu class="user-dropdown-menu">
              <!-- 用户信息卡片 -->
              <div class="user-info-section">
                <!-- 用户名 -->
                <div class="user-name">
                  <div class="name-row">
                    <span class="nickname">{{ user.nickname }}</span>
                    <VipBadge v-if="user.isVip" type="header" />
                  </div>
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
              <el-dropdown-item @click="goToVipCenter" class="action-item">
                <el-icon><Star /></el-icon>
                <span>会员中心</span>
              </el-dropdown-item>
              <el-dropdown-item @click="goToVipArticles" class="action-item">
                <el-icon><Collection /></el-icon>
                <span>会员专区</span>
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
          <el-menu-item index="/link" class="menu-item">
            <el-icon><Link /></el-icon>
            <span class="menu-text">友链</span>
          </el-menu-item>
          <el-menu-item index="/vip" class="menu-item">
            <el-icon><Star /></el-icon>
            <span class="menu-text">会员中心</span>
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
import VipBadge from "./VipBadge.vue";
import { useUserStore } from "@/stores/userStore.js";
import { ref, watch, onMounted, onBeforeUnmount } from "vue";
import { storeToRefs } from "pinia";
import { useRouter } from "vue-router";
import { info } from "@/api/user";
import { UserFilled, User, Setting, SwitchButton, ChatDotRound, Bell, Star, Collection } from "@element-plus/icons-vue";
import { useMessageStore } from "@/stores/messageStore";
import { getUnreadCount } from "@/api/privateMessage";
import { getUnreadNotificationCount } from "@/api/notification";
import WebSocketClient from "@/utils/WebSocketClient";

const userStore = useUserStore();
const messageStore = useMessageStore();
const { user } = storeToRefs(userStore);
const router = useRouter();

// 当前激活的菜单索引
const activeIndex = ref("/");

// 通知未读数量
const notificationUnreadCount = ref(0);

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

const handleSearch = () => {
  router.push("/search");
};

const goToMessage = () => {
  router.push("/message");
};

const goToNotification = () => {
  const isCurrentlyOnNotificationPage = router.currentRoute.value.path === "/notification";

  // 跳转到消息中心
  router.push("/notification");

  // 触发消息中心的刷新事件（如果当前在消息中心页面，则不延迟执行，如果不在消息中心页面，则延迟100ms确保路由跳转完成）
  setTimeout(
    () => {
      window.dispatchEvent(new CustomEvent("refresh-notifications"));
    },
    isCurrentlyOnNotificationPage ? 0 : 100
  );
};

const handleLoginClick = () => {
  // 直接使用路径跳转，更可靠
  router.push("/login");
};

// 获取未读消息数
const fetchUnreadCount = async () => {
  if (user.value) {
    try {
      const res = await getUnreadCount();
      messageStore.totalUnreadCount = res.data || 0;
    } catch (error) {
      // 静默处理
    }
  }
};

// 获取未读通知数量
const fetchNotificationUnreadCount = async () => {
  if (user.value) {
    try {
      const res = await getUnreadNotificationCount();
      const data = res.data;
      // 计算总未读数量
      notificationUnreadCount.value = data.total || 0;
    } catch (error) {
      // 静默处理
    }
  }
};

// 新消息处理器（定义在组件级别，便于移除）
const handleNewMessage = (data) => {
  messageStore.updateConversation(
    data.fromUserId,
    {
      content: data.content,
      createTime: data.createTime,
    },
    data.unreadCount, // 使用后端返回的最新未读数
    {
      nickname: data.fromUserNickname,
      avatar: data.fromUserAvatar,
    }
  );
};

// 消息撤回处理器
const handleMessageRevoked = (data) => {
  // 更新会话列表中的最后一条消息
  messageStore.updateConversationLastMessage(data.fromUserId, data.content || "撤回了一条消息");
};

// 新通知处理器
const handleNewNotification = (data) => {
  // 收到新通知时，重新获取准确的未读数量
  fetchNotificationUnreadCount();
};

// 初始化 WebSocket 连接
const initWebSocket = () => {
  if (!user.value) {
    return;
  }

  // 如果 WebSocket 未连接，则建立连接
  if (!WebSocketClient.isConnected()) {
    WebSocketClient.connect();
  }

  // 移除旧的监听器(避免重复注册)
  WebSocketClient.off("NEW_MESSAGE", handleNewMessage);
  WebSocketClient.off("MESSAGE_REVOKED", handleMessageRevoked);
  WebSocketClient.off("NEW_NOTIFICATION", handleNewNotification);
  // 注册新消息监听器
  WebSocketClient.on("NEW_MESSAGE", handleNewMessage);
  WebSocketClient.on("MESSAGE_REVOKED", handleMessageRevoked);
  WebSocketClient.on("NEW_NOTIFICATION", handleNewNotification);

  // 监听 WebSocket 重连,重新注册监听器
  WebSocketClient.off("open", handleWebSocketOpen);
  WebSocketClient.on("open", handleWebSocketOpen);
};

// WebSocket 连接成功处理器
const handleWebSocketOpen = () => {
  // 重连后重新注册监听器
  WebSocketClient.off("NEW_MESSAGE", handleNewMessage);
  WebSocketClient.off("MESSAGE_REVOKED", handleMessageRevoked);
  WebSocketClient.off("NEW_NOTIFICATION", handleNewNotification);
  WebSocketClient.on("NEW_MESSAGE", handleNewMessage);
  WebSocketClient.on("MESSAGE_REVOKED", handleMessageRevoked);
  WebSocketClient.on("NEW_NOTIFICATION", handleNewNotification);
};

const getUserInfo = async () => {
  const res = await info();
  user.value = res.data;
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

const goToVipCenter = () => {
  router.push("/vip");
};

const goToVipArticles = () => {
  router.push("/vip/articles");
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

// 监听用户登录状态变化，自动初始化 WebSocket
watch(
  () => user.value,
  (newUser) => {
    if (newUser) {
      // 用户已登录，立即初始化 WebSocket
      fetchUnreadCount();
      fetchNotificationUnreadCount();
      initWebSocket();
    } else {
      // 用户已登出，关闭 WebSocket
      WebSocketClient.close();
      // 移除监听器
      WebSocketClient.off("NEW_MESSAGE", handleNewMessage);
      WebSocketClient.off("MESSAGE_REVOKED", handleMessageRevoked);
      WebSocketClient.off("NEW_NOTIFICATION", handleNewNotification);
      WebSocketClient.off("open", handleWebSocketOpen);
      // 重置未读数量
      notificationUnreadCount.value = 0;
    }
  },
  { immediate: true } // 立即执行一次，检查当前用户状态
);

// 监听通知已读事件
const handleNotificationRead = () => {
  // 当用户查看通知页面并标记已读后，刷新未读数量
  fetchNotificationUnreadCount();
};

// 组件挂载时添加监听滚动事件
onMounted(() => {
  window.addEventListener("scroll", handleScroll);
  window.addEventListener("notification-read", handleNotificationRead);
  // 如果 pinia 有 userid 再获取用户信息（添加错误处理，避免 info() 失败时覆盖已有数据）
  if (user.value) {
    getUserInfo().catch(() => {});
  }
});

// 组件销毁时移除监听事件
onBeforeUnmount(() => {
  window.removeEventListener("scroll", handleScroll);
  window.removeEventListener("notification-read", handleNotificationRead);
  // 移除 WebSocket 监听器
  WebSocketClient.off("NEW_MESSAGE", handleNewMessage);
  WebSocketClient.off("MESSAGE_REVOKED", handleMessageRevoked);
  WebSocketClient.off("NEW_NOTIFICATION", handleNewNotification);
  WebSocketClient.off("open", handleWebSocketOpen);
});
</script>

<style lang="scss" scoped>
.pc-menu {
  height: 56px;
  width: 100%;
  padding: 0 20px 0 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  position: fixed;
  top: env(safe-area-inset-top, 0px);
  left: 0;
  right: 0;
  z-index: 1000;
  transition: transform 0.3s ease, background-color 0.3s ease, border-color 0.3s ease;
  border: none;
  border-bottom: 1px solid var(--el-border-color);

  /* 毛玻璃效果 */
  background: rgba(var(--el-bg-color), 0.85);
  backdrop-filter: blur(12px);

  --el-menu-bg-color: transparent;
  --el-menu-hover-bg-color: var(--el-fill-color-light);

  &.hidden {
    transform: translateY(-100%);
  }

  .logo {
    margin-right: 32px;
    font-weight: bold;
    white-space: nowrap;
    text-decoration: none;

    /* Logo文字 - 简洁设计 */
    .logo-text {
      font-size: 22px !important;
      font-weight: 700;
      color: var(--el-text-color-primary);
      letter-spacing: -0.5px;
      transition: color 0.2s ease;

      &:hover {
        color: var(--el-color-primary);
      }
    }
  }

  .menu-item {
    padding: 0 12px !important;
    transition: color 0.2s ease;

    .menu-text {
      font-size: 15px;
      margin-left: 6px;
      font-weight: 500;
    }
  }

  /* 头部右侧内容 */
  .right {
    display: flex;
    margin-left: auto;
    justify-content: center;
    align-items: center;
    gap: 4px;

    .search {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 36px;
      height: 36px;
      border-radius: 8px;
      cursor: pointer;
      transition: background-color 0.2s ease;

      &:hover {
        background-color: var(--el-fill-color-light);
      }
    }

    .message-icon {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 36px;
      height: 36px;
      border-radius: 8px;
      cursor: pointer;
      transition: background-color 0.2s ease;

      &:hover {
        background-color: var(--el-fill-color-light);
      }

      // 调整徽章大小和位置
      :deep(.el-badge__content) {
        font-size: 11px;
        top: 4px;
        right: 6px;
        border: none;
      }
    }

    .notification-icon {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 36px;
      height: 36px;
      border-radius: 8px;
      cursor: pointer;
      transition: background-color 0.2s ease;

      &:hover {
        background-color: var(--el-fill-color-light);
      }

      // 调整徽章大小和位置
      :deep(.el-badge__content) {
        font-size: 11px;
        top: 4px;
        right: 6px;
        border: none;
      }
    }

    .user-info {
      display: flex;
      align-items: center;
      margin-left: 8px;

      .user-avatar {
        cursor: pointer;
        transition: transform 0.2s ease, box-shadow 0.2s ease;
        border: 2px solid transparent;

        &:hover {
          transform: scale(1.05);
          border-color: var(--el-color-primary-light-5);
        }
      }

      .user-name-group {
        display: flex;
        align-items: center;
        gap: 8px;
        margin-left: 12px;
        margin-right: 4px;
        cursor: pointer;

        .nickname {
          font-size: 14px !important;
          font-weight: 500;
          color: var(--el-text-color-regular);
          transition: color 0.2s ease;

          &:hover {
            color: var(--el-text-color-primary);
          }
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
      height: 32px;
      padding: 0 16px;
      margin-left: 8px;
      background-color: var(--el-color-primary);
      color: #fff;
      font-size: 14px;
      font-weight: 500;
      border-radius: 16px;
      cursor: pointer;
      transition: background-color 0.2s ease, transform 0.2s ease;

      &:hover {
        background-color: var(--el-color-primary-light-3);
        transform: translateY(-1px);
      }
    }
  }

  // 移动端菜单按钮
  .mobile-menu-button {
    display: none; // 默认隐藏，电脑端不显示
    cursor: pointer;
    width: 36px;
    height: 36px;
    align-items: center;
    justify-content: center;
    border-radius: 8px;
    transition: background-color 0.2s ease;

    &:hover {
      background-color: var(--el-fill-color-light);
    }

    // 确保 SVG 图标垂直居中
    :deep(svg) {
      display: block;
    }
  }
}

.user-dropdown-menu {
  min-width: 200px !important;
  max-width: 260px !important;
  padding: 8px !important;
  border: 1px solid var(--el-border-color) !important;
  border-radius: 12px !important;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08) !important;

  // 用户信息区域
  .user-info-section {
    padding: 8px 4px 12px;

    // 用户名区域
    .user-name {
      text-align: center;
      max-width: 250px;

      .name-row {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 8px;

        .nickname {
          font-size: 16px;
          font-weight: 600;
          color: var(--el-text-color-primary);
          word-wrap: break-word;
          word-break: break-all;
        }
      }
    }

    // 统计数据区域
    .user-stats {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 40px;
      padding: 12px 0;

      .stat-item {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 2px;

        .stat-number {
          font-size: 18px;
          font-weight: 700;
          color: var(--el-text-color-primary);
        }

        .stat-label {
          font-size: 12px;
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
    gap: 10px;
    padding: 10px 12px;
    margin: 2px 0;
    border-radius: 8px;
    transition: background-color 0.15s ease;
    cursor: pointer;

    &:hover {
      background-color: var(--el-fill-color-light);
    }

    .el-icon {
      font-size: 16px;
      color: var(--el-text-color-secondary);
    }

    span {
      font-size: 14px;
      color: var(--el-text-color-regular);
    }
  }
}

// 移动端菜单覆盖层
.mobile-menu-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.4);
  z-index: 999;
  display: flex;

  .mobile-menu {
    width: 200px;
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: flex-start;
    padding-top: 60px;
    background-color: var(--el-bg-color);
    border-radius: 0 16px 16px 0;

    .el-menu-item {
      height: 52px;
      padding-left: 20px !important;

      .menu-text {
        font-size: 15px;
        margin-left: 10px;
      }
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
    padding: 0 12px 0 12px;
    .menu-item {
      display: none; // 隐藏PC端菜单
    }
    .mobile-menu-button {
      display: flex; // 显示移动端菜单按钮
      margin-left: 4px;
    }
    .logo {
      display: none; // 手机端隐藏 logo
    }
    .right {
      .user-info {
        margin-right: 4px;
      }
    }
  }
}
</style>
