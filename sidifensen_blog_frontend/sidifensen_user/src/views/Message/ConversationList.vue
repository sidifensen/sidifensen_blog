<template>
  <div class="conversation-page">
    <div class="container">
      <div class="conversation-container">
        <!-- 页面标题 -->
        <div class="page-header">
          <div class="page-title-wrapper">
            <h2 class="page-title">私信</h2>
            <span class="unread-badge" v-if="messageStore.totalUnreadCount > 0">
              {{ messageStore.totalUnreadCount }}
            </span>
          </div>
        </div>

        <!-- 会话列表 -->
        <div class="conversation-list" v-loading="loading">
          <div v-for="conv in messageStore.conversationList" :key="conv.id" class="conversation-item" @click="openChat(conv.targetUserId)">
            <!-- 用户头像 -->
            <div class="avatar-wrapper" @click.stop="goToUserHomepage(conv.targetUserId)">
              <el-avatar :size="50" :src="conv.targetUserAvatar" />
              <span v-if="conv.isOnline" class="online-status"></span>
            </div>

            <!-- 会话信息 -->
            <div class="conversation-info">
              <div class="conversation-header">
                <span class="nickname">{{ conv.targetUserNickname }}</span>
                <span class="time">{{ formatConversationTime(conv.lastMessageTime) }}</span>
              </div>
              <div class="conversation-content">
                <span class="last-message">{{ conv.lastMessageContent }}</span>
                <el-badge v-if="conv.unreadCount > 0" :value="conv.unreadCount" :max="99" class="unread-badge" />
              </div>
            </div>

            <!-- 删除按钮 -->
            <el-button type="danger" :icon="Delete" circle size="small" class="delete-btn" @click.stop="handleDelete(conv.targetUserId)" />
          </div>

          <!-- 空状态 -->
          <el-empty v-if="messageStore.conversationList.length === 0 && !loading" description="暂无会话" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from "vue";
import { useRouter } from "vue-router";
import { Delete } from "@element-plus/icons-vue";
import { getConversationList, deleteConversation } from "@/api/conversation";
import { useMessageStore } from "@/stores/messageStore";
import WebSocketClient from "@/utils/WebSocketClient";
import { formatConversationTime } from "@/utils/formatTime";

const router = useRouter();
const messageStore = useMessageStore();
const loading = ref(false);

// 获取会话列表
const fetchConversationList = async () => {
  try {
    loading.value = true;
    const res = await getConversationList();
    messageStore.setConversationList(res.data.data);
  } catch (error) {
    console.error("获取会话列表失败:", error);
  } finally {
    loading.value = false;
  }
};

// 打开聊天窗口
const openChat = (userId) => {
  router.push(`/message/chat/${userId}`);
};

// 跳转到用户主页
const goToUserHomepage = (userId) => {
  router.push(`/user/${userId}`);
};

