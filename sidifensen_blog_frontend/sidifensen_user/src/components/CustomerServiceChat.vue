<template>
  <el-dialog v-model="dialogVisible" title="智能客服小林 💬" :width="dialogWidth" :close-on-click-modal="false" draggable class="customer-service-dialog">
    <div class="chat-container">
      <!-- 欢迎消息 -->
      <div v-if="messageList.length === 0" class="welcome-message">
        <div class="welcome-icon">👋</div>
        <p>你好！我是小林，很高兴为你服务~</p>
        <p class="welcome-tips">你可以问我关于博客使用的任何问题哦！</p>
      </div>

      <!-- 消息列表 -->
      <div class="message-list" ref="messageListRef">
        <div v-for="(msg, index) in messageList" :key="index" :class="['message-item', msg.role === 'user' ? 'user-message' : 'ai-message']">
          <div class="message-avatar">
            <el-avatar v-if="msg.role === 'user'" :size="32" :src="userStore.user?.avatar" />
            <div v-else class="ai-avatar">🤖</div>
          </div>
          <div class="message-content">
            <div class="message-bubble">{{ msg.content }}</div>
            <div class="message-time">{{ msg.time }}</div>
          </div>
        </div>

        <!-- AI 正在输入提示 -->
        <div v-if="isAiTyping" class="typing-indicator">
          <div class="ai-avatar-small">🤖</div>
          <div class="typing-dots">
            <span></span>
            <span></span>
            <span></span>
          </div>
        </div>
      </div>

      <!-- 输入框 -->
      <div class="input-container">
        <div class="input-wrapper">
          <el-input v-model="inputMessage" type="textarea" :autosize="{ minRows: 1, maxRows: 4 }" placeholder="输入你的问题..." @keydown="handleKeydown" :disabled="isSending" maxlength="500" show-word-limit class="textarea-input" />
          <el-button :icon="Promotion" circle size="small" type="primary" @click="sendMessage" :loading="isSending" :disabled="!inputMessage.trim()" class="send-button" />
        </div>
      </div>

      <!-- 快捷问题 -->
      <div v-if="messageList.length === 0" class="quick-questions">
        <div class="quick-question-title">常见问题：</div>
        <el-tag v-for="(question, index) in quickQuestions" :key="index" class="quick-question-tag" @click="selectQuickQuestion(question)">
          {{ question }}
        </el-tag>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button size="small" @click="clearChat">清空对话</el-button>
        <el-button size="small" type="primary" @click="dialogVisible = false">关闭</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, nextTick, watch, computed, onMounted, onUnmounted } from "vue";
import { Promotion } from "@element-plus/icons-vue";
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
    return "500px";
  }
  // 桌面端
  return "450px";
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

// 生成唯一ID的函数
const generateUniqueId = () => {
  return `chat_${Date.now()}_${Math.random().toString(36).substring(2, 9)}`;
};

// 对话框显示状态
const dialogVisible = ref(false);

// 会话ID（用于维持上下文）
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
      chatId.value = generateUniqueId(); // 重新生成会话ID
      ElMessage.success("对话已清空");
    })
    .catch(() => {});
};

// 打开对话框
const open = () => {
  dialogVisible.value = true;
};

// 暴露方法给父组件
defineExpose({
  open,
});

// 监听对话框关闭
watch(dialogVisible, (newVal) => {
  if (newVal) {
    scrollToBottom();
  }
});
</script>

