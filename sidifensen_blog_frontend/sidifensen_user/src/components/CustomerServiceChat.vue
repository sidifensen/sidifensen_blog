<template>
  <el-dialog v-model="dialogVisible" title="" :width="dialogWidth" :close-on-click-modal="false" :show-close="false" draggable class="customer-service-dialog">
    <!-- 自定义 header -->
    <template #header>
      <div class="dialog-header">
        <div class="header-logo">
          <div class="logo-icon">
            <!-- 机器人图标 -->
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <rect x="3" y="11" width="18" height="10" rx="2"/>
              <circle cx="12" cy="5" r="2"/>
              <path d="M12 7v4"/>
              <line x1="8" y1="16" x2="8" y2="16"/>
              <line x1="16" y1="16" x2="16" y2="16"/>
            </svg>
          </div>
          <div class="header-info">
            <span class="header-title">智能客服小林</span>
            <span class="header-status">
              <span class="status-dot"></span>
              在线服务
            </span>
          </div>
        </div>
        <div class="header-actions">
          <el-button text class="clear-btn" @click="clearChat" title="清空对话">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M3 6h18M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2M10 11v6M14 11v6"/>
            </svg>
          </el-button>
          <el-button text class="close-btn" @click="dialogVisible = false" title="关闭">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M18 6L6 18M6 6l12 12"/>
            </svg>
          </el-button>
        </div>
      </div>
    </template>

    <div class="chat-container">
      <!-- 欢迎消息 -->
      <div v-if="messageList.length === 0" class="welcome-message">
        <div class="welcome-avatar">
          <div class="avatar-icon">
            <!-- 机器人图标 -->
            <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <rect x="3" y="11" width="18" height="10" rx="2"/>
              <circle cx="12" cy="5" r="2"/>
              <path d="M12 7v4"/>
              <line x1="8" y1="16" x2="8" y2="16"/>
              <line x1="16" y1="16" x2="16" y2="16"/>
            </svg>
          </div>
        </div>
        <p class="welcome-text">你好！我是小林，很高兴为你服务~</p>
        <p class="welcome-tips">你可以问我关于社区使用的任何问题哦！</p>
      </div>

      <!-- 消息列表 -->
      <div class="message-list" ref="messageListRef">
        <div v-for="(msg, index) in messageList" :key="index" :class="['message-item', msg.role === 'user' ? 'user-message' : 'ai-message']">
          <div class="message-avatar">
            <el-avatar v-if="msg.role === 'user'" :size="36" :src="userStore.user?.avatar" class="user-avatar" />
            <div v-else class="ai-avatar">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <rect x="3" y="11" width="18" height="10" rx="2"/>
                <circle cx="12" cy="5" r="2"/>
                <path d="M12 7v4"/>
                <line x1="8" y1="16" x2="8" y2="16"/>
                <line x1="16" y1="16" x2="16" y2="16"/>
              </svg>
            </div>
          </div>
          <div class="message-content">
            <div class="message-sender" v-if="msg.role === 'ai'">小林</div>
            <div class="message-bubble">
              <span class="bubble-text">{{ msg.content }}</span>
            </div>
            <div class="message-time">{{ msg.time }}</div>
          </div>
        </div>

        <!-- AI 正在输入提示 -->
        <div v-if="isAiTyping" class="typing-indicator">
          <div class="ai-avatar">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <rect x="3" y="11" width="18" height="10" rx="2"/>
              <circle cx="12" cy="5" r="2"/>
              <path d="M12 7v4"/>
              <line x1="8" y1="16" x2="8" y2="16"/>
              <line x1="16" y1="16" x2="16" y2="16"/>
            </svg>
          </div>
          <div class="typing-bubble">
            <span class="typing-dot"></span>
            <span class="typing-dot"></span>
            <span class="typing-dot"></span>
          </div>
        </div>
      </div>

      <!-- 输入框 -->
      <div class="input-area">
        <div class="input-wrapper">
          <el-input
            v-model="inputMessage"
            type="textarea"
            :autosize="{ minRows: 1, maxRows: 4 }"
            placeholder="输入你的问题，按 Enter 发送..."
            @keydown="handleKeydown"
            :disabled="isSending"
            maxlength="500"
            show-word-limit
            class="chat-input"
          />
          <el-button class="send-button" @click="sendMessage" :loading="isSending" :disabled="!inputMessage.trim()">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M22 2L11 13" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M22 2L15 22L11 13L2 9L22 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            发送
          </el-button>
        </div>
      </div>

      <!-- 快捷问题 -->
      <div v-if="messageList.length === 0" class="quick-questions">
        <div class="quick-question-title">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10"/>
            <path d="M9.09 9a3 3 0 015.83 1c0 2-3 3-3 3M12 17h.01"/>
          </svg>
          常见问题
        </div>
        <div class="quick-tags">
          <el-tag
            v-for="(question, index) in quickQuestions"
            :key="index"
            class="quick-tag"
            @click="selectQuickQuestion(question)"
          >
            {{ question }}
          </el-tag>
        </div>
      </div>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, nextTick, watch, computed, onMounted, onUnmounted } from "vue";