// 删除会话
const handleDelete = async (userId) => {
  try {
    await ElMessageBox.confirm("确定要删除该会话吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    await deleteConversation(userId);
    messageStore.removeConversation(userId);
  } catch (error) {
    if (error !== "cancel") {
      console.error("删除会话失败:", error);
    }
  }
};

// 新消息处理器（如果会话列表中没有这个用户，重新获取会话列表）
const handleNewMessage = (data) => {
  // Header.vue 已经更新了 messageStore,这里不需要再次调用 updateConversation
  // 只需要检查是否是新会话，如果是则重新获取会话列表
  const hasConversation = messageStore.conversationList.some((conv) => conv.targetUserId === data.fromUserId);

  if (!hasConversation) {
    fetchConversationList();
  }
};

// 处理用户在线状态变化
const handleUserOnlineStatus = (data) => {
  messageStore.updateUserOnlineStatus(data.userId, data.isOnline);
};

// 组件挂载
onMounted(() => {
  // 如果会话列表为空，才主动获取（避免重复获取）
  // Header.vue 已经在用户登录时获取过一次了
  if (messageStore.conversationList.length === 0) {
    fetchConversationList();
  }

  // 注意：不要在这里初始化 WebSocket，Header.vue 已经全局初始化了
  // 只需要监听新消息事件，用于检测新会话
  WebSocketClient.on("NEW_MESSAGE", handleNewMessage);
  WebSocketClient.on("USER_ONLINE_STATUS", handleUserOnlineStatus);
});

// 组件卸载
onUnmounted(() => {
  // 移除监听器
  WebSocketClient.off("NEW_MESSAGE", handleNewMessage);
  WebSocketClient.off("USER_ONLINE_STATUS", handleUserOnlineStatus);
});
</script>

<style lang="scss" scoped>
.conversation-page {
  min-height: 100vh;
  padding-top: 80px !important;

  // 默认浅色模式 CSS 变量
  --bg-page: #f5f7fa;
  --bg-card: #ffffff;
  --bg-hover: #f5f5f5;
  --border-color: #f0f0f0;
  --text-primary: #1a1a1a;
  --text-secondary: #a1a1aa;
  --text-muted: #71717a;
  --accent-color: #3b82f6;
  --danger-color: #ef4444;
  --success-color: #22c55e;

  .container {
    max-width: 900px;
    margin: 0 auto;
    padding: 0 20px;
  }

  .conversation-container {
    background: var(--bg-card);
    border-radius: 16px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08), 0 1px 2px rgba(0, 0, 0, 0.06);
    overflow: hidden;
    animation: fadeInUp 0.4s ease-out;

    @keyframes fadeInUp {
      from {
        opacity: 0;
        transform: translateY(20px);
      }
      to {
        opacity: 1;
        transform: translateY(0);
      }
    }

    .page-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 24px 24px;
      background: var(--bg-card);
      border-bottom: 1px solid var(--border-color);
      position: sticky;
      top: 0;
      z-index: 10;

      .page-title-wrapper {
        display: flex;
        align-items: center;
        gap: 12px;

        .page-title {
          font-size: 22px;
          font-weight: 700;
          margin: 0;
          color: var(--text-primary);
          letter-spacing: -0.02em;
        }

        .unread-badge {
          min-width: 22px;
          height: 22px;
          padding: 0 8px;
          background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
          color: white;
          border-radius: 11px;
          font-size: 12px;
          font-weight: 600;
          display: flex;
          align-items: center;
          justify-content: center;
          box-shadow: 0 2px 8px rgba(239, 68, 68, 0.3);
          animation: badgePulse 2s ease-in-out infinite;

          @keyframes badgePulse {
            0%, 100% {
              transform: scale(1);
            }
            50% {
              transform: scale(1.05);
            }
          }
        }
      }
    }

    .conversation-list {
      max-height: calc(100vh - 180px);
      overflow-y: auto;

      // 滚动条样式
      &::-webkit-scrollbar {
        width: 6px;
      }

      &::-webkit-scrollbar-track {
        background: transparent;
      }

      &::-webkit-scrollbar-thumb {
        background: var(--border-color);
        border-radius: 3px;

        &:hover {
          background: var(--text-muted);
        }
      }

      .conversation-item {
        display: flex;
        align-items: center;
        padding: 18px 24px;
        cursor: pointer;
        transition: all 0.2s ease;
        position: relative;
        border-bottom: 1px solid var(--border-color);

        &:last-child {
          border-bottom: none;
        }

        &:hover {
          background: var(--bg-hover);

          .delete-btn {
            opacity: 1;
            transform: translateX(0);
          }
        }

        &:active {
          background: var(--border-color);
        }

        .avatar-wrapper {
          position: relative;
          cursor: pointer;
          transition: transform 0.2s ease;
          flex-shrink: 0;

          &:hover {
            transform: scale(1.08);
          }

          :deep(.el-avatar) {
            border-radius: 14px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
            transition: all 0.2s ease;
          }

          .online-status {
            position: absolute;
            bottom: -2px;
            right: -2px;
            width: 14px;
            height: 14px;
            background: var(--success-color);
            border: 3px solid var(--bg-card);
            border-radius: 50%;
            box-shadow: 0 2px 6px rgba(34, 197, 94, 0.4);
            animation: onlinePulse 2s ease-in-out infinite;

            @keyframes onlinePulse {
              0%, 100% {
                opacity: 1;
              }
              50% {
                opacity: 0.7;
              }
            }
          }
        }

        .conversation-info {
          flex: 1;
          margin-left: 16px;
          min-width: 0;
          display: flex;
          flex-direction: column;
          gap: 6px;

          .conversation-header {
            display: flex;
            justify-content: space-between;
            align-items: center;

            .nickname {
              font-size: 16px;
              font-weight: 600;
              color: var(--text-primary);
              letter-spacing: -0.01em;
              transition: color 0.2s ease;
            }

            .time {
              font-size: 12px;
              color: var(--text-secondary);
              font-weight: 500;
              white-space: nowrap;
            }
          }

          .conversation-content {
            display: flex;
            justify-content: space-between;
            align-items: center;
            gap: 12px;

            .last-message {
              font-size: 14px;
              color: var(--text-muted);
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
              max-width: calc(100% - 60px);
              line-height: 1.5;
            }

            .unread-badge {
              flex-shrink: 0;
              min-width: 20px;
              height: 20px;
              padding: 0 6px;
              background: var(--accent-color);
              color: white;
              border-radius: 10px;
              font-size: 11px;
              font-weight: 700;
              display: flex;
              align-items: center;
              justify-content: center;
              box-shadow: 0 2px 6px rgba(59, 130, 246, 0.3);
            }
          }
        }

        .delete-btn {
          margin-left: 12px;
          opacity: 0;
          transform: translateX(-10px);
          transition: all 0.2s ease;
          flex-shrink: 0;

          &:hover {
            transform: scale(1.1);
          }
        }
      }
    }
  }

  // 空状态样式
  :deep(.el-empty) {
    padding: 60px 20px;
  }

  :deep(.el-empty__description) {
    color: var(--text-muted);
  }
}

// 黑夜模式适配 - 使用全局 .dark 类覆盖 CSS 变量
html.dark .conversation-page {
  --bg-page: #0a0a0a;
  --bg-card: #141414;
  --bg-hover: #1a1a1a;
  --border-color: #27272a;
  --text-primary: #f5f5f5;
  --text-secondary: #a1a1aa;
  --text-muted: #71717a;
  --accent-color: #3b82f6;
  --danger-color: #ef4444;
  --success-color: #22c55e;
}
</style>
