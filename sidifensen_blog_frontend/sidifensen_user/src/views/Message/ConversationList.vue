<template>
  <div class="conversation-page">
    <div class="container">
      <!-- 页面头部 -->
      <div class="page-header">
        <div class="header-copy">
          <p class="header-copy__eyebrow">Private Message</p>
          <h2 class="header-copy__title">私信</h2>
          <p class="header-copy__description">与好友一对一交流，分享想法与日常。</p>
        </div>
        <div class="header-actions">
          <el-button plain :icon="RefreshRight" @click="handleRefresh">刷新列表</el-button>
          <el-button plain :icon="Select" :disabled="messageStore.totalUnreadCount === 0" @click="handleMarkAllRead">全部已读</el-button>
        </div>
      </div>

      <!-- 统计卡片 -->
      <div class="summary-grid">
        <article class="summary-card">
          <div class="summary-card__label">全部未读</div>
          <div class="summary-card__value">{{ statistics.totalUnread }}</div>
          <div class="summary-card__hint">消息汇总</div>
        </article>
        <article class="summary-card">
          <div class="summary-card__label">会话总数</div>
          <div class="summary-card__value">{{ statistics.conversationCount }}</div>
          <div class="summary-card__hint">已建立</div>
        </article>
        <article class="summary-card">
          <div class="summary-card__label">在线好友</div>
          <div class="summary-card__value">{{ statistics.onlineCount }}</div>
          <div class="summary-card__hint">当前活跃</div>
        </article>
      </div>

      <!-- 会话列表 -->
      <div class="conversation-list" v-loading="loading">
        <!-- 列表工具栏 -->
        <div class="list-toolbar">
          <span class="list-toolbar__meta">会话列表</span>
          <span class="list-toolbar__meta">{{ messageStore.conversationList.length }} 条</span>
        </div>

        <!-- 空状态 -->
        <div v-if="messageStore.conversationList.length === 0 && !loading" class="empty-state">
          <el-empty description="暂无会话" />
        </div>

        <!-- 会话卡片列表 -->
        <div v-else class="conversation-items">
          <article
            v-for="conv in messageStore.conversationList"
            :key="conv.targetUserId"
            class="conversation-item"
            :class="{ unread: conv.unreadCount > 0 }"
            @click="openChat(conv.targetUserId)"
          >
            <!-- 用户头像 -->
            <div class="conversation-item__avatar" @click.stop="goToUserHomepage(conv.targetUserId)">
              <el-avatar :size="50" :src="conv.targetUserAvatar" />
            </div>

            <!-- 会话内容 -->
            <div class="conversation-item__content">
              <div class="conversation-item__top">
                <div class="status-group">
                  <span v-if="conv.isOnline" class="online-pill">在线</span>
                  <span class="time">{{ formatConversationTime(conv.lastMessageTime) }}</span>
                </div>
              </div>
              <div class="conversation-item__nickname">{{ conv.targetUserNickname }}</div>
              <div class="conversation-item__message">{{ conv.lastMessageContent }}</div>
            </div>

            <!-- 未读消息数 -->
            <div v-if="conv.unreadCount > 0" class="conversation-item__badge">
              {{ conv.unreadCount > 99 ? '99+' : conv.unreadCount }}
            </div>

            <!-- 操作按钮 -->
            <div class="conversation-item__actions">
              <el-button
                type="danger"
                :icon="Delete"
                circle
                size="small"
                class="delete-btn"
                @click.stop="handleDelete(conv.targetUserId)"
              />
            </div>
          </article>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from "vue";
import { useRouter } from "vue-router";
import { RefreshRight, Select, Delete } from "@element-plus/icons-vue";
import { ElMessageBox } from "element-plus";
import { getConversationList, deleteConversation, clearUnreadCount } from "@/api/conversation";
import { useMessageStore } from "@/stores/messageStore";
import WebSocketClient from "@/utils/WebSocketClient";
import { formatConversationTime } from "@/utils/formatTime";

const router = useRouter();
const messageStore = useMessageStore();
const loading = ref(false);

// ==================== 统计数据 ====================
// 计算属性：汇总页面统计数据
const statistics = computed(() => ({
  totalUnread: messageStore.totalUnreadCount || 0,      // 全部未读消息数
  conversationCount: messageStore.conversationList.length || 0,  // 会话总数
  onlineCount: messageStore.conversationList.filter(conv => conv.isOnline).length || 0,  // 在线好友数
}));

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

