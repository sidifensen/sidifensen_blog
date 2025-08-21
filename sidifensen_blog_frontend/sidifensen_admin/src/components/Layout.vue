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
            <!-- 消息通知 -->
            <div class="message-container" @click.stop="toggleMessageDropdown">
              <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="message-badge-container">
                <el-icon class="message-icon"><Bell /></el-icon>
              </el-badge>
              <!-- 自定义消息下拉框 -->
              <div v-if="isMessageDropdownVisible" class="custom-message-dropdown" ref="messageDropdownRef">
                <div class="message-header">
                  <span class="message-title">消息通知</span>
                  <el-button v-if="hasUnreadMessages" size="small" plain type="success" @click="markAllAsRead">全部标为已读</el-button>
                  <el-button size="small" plain  type="danger" @click="deleteAllMessages">全部删除</el-button>
                </div>
                <div class="message-list" ref="messageListRef">
                  <div v-if="messages.length === 0" class="no-message">
                    <el-icon><Message /></el-icon>
                    <span>暂无消息</span>
                  </div>
                  <div v-for="message in messages" :key="message.id" :data-id="message.id" class="message-item" :class="{ unread: !message.isRead }">
                    <div class="message-content">
                      <h4 class="message-title">{{ message.title }}</h4>
                      <p class="message-desc">{{ message.content }}</p>
                      <p class="message-time">{{ formatTime(message.createTime) }}</p>
                    </div>
                    <div class="message-actions">
                      <el-button v-if="!message.isRead" class="read-button" @click.stop="markAsRead(message.id)">
                        <el-icon><Check /></el-icon> 已读
                      </el-button>
                      <el-button class="delete-button" @click.stop="deleteMessage(message.id)">
                        <el-icon><Delete /></el-icon> 删除
                      </el-button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
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
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/userStore";
import { ElMessage } from "element-plus";
import Dark from "./Dark.vue";
import { getMessagesCount, getMessageList, readAdminMessages, deleteAdminMessages } from "@/api/message";
import { Bell, Message, Delete, User, Check } from "@element-plus/icons-vue";
import { formatTime } from "@/utils/FormatTime";
import { onUnmounted } from "vue";

const userStore = useUserStore();
const router = useRouter();

// 消息相关状态
const unreadCount = ref(0);
const messages = ref([]);
const hasUnreadMessages = computed(() => messages.value.some((msg) => !msg.isRead));
const messageListRef = ref(null);
const isMessageDropdownVisible = ref(false);
const messageDropdownRef = ref(null);

// 获取未读消息数量
const fetchUnreadCount = async () => {
  try {
    const res = await getMessagesCount();
    unreadCount.value = Number(res.data.data);
  } catch (error) {
    ElMessage.error("获取消息数量失败");
    console.error("获取消息数量失败:", error);
  }
};

// 获取消息列表
const fetchMessages = async () => {
  try {
    const res = await getMessageList();
    messages.value = res.data.data.map((msg) => {
      // 解析content字段（JSON字符串）
      let contentObj = {};
      try {
        contentObj = JSON.parse(msg.content);
      } catch (e) {
        console.error("解析消息内容失败:", e);
      }
      return {
        ...msg,
        isRead: msg.isRead === 1,
        content: contentObj.text || msg.content,
      };
    });
  } catch (error) {
    ElMessage.error("获取消息列表失败");
    console.error("获取消息列表失败:", error);
    messages.value = [];
  }
};

// 切换消息下拉框显示状态
const toggleMessageDropdown = async () => {
  isMessageDropdownVisible.value = !isMessageDropdownVisible.value;
  if (isMessageDropdownVisible.value) {
    await fetchMessages();
    // 监听点击事件，点击外部关闭下拉框
    setTimeout(() => {
      document.addEventListener("click", closeMessageDropdown);
    }, 0);
  } else {
    document.removeEventListener("click", closeMessageDropdown);
  }
};

