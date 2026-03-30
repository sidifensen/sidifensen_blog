<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import {
  getMessageList,
  getUnreadCount,
  markAsRead,
  markAllAsRead,
  deleteMessage
} from '@/api/message'

const userStore = useUserStore()

const messageList = ref([])
const unreadCount = ref(0)
const loading = ref(false)
const activeTab = ref('all')

const tabs = [
  { key: 'all', name: '全部' },
  { key: 'comment', name: '评论' },
  { key: 'like', name: '点赞' },
  { key: 'follow', name: '关注' }
]

/**
 * 获取未读数量
 */
async function fetchUnreadCount() {
  try {
    const res = await getUnreadCount()
    unreadCount.value = res.data || 0
  } catch (err) {
    console.error('获取未读数失败', err)
  }
}

/**
 * 获取消息列表
 */
async function fetchMessageList() {
  try {
    loading.value = true
    const res = await getMessageList({ type: activeTab.value })
    messageList.value = res.data || res
  } catch (err) {
    console.error('获取消息列表失败', err)
  } finally {
    loading.value = false
  }
}

/**
 * 标记单条已读
 */
async function handleMarkAsRead(messageId) {
  try {
    await markAsRead(messageId)
    fetchMessageList()
    fetchUnreadCount()
  } catch (err) {
    console.error('标记已读失败', err)
  }
}

/**
 * 全部标记已读
 */
async function handleMarkAllAsRead() {
  try {
    await markAllAsRead()
    uni.showToast({ title: '已标记全部已读', icon: 'success' })
    fetchMessageList()
    fetchUnreadCount()
  } catch (err) {
    console.error('标记全部已读失败', err)
  }
}

/**
 * 切换标签
 */
function onTabChange(tab) {
  activeTab.value = tab
  fetchMessageList()
}

/**
 * 点击消息
 */
function onMessageClick(message) {
  if (!message.isRead) {
    handleMarkAsRead(message.id)
  }

  // 根据消息类型跳转
  if (message.type === 'comment' && message.articleId) {
    uni.navigateTo({ url: `/pages/article/article?id=${message.articleId}` })
  } else if (message.type === 'follow' && message.fromUserId) {
    uni.navigateTo({ url: `/pages/user/profile?userId=${message.fromUserId}` })
  }
}

/**
 * 删除消息
 */
function handleDeleteMessage(message) {
  uni.showModal({
    title: '提示',
    content: '确定要删除这条消息吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          await deleteMessage(message.id)
          uni.showToast({ title: '删除成功', icon: 'success' })
          fetchMessageList()
          fetchUnreadCount()
        } catch (err) {
          uni.showToast({ title: '删除失败', icon: 'none' })
        }
      }
    }
  })
}

onMounted(() => {
  if (!userStore.isLoggedIn) {
    uni.navigateTo({ url: '/pages/login/login' })
    return
  }
  fetchUnreadCount()
  fetchMessageList()
})
</script>

<template>
  <view class="message-page">
    <!-- 标签切换 -->
    <view class="tabs">
      <view
        v-for="tab in tabs"
        :key="tab.key"
        class="tab-item"
        :class="{ active: activeTab === tab.key }"
        @click="onTabChange(tab.key)"
      >
        {{ tab.name }}
      </view>
    </view>

    <!-- 清空按钮 -->
    <view v-if="unreadCount > 0" class="action-bar">
      <text class="unread-tip">{{ unreadCount }} 条未读</text>
      <text class="mark-all" @click="handleMarkAllAsRead">全部标为已读</text>
    </view>

    <!-- 消息列表 -->
    <scroll-view class="message-list" scroll-y>
      <view
        v-for="message in messageList"
        :key="message.id"
        class="message-item card"
        :class="{ unread: !message.isRead }"
      >
        <view class="message-main" @click="onMessageClick(message)">
          <view class="message-avatar">
            <u-avatar :src="message.fromUserAvatar" size="80" />
          </view>
          <view class="message-content">
            <view class="message-header">
              <text class="user-name">{{ message.fromUserName }}</text>
              <text class="message-time">{{ message.createTime }}</text>
            </view>
            <view class="message-text">{{ message.content }}</view>
          </view>
          <view v-if="!message.isRead" class="unread-dot"></view>
        </view>
        <view class="message-actions">
          <text class="delete-btn" @click="handleDeleteMessage(message)">删除</text>
        </view>
      </view>

      <view v-if="messageList.length === 0 && !loading" class="empty-state">
        <text class="empty-text">暂无消息</text>
      </view>
    </scroll-view>
  </view>
</template>

<style lang="scss" scoped>
.message-page {
  min-height: 100vh;
  background: var(--u-bg-color);
}

.tabs {
  display: flex;
  background: var(--u-bg-white);
  border-bottom: 1px solid var(--u-border-color);
  padding: 0 var(--spacing-lg);

  .tab-item {
    flex: 1;
    text-align: center;
    padding: var(--spacing-md) 0;
    font-size: 15px;
    color: var(--u-tips-color);
    position: relative;

    &.active {
      color: var(--u-type-primary);
      font-weight: 500;

      &::after {
        content: '';
        position: absolute;
        bottom: 0;
        left: 50%;
        transform: translateX(-50%);
        width: 40px;
        height: 2px;
        background: var(--u-type-primary);
        border-radius: 1px;
      }
    }
  }
}

.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-md) var(--spacing-lg);
  background: var(--u-bg-white);
  border-bottom: 1px solid var(--u-border-color);

  .unread-tip {
    font-size: 14px;
    color: var(--u-type-primary);
  }

  .mark-all {
    font-size: 14px;
    color: var(--u-tips-color);
  }
}

.message-list {
  padding: var(--spacing-lg);
  height: calc(100vh - 100px);

  .message-item {
    display: flex;
    flex-direction: column;
    margin-bottom: var(--spacing-md);
    position: relative;

    &.unread {
      background: var(--u-bg-gray-light);
    }

    .message-main {
      display: flex;
      align-items: flex-start;
      gap: var(--spacing-md);
    }

    .message-content {
      flex: 1;

      .message-header {
        display: flex;
        justify-content: space-between;
        align-items: center;

        .user-name {
          font-size: 14px;
          font-weight: 500;
          color: var(--u-main-color);
        }

        .message-time {
          font-size: 12px;
          color: var(--u-tips-color);
        }
      }

      .message-text {
        font-size: 14px;
        color: var(--u-content-color);
        margin-top: 4px;
        @include text-ellipsis(2);
      }
    }

    .unread-dot {
      width: 8px;
      height: 8px;
      border-radius: 50%;
      background: var(--u-type-error);
      position: absolute;
      top: var(--spacing-md);
      right: var(--spacing-md);
    }

    .message-actions {
      display: flex;
      justify-content: flex-end;
      padding-top: var(--spacing-sm);

      .delete-btn {
        font-size: 12px;
        color: var(--u-type-error);
        padding: 4px 8px;
      }
    }
  }

  .empty-state {
    @include flex-center-column;
    padding: var(--spacing-2xl);
    color: var(--u-tips-color);
  }
}
</style>