// 刷新会话列表
const handleRefresh = async () => {
  try {
    await fetchConversationList();
    ElMessage.success("刷新成功");
  } catch (error) {
    // 错误已在 fetchConversationList 中记录
  }
};

// 全部标记已读
const handleMarkAllRead = async () => {
  const unreadConversations = messageStore.conversationList.filter((conv) => conv.unreadCount > 0);
  if (unreadConversations.length === 0) {
    ElMessage.info("没有未读消息");
    return;
  }

  try {
    // 遍历所有未读会话，调用清空未读数接口
    await Promise.all(unreadConversations.map((conv) => clearUnreadCount(conv.targetUserId)));
    // 更新本地状态
    unreadConversations.forEach((conv) => {
      messageStore.clearConversationUnread(conv.targetUserId);
    });
    ElMessage.success("已全部标记为已读");
  } catch (error) {
    ElMessage.error("操作失败");
    console.error("标记已读失败:", error);
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
  // 浅色模式默认值
  --bg-page: #f5f7fb;
  --bg-card: #ffffff;
  --bg-soft: #f8fafc;
  --bg-accent: #eef4ff;
  --text-title: #162033;
  --text-primary: #344054;
  --text-secondary: #667085;
  --text-muted: #98a2b3;
  --border: #dbe3ef;
  --border-strong: #c6d2e3;
  --accent: #2f6fec;
  --accent-strong: #1f5bd0;
  --shadow: 0 1px 3px rgba(17, 24, 39, 0.08);
  --shadow-hover: 0 10px 24px rgba(17, 24, 39, 0.08);

  min-height: 100vh;
  padding-top: 100px !important;
  padding-bottom: 48px;
  background:
    radial-gradient(circle at top left, var(--bg-accent), transparent 34%),
    linear-gradient(180deg, var(--bg-page) 0%, var(--bg-soft) 100%);

  .container {
    max-width: 1180px;
    margin: 0 auto;
    padding: 0 20px;

    // 页面头部样式
    .page-header {
      display: flex;
      justify-content: space-between;
      gap: 20px;
      padding: 28px;
      background: var(--bg-card);
      border: 1px solid var(--border);
      border-radius: 24px;
      box-shadow: var(--shadow);
      margin-bottom: 20px;

      .header-copy {
        display: flex;
        flex-direction: column;
        gap: 10px;

        .header-copy__eyebrow {
          margin: 0;
          font-size: 12px;
          font-weight: 700;
          letter-spacing: 0.16em;
          text-transform: uppercase;
          color: var(--accent);
        }

        .header-copy__title {
          margin: 0;
          font-family: "AlimamaShuHei", "Helvetica Neue", Arial, sans-serif;
          font-size: 28px;
          color: var(--text-title);
        }

        .header-copy__description {
          margin: 0;
          max-width: 620px;
          font-size: 14px;
          line-height: 1.7;
          color: var(--text-secondary);
        }
      }

      .header-actions {
        display: flex;
        flex-wrap: wrap;
        gap: 12px;
        align-content: flex-start;

        :deep(.el-button) {
          height: 40px;
          padding: 0 16px;
          border-radius: 999px;
          border-color: var(--border-strong);
          color: var(--text-primary);
          background: var(--bg-soft);
        }
      }
    }

    // 统计卡片样式
    .summary-grid {
      display: grid;
      grid-template-columns: repeat(3, minmax(0, 1fr));
      gap: 14px;
      margin-top: 18px;

      .summary-card {
        padding: 18px;
        background: var(--bg-card);
        border: 1px solid var(--border);
        border-radius: 18px;
        box-shadow: var(--shadow);

        .summary-card__label {
          font-size: 13px;
          color: var(--text-secondary);
        }

        .summary-card__value {
          margin-top: 10px;
          font-size: 26px;
          font-weight: 700;
          color: var(--text-title);
        }

        .summary-card__hint {
          margin-top: 8px;
          font-size: 12px;
          color: var(--text-muted);
        }
      }
    }

    // 会话列表样式
    .conversation-list {
      min-height: 420px;
      margin-top: 18px;
      padding: 24px;
      background: var(--bg-card);
      border: 1px solid var(--border);
      border-radius: 24px;
      box-shadow: var(--shadow);

      // 列表工具栏
      .list-toolbar {
        display: flex;
        justify-content: space-between;
        gap: 12px;
        margin-bottom: 18px;

        .list-toolbar__meta {
          font-size: 13px;
          color: var(--text-muted);
        }
      }

      // 空状态
      .empty-state {
        display: flex;
        align-items: center;
        justify-content: center;
        min-height: 320px;
      }

      // 会话卡片容器
      .conversation-items {
        display: flex;
        flex-direction: column;
        gap: 12px;

        // 会话卡片样式
        .conversation-item {
          display: grid;
          grid-template-columns: 56px minmax(0, 1fr) auto auto;
          gap: 14px;
          padding: 16px;
          border: 1px solid var(--border);
          border-radius: 18px;
          background: var(--bg-card);
          box-shadow: var(--shadow);
          cursor: pointer;
          transition: box-shadow 0.2s ease, border-color 0.2s ease;

          &:hover {
            border-color: var(--border-strong);
            box-shadow: var(--shadow-hover);

            .delete-btn {
              opacity: 1;
            }
          }

          // 未读状态样式
          &.unread {
            background: var(--bg-accent);
          }

          // 用户头像
          .conversation-item__avatar {
            display: flex;
            align-items: flex-start;
            justify-content: center;

            :deep(.el-avatar) {
              border: 1px solid var(--border);
              border-radius: 14px;
            }
          }

          // 会话内容
          .conversation-item__content {
            display: flex;
            flex-direction: column;
            gap: 6px;
            min-width: 0;

            .conversation-item__top {
              .status-group {
                display: flex;
                align-items: center;
                gap: 8px;
              }

              // 在线状态标签
              .online-pill {
                display: inline-flex;
                padding: 2px 10px;
                background: var(--bg-soft);
                border-radius: 999px;
                font-size: 11px;
                font-weight: 700;
                color: var(--accent);
              }

              .time {
                font-size: 12px;
                color: var(--text-muted);
              }
            }

            // 用户昵称
            .conversation-item__nickname {
              font-size: 15px;
              font-weight: 600;
              color: var(--text-title);
            }

            // 最后一条消息
            .conversation-item__message {
              font-size: 13px;
              color: var(--text-secondary);
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
            }
          }

          // 未读消息角标
          .conversation-item__badge {
            display: flex;
            align-items: center;
            min-width: 20px;
            height: 20px;
            padding: 0 6px;
            background: #ef4444;
            color: white;
            border-radius: 10px;
            font-size: 11px;
            font-weight: 700;
          }

          // 操作按钮
          .conversation-item__actions {
            display: flex;
            align-items: center;

            .delete-btn {
              opacity: 0;
              transition: opacity 0.2s ease;
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
}

// 黑夜模式适配
html.dark {
  .conversation-page {
    --bg-page: #0b1220;
    --bg-card: #111a2d;
    --bg-soft: #152238;
    --bg-accent: #132a4e;
    --text-title: #f3f6fc;
    --text-primary: #d7deea;
    --text-secondary: #a7b3c7;
    --text-muted: #8292ac;
    --border: #25344d;
    --border-strong: #355070;
    --accent: #7fb0ff;
    --accent-strong: #a7c7ff;
    --shadow: 0 1px 3px rgba(2, 6, 23, 0.35);
    --shadow-hover: 0 12px 28px rgba(2, 6, 23, 0.34);
  }
}

// 平板端响应式样式
@media (max-width: 900px) {
  .conversation-page {
    .container {
      .page-header {
        flex-direction: column;
      }

      .summary-grid {
        grid-template-columns: 1fr;
      }

      .conversation-list {
        .conversation-items {
          .conversation-item {
            grid-template-columns: 48px minmax(0, 1fr) auto;

            .conversation-item__actions {
              grid-column: 1 / -1;
              justify-content: flex-end;
            }
          }
        }
      }
    }
  }
}

// 移动端响应式样式
@media (max-width: 640px) {
  .conversation-page {
    padding-top: 80px !important;
    padding-bottom: 36px;

    .container {
      padding: 0 14px;

      .page-header {
        padding: 20px;

        .header-copy {
          .header-copy__title {
            font-size: 24px;
          }

          .header-copy__description {
            font-size: 13px;
          }
        }
      }

      .summary-grid {
        .summary-card {
          padding: 14px;

          .summary-card__value {
            font-size: 22px;
          }
        }
      }

      .conversation-list {
        padding: 18px;

        .conversation-items {
          .conversation-item {
            padding: 14px;
            grid-template-columns: 44px minmax(0, 1fr) auto;

            .conversation-item__avatar {
              :deep(.el-avatar) {
                width: 44px;
                height: 44px;
              }
            }

            .conversation-item__content {
              .conversation-item__nickname {
                font-size: 14px;
              }

              .conversation-item__message {
                font-size: 12px;
              }
            }
          }
        }
      }
    }
  }
}
</style>