<style lang="scss" scoped>
// 聊天容器
.chat-container {
  display: flex;
  flex-direction: column;
  height: 600px;

  // 手机端高度调整
  @media (max-width: 768px) {
    height: calc(100vh - 8vh - 200px); // 配合对话框的上边距和下边距
    min-height: 350px;
    max-height: calc(100vh - 8vh - 200px); // 确保不超过对话框的最大高度
  }

  // 欢迎消息
  .welcome-message {
    text-align: center;
    padding: 30px 20px 20px 20px; // 减少 padding，为问题列表留出空间
    color: var(--el-text-color-regular);

    .welcome-icon {
      font-size: 40px; // 稍微减小图标
      margin-bottom: 12px;
    }

    p {
      margin: 6px 0;
      font-size: 15px;
    }

    .welcome-tips {
      font-size: 13px;
      color: var(--el-text-color-secondary);
    }
  }

  // 消息列表
  .message-list {
    flex: 1;
    overflow-y: auto;
    overflow-x: hidden; // 防止横向滚动
    padding: 16px;
    display: flex;
    flex-direction: column;
    gap: 16px;
    width: 100%; // 确保宽度为 100%
    box-sizing: border-box; // 包含 padding 在宽度计算中

    // 自定义滚动条
    &::-webkit-scrollbar {
      width: 6px;
    }

    &::-webkit-scrollbar-thumb {
      background: var(--el-border-color);
      border-radius: 3px;

      &:hover {
        background: var(--el-border-color-dark);
      }
    }

    // 消息项
    .message-item {
      display: flex;
      align-items: flex-start;
      gap: 8px;
      width: 100%; // 确保宽度为 100%
      min-width: 0; // 允许收缩
      box-sizing: border-box; // 包含 padding 在宽度计算中

      // 消息头像
      .message-avatar {
        flex-shrink: 0;

        .ai-avatar {
          width: 32px;
          height: 32px;
          border-radius: 50%;
          background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 18px;
        }
      }

      // 消息内容
      .message-content {
        display: flex;
        flex-direction: column;
        gap: 4px;
        max-width: 70%;
        min-width: 0; // 确保 flex 子元素可以收缩
        flex: 0 1 auto; // 允许收缩但不会自动扩展

        .message-bubble {
          padding: 10px 14px;
          border-radius: 12px;
          font-size: 14px;
          line-height: 1.6;
          word-wrap: break-word;
          word-break: break-word; // 强制长单词换行
          overflow-wrap: break-word; // 确保长文本换行
          white-space: pre-wrap;
          max-width: 100%; // 确保不超出父容器
          box-sizing: border-box; // 包含 padding 在宽度计算中
          overflow: hidden; // 防止内容溢出
        }

        .message-time {
          font-size: 12px;
          color: var(--el-text-color-secondary);
          padding: 0 4px;
        }
      }
    }

    // 用户消息样式
    .user-message {
      flex-direction: row-reverse;

      .message-content {
        align-items: flex-end;

        .message-bubble {
          background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
          color: white;
        }
      }
    }

    // AI 消息样式
    .ai-message {
      .message-content {
        .message-bubble {
          background: var(--el-fill-color-light);
          color: var(--el-text-color-primary);
        }
      }
    }

    // 正在输入指示器
    .typing-indicator {
      display: flex;
      align-items: center;
      gap: 8px;

      .ai-avatar-small {
        width: 24px;
        height: 24px;
        border-radius: 50%;
        background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 14px;
      }

      .typing-dots {
        display: flex;
        gap: 4px;
        padding: 8px 12px;
        background: var(--el-fill-color-light);
        border-radius: 12px;

        span {
          width: 6px;
          height: 6px;
          border-radius: 50%;
          background: var(--el-text-color-secondary);
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
  }

  // 输入容器
  .input-container {
    padding: 12px 16px;
    border-top: 1px solid var(--el-border-color-light);

    // 输入框包装器
    .input-wrapper {
      display: flex;
      align-items: flex-end;
      gap: 8px;
      position: relative;

      // 文本域输入框
      .textarea-input {
        flex: 1;
        min-width: 0; // 确保可以收缩

        :deep(.el-textarea__inner) {
          //   padding-right: 54px; // 为字数统计和滚动条留出空间（50px + 4px滚动条）
          //   padding-bottom: 30px; // 为字数统计留出空间
          resize: none; // 禁用手动调整大小
          font-family: inherit;
          line-height: 1.5;
          word-wrap: break-word;
          word-break: break-word;
          overflow-wrap: break-word;
          max-height: 120px; // 最大高度（约4行）
          overflow-y: auto; // 超过最大高度时显示滚动条
          box-sizing: border-box; // 包含 padding 在高度计算中

          // 自定义滚动条样式
          &::-webkit-scrollbar {
            width: 8px;
          }

          &::-webkit-scrollbar-thumb {
            background: var(--el-text-color-secondary);
            border-radius: 2px;
            min-height: 18px; // 最小高度，确保可见性

            &:hover {
              background: var(--el-border-color-dark);
            }
          }

          &::-webkit-scrollbar-track {
            background: transparent;
          }
        }

        // 字数统计样式
        :deep(.el-input__count) {
          bottom: 5px;
          right: 10px;
          line-height: 1;
        }
      }

      // 发送按钮
      .send-button {
        flex-shrink: 0; // 按钮不收缩
        margin-bottom: 0; // 与输入框底部对齐
      }
    }
  }

  // 快捷问题
  .quick-questions {
    padding: 12px 16px 20px 16px; // 增加底部 padding，与底部按钮隔开
    border-top: 1px solid var(--el-border-color-light);
    max-height: 160px; // 限制最大高度
    overflow-y: auto; // 如果内容过多，允许滚动

    // 自定义滚动条样式
    &::-webkit-scrollbar {
      width: 6px;
    }

    &::-webkit-scrollbar-thumb {
      background: var(--el-border-color);
      border-radius: 3px;

      &:hover {
        background: var(--el-border-color-dark);
      }
    }

    .quick-question-title {
      font-size: 12px;
      color: var(--el-text-color-secondary);
      margin-bottom: 8px;
      font-weight: 500;
    }

    .quick-question-tag {
      margin: 4px 4px 4px 0; // 调整间距，右边距为0，让按钮可以紧贴
      cursor: pointer;
      transition: all 0.3s;
      font-size: 12px;
      padding: 6px 12px;
      border-radius: 16px;
      white-space: nowrap; // 防止文字换行

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
        background-color: var(--el-color-primary);
        color: white;
      }

      &:active {
        transform: translateY(0);
      }
    }
  }
}

// 对话框底部
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
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
    transform: translateY(-8px);
    opacity: 1;
  }
}

// 对话框响应式样式
:deep(.customer-service-dialog) {
  // 手机端样式
  @media (max-width: 768px) {
    .el-dialog {
      margin: 0 auto;
      margin-top: 8vh !important; // 增加上边距，让弹窗往上移动
      margin-bottom: 100px !important; // 增加下边距，为悬浮按钮留出空间（悬浮按钮高度约64px + 安全距离）
      max-width: calc(100vw - 32px) !important;
      width: calc(100vw - 32px) !important;
      max-height: calc(100vh - 8vh - 100px) !important; // 限制最大高度，确保不超出屏幕
    }

    .el-dialog__header {
      padding: 16px 16px 12px 16px;
    }

    .el-dialog__body {
      padding: 0;
    }

    .el-dialog__footer {
      padding: 12px 16px;
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