// 关闭消息下拉框
const closeMessageDropdown = (e) => {
  if (messageDropdownRef.value && !messageDropdownRef.value.contains(e.target) && !e.target.closest(".message-badge-container")) {
    isMessageDropdownVisible.value = false;
    document.removeEventListener("click", closeMessageDropdown);
  }
};

// 清理事件监听器
onUnmounted(() => {
  document.removeEventListener("click", closeMessageDropdown);
});

// 标记单条消息为已读
const markAsRead = async (messageId) => {
  try {
    // 确保messageId是整型
    const numericMessageId = parseInt(messageId, 10);
    await readAdminMessages([numericMessageId]);
    // 刷新消息列表
    await fetchMessages();
    // 更新未读数量
    await fetchUnreadCount();
    ElMessage.success("消息已标记为已读");
  } catch (error) {
    ElMessage.error("标记消息为已读失败");
    console.error("标记消息为已读失败:", error);
  }
};

// 标记所有消息为已读
const markAllAsRead = async () => {
  const unreadMessageIds = messages.value.filter((msg) => !msg.isRead).map(({ id }) => id);
  if (unreadMessageIds.length === 0) {
    ElMessage.success("所有消息均已读");
    return;
  }
  try {
    await readAdminMessages(unreadMessageIds);
    // 刷新消息列表
    await fetchMessages();
    // 更新未读数量
    await fetchUnreadCount();
    ElMessage.success("已标记所有消息为已读");
  } catch (error) {
    ElMessage.error("标记消息为已读失败");
    console.error("标记消息为已读失败:", error);
  }
};

// 删除消息
const deleteMessage = async (messageId) => {
  try {
    const numericMessageId = Number(messageId);
    await deleteAdminMessages([numericMessageId]);
    // 刷新消息列表
    await fetchMessages();
    // 更新未读数量
    await fetchUnreadCount();
    ElMessage.success("消息删除成功");
  } catch (error) {
    ElMessage.error("消息删除失败");
    console.error("消息删除失败:", error);
  }
};

// 删除所有消息
const deleteAllMessages = async () => {
  if (messages.value.length === 0) {
    ElMessage.success("暂无消息可删除");
    return;
  }
  try {
    const allMessageIds = messages.value.map(({ id }) => id);
    await deleteAdminMessages(allMessageIds);
    // 刷新消息列表
    await fetchMessages();
    // 更新未读数量
    await fetchUnreadCount();
    ElMessage.success("已删除所有消息");
  } catch (error) {
    ElMessage.error("删除所有消息失败");
    console.error("删除所有消息失败:", error);
  }
};

