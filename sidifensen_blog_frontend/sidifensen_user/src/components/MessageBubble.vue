<template>
  <div class="message-bubble" :class="{ 'is-mine': isMine }">
    <el-avatar
      :size="40"
      :src="message.fromUserAvatar"
      class="bubble-avatar"
      @click="handleAvatarClick"
    />
    <div class="bubble-content">
      <div class="bubble-header">
        <span class="bubble-nickname">{{ message.fromUserNickname }}</span>
        <span class="bubble-time">{{ getFriendlyTime(message.createTime) }}</span>
      </div>
      <div class="bubble-body">
        <div v-if="message.messageType === 1" class="text-bubble">{{ message.content }}</div>
        <el-image
          v-else-if="message.messageType === 2"
          :src="message.imageUrl"
          class="image-bubble"
          fit="cover"
          :preview-src-list="[message.imageUrl]"
          :initial-index="0"
          preview-teleported
        >
          <template #placeholder>
            <div class="image-loading">加载中...</div>
          </template>
          <template #error>
            <div class="image-error">
              <el-icon><Picture /></el-icon>
              <span>加载失败</span>
            </div>
          </template>
        </el-image>
      </div>
      <div v-if="isMine" class="read-status">
        <span v-if="message.isRead === 1" class="read-text">已读</span>
        <span v-else class="unread-text">未读</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Picture } from '@element-plus/icons-vue'
import { getFriendlyTime } from '@/utils/formatTime'

/**
 * 消息气泡组件
 * 用于在聊天窗口中显示单条消息
 */
const props = defineProps({
  /**
   * 消息对象
   */
  message: {
    type: Object,
    required: true,
    default: () => ({
      id: 0,
      fromUserId: 0,
      fromUserAvatar: '',
      fromUserNickname: '',
      content: '',
      imageUrl: '',
      messageType: 1, // 1文本 2图片
      createTime: '',
      isRead: 0,
    }),
  },
  /**
   * 是否是我的消息
   */
  isMine: {
    type: Boolean,
    default: false,
  },
})

const emit = defineEmits(['avatar-click'])

const handleAvatarClick = () => {
  emit('avatar-click', props.message.fromUserId)
}
</script>

<style lang="scss" scoped>
.message-bubble {
  display: flex;
  margin-bottom: 20px;
  animation: bubbleSlideIn 0.3s ease-out;

  // 默认浅色模式 CSS 变量
  --bg-page: #f5f7fa;
  --bg-message-received: #ffffff;
  --bg-message-sent: #3b82f6;
  --border-color: #f0f0f0;
  --text-primary: #1a1a1a;
  --text-secondary: #52525b;
  --text-muted: #a1a1aa;
  --text-on-accent: #ffffff;
  --accent-color: #3b82f6;
  --success-color: #22c55e;

  @keyframes bubbleSlideIn {
    from {
      opacity: 0;
      transform: translateY(10px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }

  // 我的消息样式 - 右对齐
  &.is-mine {
    flex-direction: row-reverse;

    .bubble-content {
      align-items: flex-end;
      margin-left: 0;
      margin-right: 12px;

      .bubble-header {
        flex-direction: row-reverse;
      }

      .bubble-body {
        background: linear-gradient(135deg, var(--accent-color) 0%, #2563eb 100%);
        color: var(--text-on-accent);
        border-radius: 18px 18px 4px 18px;
        box-shadow: 0 2px 8px rgba(59, 130, 246, 0.25);
        max-width: 70vw; // 气泡最大宽度
        width: max-content; // 气泡宽度自适应内容

        // 图片消息不需要蓝色背景
        &:has(.image-bubble) {
          background: transparent;
          box-shadow: none;
          max-width: 240px; // 图片消息单独限制
        }

        .text-bubble {
          word-break: break-word;
          line-height: 1.5;
          max-width: 100%; // 文字气泡最大占满父元素
        }
      }

      .read-status {
        text-align: right;

        .read-text {
          color: var(--success-color);
          font-weight: 500;
        }

        .unread-text {
          color: var(--text-muted);
        }
      }
    }
  }

  .bubble-avatar {
    flex-shrink: 0;
    cursor: pointer;
    transition: transform 0.2s ease;
    border-radius: 12px;
    margin-top: 10px;

    &:hover {
      transform: scale(1.08);
    }
  }

  .bubble-content {
    display: flex;
    flex-direction: column;
    margin-left: 12px;
    min-width: 0; // 防止flex子项溢出

    .bubble-header {
      display: flex;
      align-items: center;
      gap: 10px;
      margin-bottom: 6px;

      .bubble-nickname {
        font-size: 11px;
        font-weight: 600;
        color: var(--text-secondary);
        flex-shrink: 0; // 防止昵称被压缩
      }

      .bubble-time {
        font-size: 11px;
        color: var(--text-muted);
        font-weight: 500;
        white-space: nowrap; // 时间不换行
        flex-shrink: 0; // 时间不压缩
      }
    }

    .bubble-body {
      padding: 10px 14px;
      border-radius: 18px 18px 18px 4px;
      background: var(--bg-message-received);
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
      transition: all 0.2s ease;
      max-width: 70vw; // 气泡最大宽度
      width: max-content; // 气泡宽度自适应内容

      &:hover {
        box-shadow: 0 2px 6px rgba(0, 0, 0, 0.12);
      }

      // 图片消息不需要气泡样式
      &:has(.image-bubble) {
        padding: 0;
        background: transparent;
        box-shadow: none;
        max-width: 240px; // 图片消息单独限制
      }

      .text-bubble {
        word-break: break-word;
        font-size: 14px;
        line-height: 1.6;
        max-width: 100%; // 文字气泡最大占满父元素
      }

      .image-bubble {
        max-width: 240px;
        max-height: 240px;
        border-radius: 12px;
        cursor: pointer;
        transition: transform 0.2s ease;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

        &:hover {
          transform: scale(1.02);
        }

        .image-loading {
          display: flex;
          justify-content: center;
          align-items: center;
          width: 200px;
          height: 200px;
          background: var(--border-color);
          border-radius: 12px;
          font-size: 13px;
          color: var(--text-muted);
        }

        .image-error {
          display: flex;
          flex-direction: column;
          justify-content: center;
          align-items: center;
          gap: 8px;
          width: 200px;
          height: 200px;
          background: var(--border-color);
          border-radius: 12px;
          color: var(--text-muted);

          .el-icon {
            font-size: 32px;
          }

          span {
            font-size: 13px;
          }
        }
      }
    }

    // 已读/未读状态
    .read-status {
      margin-top: 4px;
      font-size: 10px;
      font-weight: 500;
    }
  }
}

// 黑夜模式适配
html.dark {
  .message-bubble {
    --bg-page: #0a0a0a;
    --bg-message-received: #1a1a1a;
    --bg-message-sent: #3b82f6;
    --border-color: #27272a;
    --text-primary: #f5f5f5;
    --text-secondary: #a1a1aa;
    --text-muted: #71717a;
    --text-on-accent: #ffffff;
    --accent-color: #3b82f6;
    --success-color: #22c55e;
  }
}
</style>
