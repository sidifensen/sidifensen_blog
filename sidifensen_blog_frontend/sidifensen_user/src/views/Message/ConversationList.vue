<template>
  <div class="conversation-page">
    <div class="container">
      <div class="conversation-container">
        <!-- 页面标题 -->
        <div class="page-header">
          <h2 class="page-title">私信</h2>
          <span class="unread-badge" v-if="messageStore.totalUnreadCount > 0">
            {{ messageStore.totalUnreadCount }}
          </span>
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
  background: transparent;
  min-height: 100vh;
  padding-top: 70px !important;

  .container {
    max-width: 800px;
    margin: 0 auto;
    padding: 0 10px;
  }

  .conversation-container {
    background: var(--el-bg-color);
    border-radius: 8px;
    box-shadow: 0 2px 12px var(--el-border-color-light);
    overflow: hidden;

    .page-header {
      display: flex;
      align-items: center;
      padding: 20px;
      border-bottom: 1px solid var(--el-border-color-light);

      .page-title {
        font-size: 20px;
        font-weight: 600;
        margin: 0;
        color: var(--el-text-color-primary);
      }

      .unread-badge {
        margin-left: 10px;
        padding: 2px 8px;
        background: var(--el-color-danger);
        color: white;
        border-radius: 10px;
        font-size: 12px;
      }
    }

    .conversation-list {
      .conversation-item {
        display: flex;
        align-items: center;
        padding: 15px 20px;
        cursor: pointer;
        transition: background 0.3s;
        position: relative;

        &:hover {
          background: var(--el-bg-color-page);

          .delete-btn {
            opacity: 1;
          }
        }

        .avatar-wrapper {
          position: relative;
          cursor: pointer;
          transition: transform 0.3s;

          &:hover {
            transform: scale(1.05);
          }

          .online-status {
            position: absolute;
            bottom: 2px;
            right: 2px;
            width: 12px;
            height: 12px;
            background: var(--el-color-success);
            border: 2px solid white;
            border-radius: 50%;
          }
        }

        .conversation-info {
          flex: 1;
          margin-left: 15px;
          min-width: 0;

          .conversation-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 5px;

            .nickname {
              font-size: 16px;
              font-weight: 600;
              color: var(--el-text-color-primary);
            }

            .time {
              font-size: 12px;
              color: var(--el-text-color-secondary);
            }
          }

          .conversation-content {
            display: flex;
            justify-content: space-between;
            align-items: center;

            .last-message {
              font-size: 14px;
              color: var(--el-text-color-regular);
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
              max-width: 400px;
            }
          }
        }

        .delete-btn {
          margin-left: 15px;
          opacity: 0;
          transition: opacity 0.3s;
        }
      }
    }
  }
}
</style>