import { customerServiceChat } from "@/api/ai";
import { useUserStore } from "@/stores/userStore";

const userStore = useUserStore();

// 响应式对话框宽度
const windowWidth = ref(window.innerWidth);
const dialogWidth = computed(() => {
  // 手机端：留出左右各 16px 的边距
  if (windowWidth.value <= 768) {
    return `${windowWidth.value - 32}px`;
  }
  // 平板端
  if (windowWidth.value <= 992) {
    return "600px";
  }
  // 桌面端
  return "700px";
});

// 监听窗口大小变化
const handleResize = () => {
  windowWidth.value = window.innerWidth;
};

onMounted(() => {
  window.addEventListener("resize", handleResize);
});

onUnmounted(() => {
  window.removeEventListener("resize", handleResize);
});

// 生成唯一 ID 的函数
const generateUniqueId = () => {
  return `chat_${Date.now()}_${Math.random().toString(36).substring(2, 9)}`;
};

// 对话框显示状态
const dialogVisible = ref(false);

// 会话 ID（用于维持上下文）
const chatId = ref(generateUniqueId());

// 消息列表
const messageList = ref([]);

// 输入内容
const inputMessage = ref("");

// 发送状态
const isSending = ref(false);

// AI 正在输入状态
const isAiTyping = ref(false);

// 消息列表引用
const messageListRef = ref(null);

// 快捷问题 - 常见问题列表
const quickQuestions = ref([
  "总结这个项目",
  "我想申请友链",
  "如何发布文章？",
  "如何创建专栏？",
  "如何修改个人资料？",
  "如何给其他用户发私信？",
  "如何查看我收到的通知？",
  "如何查看我的文章数据？",
  "如何管理我的收藏？",
  "如何关注其他用户？",
  "专栏和文章有什么区别？",
  "标签有什么作用？",
  "文章可以设置哪些可见性？",
  "如何上传图片？",
  "如何删除或编辑已发布的文章？",
  "如何提高文章的阅读量？",
  "文章为什么没有通过审核？",
]);

// 处理键盘事件
const handleKeydown = (event) => {
  // Shift + Enter 换行，Enter 发送
  if (event.key === "Enter" && !event.shiftKey) {
    event.preventDefault();
    sendMessage();
  }
};

// 格式化时间
const formatTime = () => {
  const now = new Date();
  return `${now.getHours().toString().padStart(2, "0")}:${now.getMinutes().toString().padStart(2, "0")}`;
};

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (messageListRef.value) {
      messageListRef.value.scrollTop = messageListRef.value.scrollHeight;
    }
  });
};

// 发送消息
const sendMessage = async () => {
  const message = inputMessage.value.trim();
  if (!message || isSending.value) return;

  // 添加用户消息到列表
  messageList.value.push({
    role: "user",
    content: message,
    time: formatTime(),
  });

  inputMessage.value = "";
  isSending.value = true;
  isAiTyping.value = true;
  scrollToBottom();

  try {
    // 调用流式 API
    const response = await customerServiceChat(message, chatId.value);

    if (!response.ok) {
      throw new Error("网络请求失败");
    }

    const reader = response.body.getReader();
    const decoder = new TextDecoder();

    // 创建 AI 消息对象
    const aiMessage = {
      role: "ai",
      content: "",
      time: formatTime(),
    };

    // 添加 AI 消息占位
    messageList.value.push(aiMessage);
    isAiTyping.value = false;

    // 获取消息在列表中的索引，确保引用正确
    const messageIndex = messageList.value.length - 1;
    scrollToBottom();

    // 读取流式响应
    while (true) {
      const { done, value } = await reader.read();
      if (done) {
        break;
      }

      const chunk = decoder.decode(value, { stream: true });

      // 直接修改列表中的消息内容，触发 Vue 响应式更新
      messageList.value[messageIndex].content += chunk;

      // 使用 nextTick 确保 DOM 更新后再滚动
      await nextTick();
      scrollToBottom();
    }
  } catch (error) {
    ElMessage.error("发送消息失败，请稍后重试");
    // 移除最后一条消息
    if (messageList.value[messageList.value.length - 1].role === "ai") {
      messageList.value.pop();
    }
  } finally {
    isSending.value = false;
    isAiTyping.value = false;
  }
};