// 页面加载时获取消息数量
onMounted(() => {
  fetchUnreadCount();
  // 可以添加一个定时器，定期刷新消息数量
  const intervalId = setInterval(fetchUnreadCount, 60000); // 每分钟刷新一次
  // 清理定时器
  onUnmounted(() => clearInterval(intervalId));
});

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

        // 消息容器样式
        .message-container {
          position: relative;
          display: inline-block;
          // 消息徽章容器
          :deep(.message-badge-container) {
            margin-right: 20px;
            cursor: pointer;
            padding: 7px;
            border-radius: 50%;
            transition: all 0.3s ease;
            display: inline-flex;
            &:hover {
              background-color: #f0f9ff;
              transform: scale(1.05);
            }
            .message-icon {
              font-size: 25px;
              color: #3aa17e;
            }
            // 徽章
            .el-badge__content {
              top: 10px;
              right: 20px;
              color: #fff;
              background: #3aa17e;
            }
          }

          // 自定义消息下拉框样式
          .custom-message-dropdown {
            width: 420px;
            max-height: calc(100vh - 150px);
            overflow: hidden;
            border-radius: 12px;
            box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
            border: 1px solid var(--el-border-color);
            position: absolute;
            right: 0;
            top: 100%;
            margin-top: 8px;
            z-index: 1000;
            transform-origin: top right;
            animation: fade-in 0.2s ease-out;

            // 下拉框动画
            @keyframes fade-in {
              from {
                opacity: 0;
                transform: scale(0.95);
              }
              to {
                opacity: 1;
                transform: scale(1);
              }
            }

            // 移动端适配 - 移到最后以避免Sass警告
            @media screen and (max-width: 768px) {
              width: 70vw;
              right: 50%;
              transform: translateX(50%);
            }

            // 消息头部样式
            .message-header {
              display: flex;
              justify-content: space-between;
              align-items: center;
              padding: 14px 20px;
              background-color: var(--el-bg-color);
              border-bottom: 1px solid var(--el-border-color);

              .message-title {
                flex: 1;
                font-size: 16px;
                font-weight: 600;
                color: var(--el-text-color);
              }
            }

            // 消息列表样式
            .message-list {
              max-height: 400px;
              overflow-y: auto;
              overflow-x: hidden;
              padding: 0;
              scrollbar-width: thin;
              scrollbar-color: var(--el-scrollbar-thumb-color) var(--el-scrollbar-track-color);
              .no-message {
                display: flex;
                flex-direction: column;
                align-items: center;
                justify-content: center;
                padding: 40px 0;
                color: #94a3b8;
                .el-icon {
                  font-size: 48px;
                  margin-bottom: 16px;
                  color: #cbd5e1;
                }
                span {
                  font-size: 14px;
                }
              }
              // 消息列表
              .message-item {
                display: flex;
                justify-content: space-between;
                align-items: center;
                padding: 16px 20px;
                cursor: pointer;
                transition: all 0.25s ease;
                position: relative;
                background-color: var(--el-bg-color-overlay);
                border-bottom: 1px solid var(--el-border-color);
                &:hover {
                  background-color: var(--el-border-color-lighter); // 悬停状态使用浅色填充
                }
                &.unread {
                  background-color: var(--el-border-color-light); // 未读状态使用浅色填充
                }
                // 未读消息指示点
                &.unread::before {
                  content: "";
                  position: absolute;
                  left: 10px;
                  top: 50%;
                  transform: translateY(-50%);
                  width: 6px;
                  height: 6px;
                  border-radius: 50%;
                  background-color: #3aa17e;
                }
                .message-content {
                  flex: 1;
                  padding-right: 10px;
                  min-width: 0;
                  .message-title {
                    font-size: 14px;
                    font-weight: 600;
                    color: #1e293b;
                    margin-bottom: 4px;
                    white-space: nowrap;
                    overflow: hidden;
                    text-overflow: ellipsis;
                  }
                  // content内容
                  .message-desc {
                    font-size: 13px;
                    color: #64748b;
                    margin-bottom: 6px;
                    display: -webkit-box;
                    -webkit-line-clamp: 2; // 显示行数
                    -webkit-box-orient: vertical;
                    overflow: hidden;
                  }
                  .message-time {
                    font-size: 12px;
                    color: #94a3b8;
                  }
                }
                // 消息操作按钮
                .message-actions {
                  display: flex;
                  flex-direction: column;
                  align-items: center;
                  gap: 6px;
                  .el-button {
                    min-width: auto;
                    padding: 0 8px;
                    height: 24px;
                    line-height: 24px;
                    font-size: 12px;
                    border-radius: 4px;
                    .el-icon {
                      font-size: 14px;
                      margin-right: 2px;
                    }
                  }
                  .read-button {
                    color: #3aa17e;
                    border-color: #3aa17e;
                    &:hover {
                      background-color: rgba(58, 161, 126, 0.1);
                    }
                  }
                  .delete-button {
                    color: #ef4444;
                    border-color: #ef4444;
                    margin-left: 0;
                    &:hover {
                      background-color: rgba(239, 68, 68, 0.1);
                    }
                  }
                }
              }
            }
          }
        }
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