// 选择快捷问题
const selectQuickQuestion = (question) => {
  inputMessage.value = question;
  sendMessage();
};

// 清空对话
const clearChat = () => {
  ElMessageBox.confirm("确定要清空对话记录吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(() => {
      messageList.value = [];
      chatId.value = generateUniqueId(); // 重新生成会话 ID
      ElMessage.success("对话已清空");
    })
    .catch(() => {});
};

// 打开对话框
const open = () => {
  dialogVisible.value = true;
};

// 切换对话框状态
const toggle = () => {
  dialogVisible.value = !dialogVisible.value;
};

// 暴露方法给父组件
defineExpose({
  open,
  toggle,
});

// 监听对话框关闭
watch(dialogVisible, (newVal) => {
  if (newVal) {
    scrollToBottom();
  }
});
</script>

<style lang="scss" scoped>
// 对话头部样式
.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 4px 0;

  .header-logo {
    display: flex;
    align-items: center;
    gap: 12px;

    .logo-icon {
      width: 40px;
      height: 40px;
      border-radius: 12px;
      background: #3b82f6;
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;
      flex-shrink: 0;
    }

    .header-info {
      display: flex;
      flex-direction: column;
      gap: 4px;

      .header-title {
        font-size: 16px;
        font-weight: 600;
        color: var(--el-text-color-primary);
        line-height: 1.4;
      }

      .header-status {
        display: flex;
        align-items: center;
        gap: 6px;
        font-size: 12px;
        color: var(--el-text-color-regular);

        .status-dot {
          width: 8px;
          height: 8px;
          border-radius: 50%;
          background: #10b981;
          animation: pulse 2s infinite;
        }
      }
    }
  }

  .header-actions {
    display: flex;
    align-items: center;
    gap: 4px;

    .clear-btn,
    .close-btn {
      color: var(--el-text-color-secondary);
      border-radius: 8px;
      transition: all 0.2s;

      &:hover {
        background: var(--el-fill-color);
        color: var(--el-text-color-primary);
      }

      :deep(.svg-icon) {
        width: 20px;
        height: 20px;
      }
    }
  }
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.6;
  }
}

// 聊天容器
.chat-container {
  display: flex;
  flex-direction: column;
  height: 650px;
  background: transparent;

  // 手机端高度调整
  @media (max-width: 768px) {
    height: calc(100vh - 12vh);
    min-height: 400px;
    max-height: calc(100vh - 12vh);
  }

  // 平板端高度
  @media (min-width: 769px) and (max-width: 992px) {
    height: 600px;
  }

  // 消息列表
  .message-list {
    flex: 1;
    overflow-y: auto;
    overflow-x: hidden;
    padding: 20px;
    display: flex;
    flex-direction: column;
    gap: 20px;
    background: transparent;
    backdrop-filter: blur(10px);
    min-height: 0; // 关键：允许 flex 子项正确滚动
  }

  // 欢迎消息
  .welcome-message {
    flex-shrink: 0; // 防止被压缩
    text-align: center;
    padding: 50px 30px 40px 30px;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 20px;

    .welcome-avatar {
      width: 80px;
      height: 80px;
      display: flex;
      align-items: center;
      justify-content: center;

      .avatar-icon {
        width: 80px;
        height: 80px;
        border-radius: 50%;
        background: #3b82f6;
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        box-shadow: 0 4px 12px rgba(59, 130, 246, 0.25);

        svg {
          width: 40px;
          height: 40px;
        }
      }
    }

    .welcome-text {
      font-size: 20px;
      font-weight: 600;
      color: var(--el-text-color-primary);
      margin: 0;
    }

    .welcome-tips {
      font-size: 15px;
      color: var(--el-text-color-regular);
      margin: 0;
      opacity: 0.8;
    }
  }

  // 消息项
  .message-item {
    display: flex;
    align-items: flex-start;
    gap: 12px;
    width: 100%;
    min-width: 0;
    animation: messageSlideIn 0.3s ease-out;

    .message-avatar {
      flex-shrink: 0;

      .user-avatar {
        border: 2px solid white;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      }

      .ai-avatar {
        width: 36px;
        height: 36px;
        border-radius: 50%;
        background: #3b82f6;
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        flex-shrink: 0;
        box-shadow: 0 2px 8px rgba(59, 130, 246, 0.25);
      }
    }

    // 消息内容
    .message-content {
      display: flex;
      flex-direction: column;
      gap: 6px;
      max-width: 75%;
      min-width: 0;
      flex: 0 1 auto;

      .message-sender {
        font-size: 12px;
        font-weight: 500;
        color: var(--el-text-color-secondary);
        padding: 0 4px;
      }

      .message-bubble {
        padding: 12px 16px;
        border-radius: 16px;
        font-size: 14px;
        line-height: 1.6;
        word-wrap: break-word;
        word-break: break-word;
        overflow-wrap: break-word;
        white-space: pre-wrap;
        max-width: 100%;
        box-sizing: border-box;
        overflow: hidden;
        box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);

        .bubble-text {
          color: var(--el-text-color-primary);
        }
      }

      .message-time {
        font-size: 11px;
        color: var(--el-text-color-secondary);
        padding: 0 4px;
        opacity: 0.7;
      }
    }
  }

  // 用户消息样式
  .user-message {
    flex-direction: row-reverse;

    .message-content {
      align-items: flex-end;

      .message-sender {
        text-align: right;
      }

      .message-bubble {
        background: #3b82f6;

        .bubble-text {
          color: white;
        }
      }
    }
  }

  // AI 消息样式
  .ai-message {
    .message-content {
      .message-bubble {
        background: var(--el-bg-color);
        border: 1px solid var(--el-border-color-light, rgba(0, 0, 0, 0.1));
      }
    }
  }

  // 正在输入指示器
  .typing-indicator {
    display: flex;
    align-items: center;
    gap: 12px;
    animation: messageSlideIn 0.3s ease-out;

    .ai-avatar {
      width: 32px;
      height: 32px;
      border-radius: 50%;
      background: #3b82f6;
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;
      flex-shrink: 0;
      box-shadow: 0 2px 8px rgba(59, 130, 246, 0.25);

      svg {
        width: 18px;
        height: 18px;
      }
    }

    .typing-bubble {
      display: flex;
      gap: 4px;
      padding: 10px 14px;
      background: var(--el-bg-color);
      border: 1px solid var(--el-border-color-light, rgba(0, 0, 0, 0.1));
      border-radius: 16px;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);

      .typing-dot {
        width: 6px;
        height: 6px;
        border-radius: 50%;
        background: #3b82f6;
        animation: typing 1.4s infinite;

        &:nth-child(2) {
          animation-delay: 0.2s;
        }

        &:nth-child(3) {
          animation-delay: 0.4s;
        }
      }
    }
  }

  // 输入区域
  .input-area {
    flex-shrink: 0;
    padding: 16px 20px;
    background: var(--el-bg-color);
    border-top: 1px solid var(--el-border-color-light, rgba(0, 0, 0, 0.1));

    .input-wrapper {
      display: flex;
      align-items: flex-end;
      gap: 12px;
      background: var(--el-fill-color-light);
      border-radius: 16px;
      padding: 8px;
      border: 1px solid rgba(59, 130, 246, 0.15);
      transition: all 0.3s;

      &:focus-within {
        border-color: #3b82f6;
        box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
        background: var(--el-bg-color);
      }

      // 手机端样式
      @media (max-width: 768px) {
        padding: 6px;
        gap: 8px;
      }

      .chat-input {
        flex: 1;
        min-width: 0;

        :deep(.el-textarea__inner) {
          resize: none;
          font-family: inherit;
          line-height: 1.5;
          word-wrap: break-word;
          word-break: break-word;
          overflow-wrap: break-word;
          max-height: 120px;
          overflow-y: auto;
          box-sizing: border-box;
          border: none !important;
          box-shadow: none !important;
          background: transparent;
          padding: 8px 12px;
          font-size: 14px;

          &::-webkit-scrollbar {
            width: 6px;
          }

          &::-webkit-scrollbar-thumb {
            background: #c0c4cc;
            border-radius: 3px;

            &:hover {
              background: #909399;
            }
          }

          &::-webkit-scrollbar-track {
            background: transparent;
          }
        }

        :deep(.el-input__count) {
          bottom: 4px;
          right: 8px;
          line-height: 1;
          font-size: 11px;
          background: transparent;
        }
      }

      .send-button {
        flex-shrink: 0;
        background: #3b82f6;
        border: none;
        border-radius: 12px;
        padding: 10px 20px;
        height: auto;
        font-weight: 500;
        font-size: 14px;
        display: flex;
        align-items: center;
        gap: 6px;
        transition: all 0.3s;
        white-space: nowrap;

        &:hover:not(:disabled) {
          background: #2563eb;
          box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
        }

        &:active:not(:disabled) {
          transform: translateY(0);
        }

        &:disabled {
          opacity: 0.6;
          cursor: not-allowed;
        }

        // 手机端样式
        @media (max-width: 768px) {
          padding: 8px 16px;
          font-size: 13px;
        }
      }
    }
  }

  // 快捷问题
  .quick-questions {
    flex-shrink: 0;
    padding: 20px;
    background: var(--el-fill-color-lighter);
    border-top: 1px solid var(--el-border-color-light, rgba(0, 0, 0, 0.1));

    .quick-question-title {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 13px;
      font-weight: 500;
      color: var(--el-text-color-regular);
      margin-bottom: 12px;
      flex-shrink: 0;

      svg {
        color: #3b82f6;
        flex-shrink: 0;
      }
    }

    .quick-tags {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;
      max-height: 100px;
      overflow-y: auto;

      &::-webkit-scrollbar {
        width: 4px;
      }

      &::-webkit-scrollbar-thumb {
        background: #c0c4cc;
        border-radius: 2px;
      }

      .quick-tag {
        cursor: pointer;
        transition: all 0.2s;
        font-size: 12px;
        padding: 8px 14px;
        border-radius: 20px;
        white-space: nowrap;
        background: var(--el-fill-color);
        border-color: transparent;
        color: var(--el-text-color-regular);
        flex-shrink: 0;

        &:hover {
          background: #3b82f6;
          color: white;
        }

        &:active {
          transform: scale(0.98);
        }
      }
    }

    // 手机端样式
    @media (max-width: 768px) {
      padding: 12px 16px;

      .quick-question-title {
        font-size: 12px;
        margin-bottom: 8px;
      }

      .quick-tags {
        max-height: 100px;
        gap: 6px;
      }

      .quick-tag {
        padding: 6px 10px;
        font-size: 11px;
      }
    }
  }
}

@keyframes messageSlideIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

// 打字动画
@keyframes typing {
  0%,
  60%,
  100% {
    transform: translateY(0);
    opacity: 0.4;
  }

  30% {
    transform: translateY(-6px);
    opacity: 1;
  }
}

// 对话框响应式样式
:deep(.customer-service-dialog) {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);

  .el-dialog__header {
    padding: 16px 20px;
    border-bottom: 1px solid var(--el-border-color-light);
    background: var(--el-fill-color-lighter);
  }

  .el-dialog__body {
    padding: 0;
    background: transparent;
  }

  .el-dialog__footer {
    padding: 0;
    border-top: 1px solid var(--el-border-color-light);
    background: var(--el-fill-color-light);
  }

  // 手机端样式
  @media (max-width: 768px) {
    .el-dialog {
      margin: 0 auto;
      margin-top: 8vh !important;
      margin-bottom: 100px !important;
      max-width: calc(100vw - 32px) !important;
      width: calc(100vw - 32px) !important;
      max-height: calc(100vh - 8vh - 100px) !important;
      border-radius: 16px 16px 0 0;
    }
  }

  // 平板端样式
  @media (min-width: 769px) and (max-width: 992px) {
    .el-dialog {
      margin: 5vh auto;
    }
  }
}
</style>
