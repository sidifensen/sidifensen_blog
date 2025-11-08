<template>
  <div class="chat-window-page">
    <div class="container">
      <div class="chat-window-container">
        <!-- 聊天窗口头部 -->
        <div class="chat-header">
          <el-button :icon="ArrowLeft" circle @click="goBack" />
          <div class="user-info" v-if="targetUser">
            <div class="avatar-wrapper" @click="goToUserHomepage(targetUser.id)">
              <el-avatar :size="40" :src="targetUser.avatar" />
              <span v-if="isOnline" class="online-indicator"></span>
            </div>
            <div class="user-details">
              <span class="username">{{ targetUser.nickname }}</span>
              <span class="online-status-text" v-if="isOnline">在线</span>
            </div>
          </div>
        </div>

        <!-- 消息列表 -->
        <div class="message-list" ref="messageContainer" v-loading="loading">
          <div
            v-for="msg in messageStore.currentChatMessages"
            :key="msg.id"
            class="message-item"
            :class="{ 'is-mine': msg.fromUserId === currentUserId }"
            @contextmenu.prevent="handleContextMenu($event, msg)"
            @touchstart="handleTouchStart($event, msg)"
            @touchend="handleTouchEnd"
            @touchmove="handleTouchMove"
          >
            <el-avatar :size="40" :src="msg.fromUserAvatar" class="message-avatar" @click="goToUserHomepage(msg.fromUserId)" />
            <div class="message-content">
              <div class="message-header">
                <span class="message-nickname">{{ msg.fromUserNickname }}</span>
                <span class="message-time">{{ getFriendlyTime(msg.createTime) }}</span>
              </div>
              <div class="message-body">
                <div v-if="msg.messageType === 1" class="text-message">{{ msg.content }}</div>
                <el-image v-else-if="msg.messageType === 2" :src="msg.imageUrl" class="image-message" fit="cover" :preview-src-list="[msg.imageUrl]" :initial-index="0" preview-teleported>
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
              <!-- 显示自己消息的已读/未读状态 -->
              <div v-if="msg.fromUserId === currentUserId" class="read-status">
                <span v-if="msg.isRead === 1" class="read-text">已读</span>
                <span v-else class="unread-text">未读</span>
              </div>
            </div>
          </div>

          <!-- 撤回提示 -->
          <div v-if="revokeNotification" class="revoke-notification">
            <span>{{ revokeNotification }}</span>
          </div>

          <!-- 空状态 -->
          <el-empty v-if="messageStore.currentChatMessages.length === 0 && !loading" description="暂无消息" />
        </div>

        <!-- 右键菜单 -->
        <div v-if="contextMenuVisible" class="context-menu" :style="{ left: contextMenuPosition.x + 'px', top: contextMenuPosition.y + 'px' }">
          <div v-if="selectedMessage && selectedMessage.messageType === 1" class="context-menu-item" @click="handleCopy">
            <el-icon><DocumentCopy /></el-icon>
            <span>复制</span>
          </div>
          <div v-if="canRevoke(selectedMessage)" class="context-menu-item" @click="handleRevoke">
            <el-icon><Delete /></el-icon>
            <span>撤回消息</span>
          </div>
        </div>

        <!-- 消息输入框 -->
        <div class="message-input-area">
          <el-input ref="messageInput" v-model="messageContent" type="textarea" :rows="3" placeholder="请输入消息..." @keydown.enter.ctrl="sendMessage" resize="none" />
          <div class="input-actions">
            <div class="input-actions-left">
              <el-button :icon="ChatDotSquare" @click="toggleEmojiPicker">表情</el-button>
              <el-button :icon="Picture" @click="openImagePicker" :loading="imageUploadLoading">图片</el-button>
            </div>
            <el-button type="primary" @click="sendMessage">发送 (Ctrl+Enter)</el-button>
          </div>

          <!-- Emoji 表情选择器 -->
          <div v-show="showEmojiPicker" class="emoji-picker">
            <div class="emoji-header">
              <span class="emoji-title">选择表情</span>
              <el-button text :icon="Close" @click="closeEmojiPicker" />
            </div>
            <div class="emoji-list">
              <div v-for="(emoji, index) in emojiList" :key="index" class="emoji-item" @click="insertEmoji(emoji)">
                {{ emoji }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from "vue";
import { useRoute, useRouter } from "vue-router";
import { ArrowLeft, Delete, DocumentCopy, ChatDotSquare, Close, Picture } from "@element-plus/icons-vue";
import { getChatHistory } from "@/api/privateMessage";
import { getUserInfoById } from "@/api/user";
import { getConversationList } from "@/api/conversation";
import { uploadMessagePhoto } from "@/api/photo";
import { useMessageStore } from "@/stores/messageStore";
import { useUserStore } from "@/stores/userStore";
import WebSocketClient from "@/utils/WebSocketClient";
import { getFriendlyTime } from "@/utils/formatTime";
import { emojiList } from "@/utils/emoji";

const route = useRoute();
const router = useRouter();
const messageStore = useMessageStore();
const userStore = useUserStore();

const loading = ref(false); // 加载状态
const messageContent = ref(""); // 消息输入框内容
const targetUser = ref(null); // 目标用户信息
const messageContainer = ref(null); // 消息列表容器
const messageInput = ref(null); // 消息输入框引用

// Emoji 表情选择器相关
const showEmojiPicker = ref(false); // 表情选择器显示状态

// 图片上传相关
const imageUploadLoading = ref(false); // 图片上传加载状态
const fileInputRef = ref(null); // 文件输入框引用

const targetUserId = computed(() => parseInt(route.params.userId)); // 目标用户ID
const currentUserId = computed(() => userStore.user?.id); // 当前用户ID
const isOnline = ref(false);

// 右键菜单相关
const contextMenuVisible = ref(false); // 右键菜单是否可见
const contextMenuPosition = ref({ x: 0, y: 0 }); // 右键菜单位置
const selectedMessage = ref(null); // 选中的消息

// 长按相关
const longPressTimer = ref(null); // 长按定时器
const isLongPress = ref(false); // 是否是长按

// 撤回提示
const revokeNotification = ref(""); // 撤回提示文本
const revokeNotificationTimer = ref(null); // 撤回提示定时器
// 获取目标用户信息
const fetchTargetUser = async () => {
  try {
    const res = await getUserInfoById(targetUserId.value);
    targetUser.value = res.data.data;
  } catch (error) {
    console.error("获取用户信息失败:", error);
  }
};

// 获取聊天记录
const fetchChatHistory = async () => {
  try {
    loading.value = true;
    const res = await getChatHistory(targetUserId.value, 1, 50);
    const messages = res.data.data?.data || [];
    // 将消息列表反转，确保最新消息在底部
    messageStore.setCurrentChatMessages(messages.reverse());
    scrollToBottom();

    // 获取历史消息后，标记为已读
    if (messages.length > 0) {
      WebSocketClient.markAsRead(targetUserId.value);
    }
  } catch (error) {
    console.error("获取聊天记录失败:", error);
  } finally {
    loading.value = false;
  }
};

// 发送消息
const sendMessage = () => {
  if (!messageContent.value.trim()) {
    return;
  }

  const content = messageContent.value;

  // 立即添加到消息列表（乐观更新）
  const tempMessage = {
    id: Date.now(), // 临时ID
    fromUserId: currentUserId.value,
    toUserId: targetUserId.value,
    content: content,
    messageType: 1,
    createTime: new Date(),
    isRead: 0,
    isRevoked: 0,
    fromUserNickname: userStore.user?.nickname,
    fromUserAvatar: userStore.user?.avatar,
    toUserNickname: targetUser.value?.nickname,
    toUserAvatar: targetUser.value?.avatar,
  };

  messageStore.addMessageToCurrentChat(tempMessage);
  scrollToBottom();

  WebSocketClient.sendTextMessage(targetUserId.value, content);
  messageContent.value = "";
};

// 打开图片选择器
const openImagePicker = () => {
  // 创建隐藏的文件输入框
  if (!fileInputRef.value) {
    const input = document.createElement("input");
    input.type = "file";
    input.accept = "image/*";
    input.onchange = handleImageSelect;
    fileInputRef.value = input;
  }
  fileInputRef.value.click();
};

// 处理图片选择
const handleImageSelect = async (event) => {
  const file = event.target.files?.[0];
  if (!file) {
    return;
  }

  // 验证文件类型
  if (!file.type.startsWith("image/")) {
    ElMessage.error("只能上传图片文件");
    return;
  }

  // 验证文件大小（10MB）
  const maxSize = 10 * 1024 * 1024;
  if (file.size > maxSize) {
    ElMessage.error("图片大小不能超过10MB");
    return;
  }

  try {
    imageUploadLoading.value = true;

    // 上传图片
    const res = await uploadMessagePhoto(file);
    const imageUrl = res.data.data;

    // 立即添加到消息列表（乐观更新）
    const tempMessage = {
      id: Date.now(), // 临时ID
      fromUserId: currentUserId.value,
      toUserId: targetUserId.value,
      content: "[图片]",
      messageType: 2,
      imageUrl: imageUrl,
      createTime: new Date(),
      isRead: 0,
      isRevoked: 0,
      fromUserNickname: userStore.user?.nickname,
      fromUserAvatar: userStore.user?.avatar,
      toUserNickname: targetUser.value?.nickname,
      toUserAvatar: targetUser.value?.avatar,
    };

    messageStore.addMessageToCurrentChat(tempMessage);
    scrollToBottom();

    // 发送图片消息
    WebSocketClient.sendImageMessage(targetUserId.value, imageUrl);
    ElMessage.success("图片发送成功");
  } catch (error) {
    ElMessage.error("图片上传失败");
    console.error("上传图片失败:", error);
  } finally {
    imageUploadLoading.value = false;
    // 清空文件输入框，允许重复上传同一文件
    if (fileInputRef.value) {
      fileInputRef.value.value = "";
    }
  }
};

// 切换表情选择器
const toggleEmojiPicker = () => {
  showEmojiPicker.value = !showEmojiPicker.value;
};

// 关闭表情选择器
const closeEmojiPicker = () => {
  showEmojiPicker.value = false;
};

// 插入表情到输入框
const insertEmoji = (emoji) => {
  // 获取 textarea 元素
  const textarea = messageInput.value?.$el?.querySelector("textarea");

  if (textarea) {
    // 获取当前光标位置
    const startPos = textarea.selectionStart;
    const endPos = textarea.selectionEnd;

    // 在光标位置插入表情
    const beforeText = messageContent.value.substring(0, startPos);
    const afterText = messageContent.value.substring(endPos);
    messageContent.value = beforeText + emoji + afterText;

    // 更新光标位置（在插入的表情后面）
    nextTick(() => {
      const newPos = startPos + emoji.length;
      textarea.setSelectionRange(newPos, newPos);
      textarea.focus();
    });
  } else {
    // 如果无法获取 textarea，直接在末尾添加
    messageContent.value += emoji;
  }

  // 不关闭表情选择器，允许连续选择表情
  // closeEmojiPicker();
};

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (messageContainer.value) {
      messageContainer.value.scrollTop = messageContainer.value.scrollHeight;
    }
  });
};

// 返回会话列表
const goBack = () => {
  router.push("/message");
};

// 跳转到用户主页
const goToUserHomepage = (userId) => {
  router.push(`/user/${userId}`);
};

// 检查消息是否可以撤回（2分钟内且是自己的消息）
const canRevoke = (msg) => {
  if (msg.fromUserId !== currentUserId.value) {
    return false;
  }
  if (msg.isRevoked === 1) {
    return false;
  }
  const now = new Date().getTime();
  const createTime = new Date(msg.createTime).getTime();
  const diffMinutes = (now - createTime) / 1000 / 60;
  return diffMinutes <= 2; // 2分钟内可以撤回
};

// 处理右键菜单
const handleContextMenu = (event, msg) => {
  // 图片消息且不可撤回时，不显示菜单
  if (msg.messageType !== 1 && !canRevoke(msg)) {
    return;
  }

  contextMenuVisible.value = true;
  contextMenuPosition.value = {
    x: event.clientX,
    y: event.clientY,
  };
  selectedMessage.value = msg;
};

// 处理长按开始
const handleTouchStart = (event, msg) => {
  // 图片消息且不可撤回时，不响应长按
  if (msg.messageType !== 1 && !canRevoke(msg)) {
    return;
  }

  isLongPress.value = false;
  longPressTimer.value = setTimeout(() => {
    isLongPress.value = true;
    // 获取触摸位置
    const touch = event.touches[0];
    contextMenuVisible.value = true;
    contextMenuPosition.value = {
      x: touch.clientX,
      y: touch.clientY,
    };
    selectedMessage.value = msg;

    // 震动反馈（如果支持）
    if (navigator.vibrate) {
      navigator.vibrate(50);
    }
  }, 500); // 长按500毫秒触发
};

// 处理长按结束
const handleTouchEnd = () => {
  if (longPressTimer.value) {
    clearTimeout(longPressTimer.value);
    longPressTimer.value = null;
  }
};

// 处理触摸移动（取消长按）
const handleTouchMove = () => {
  if (longPressTimer.value) {
    clearTimeout(longPressTimer.value);
    longPressTimer.value = null;
  }
};

// 复制消息
const handleCopy = async () => {
  if (!selectedMessage.value || selectedMessage.value.messageType !== 1) {
    return;
  }

  try {
    // 使用现代浏览器的 Clipboard API
    if (navigator.clipboard && navigator.clipboard.writeText) {
      await navigator.clipboard.writeText(selectedMessage.value.content);
      ElMessage.success("消息已复制到剪贴板");
    } else {
      // 降级方案：使用传统的 document.execCommand
      const textArea = document.createElement("textarea");
      textArea.value = selectedMessage.value.content;
      textArea.style.position = "fixed";
      textArea.style.left = "-999999px";
      textArea.style.top = "-999999px";
      document.body.appendChild(textArea);
      textArea.focus();
      textArea.select();

      try {
        document.execCommand("copy");
        ElMessage.success("消息已复制到剪贴板");
      } catch (err) {
        console.error("复制失败:", err);
        ElMessage.error("复制失败，请手动复制");
      }

      document.body.removeChild(textArea);
    }
  } catch (error) {
    console.error("复制消息失败:", error);
    ElMessage.error("复制失败，请手动复制");
  }

  // 关闭右键菜单
  contextMenuVisible.value = false;
  selectedMessage.value = null;
};

// 撤回消息
const handleRevoke = async () => {
  if (!selectedMessage.value) {
    return;
  }

  // 调用 WebSocket 撤回消息（等待后端响应，不再乐观更新）
  WebSocketClient.revokeMessage(selectedMessage.value.id);

  // 关闭右键菜单
  contextMenuVisible.value = false;
  selectedMessage.value = null;
};

// 显示撤回提示
const showRevokeNotification = (text) => {
  revokeNotification.value = text;

  // 清除之前的定时器
  if (revokeNotificationTimer.value) {
    clearTimeout(revokeNotificationTimer.value);
  }

  // 10秒后自动隐藏
  revokeNotificationTimer.value = setTimeout(() => {
    revokeNotification.value = "";
    revokeNotificationTimer.value = null;
  }, 10000);

  // 滚动到底部以显示提示
  scrollToBottom();
};

// 关闭右键菜单（点击其他地方）
const closeContextMenu = (event) => {
  contextMenuVisible.value = false;
  selectedMessage.value = null;

  // 如果点击的不是表情按钮和表情选择器本身，则关闭表情选择器
  if (event && showEmojiPicker.value) {
    const target = event.target;
    const emojiPicker = document.querySelector(".emoji-picker");
    const emojiButton = document.querySelector(".input-actions .el-button");

    if (emojiPicker && !emojiPicker.contains(target) && emojiButton && !emojiButton.contains(target)) {
      closeEmojiPicker();
    }
  }
};

// WebSocket 消息处理
const handleNewMessage = (data) => {
  if (data.fromUserId === targetUserId.value || data.toUserId === targetUserId.value) {
    // 补充用户信息
    const message = {
      ...data,
      id: data.messageId || data.id, // 确保有消息ID
      fromUserNickname: data.fromUserNickname || targetUser.value?.nickname,
      fromUserAvatar: data.fromUserAvatar || targetUser.value?.avatar,
      toUserNickname: data.toUserNickname || userStore.user?.nickname,
      toUserAvatar: data.toUserAvatar || userStore.user?.avatar,
    };
    // 将消息添加到当前聊天窗口
    messageStore.addMessageToCurrentChat(message);
    scrollToBottom();

    // 如果是收到对方的消息，立即标记为已读
    if (data.fromUserId === targetUserId.value) {
      // 清空前端会话未读数
      messageStore.clearConversationUnread(targetUserId.value);
      // 通知后端标记为已读
      WebSocketClient.markAsRead(targetUserId.value);
    }
  }
};

const handleSendSuccess = (data) => {
  // 消息发送成功，更新临时ID为真实ID
  const messages = messageStore.currentChatMessages;
  const lastMessage = messages[messages.length - 1];
  if (lastMessage && lastMessage.id > Date.now() - 5000) {
    // 更新最后一条消息的ID
    lastMessage.id = data.messageId;
  }
};

// 处理消息已读回执
const handleMessageRead = (data) => {
  // 对方已读我的消息，更新当前聊天窗口中我发送的消息为已读状态
  if (data.fromUserId === targetUserId.value) {
    messageStore.currentChatMessages.forEach((msg) => {
      if (msg.fromUserId === currentUserId.value && msg.toUserId === targetUserId.value) {
        msg.isRead = 1;
      }
    });
  }
};

// 处理用户在线状态变化
const handleUserOnlineStatus = (data) => {
  // 如果是当前聊天的用户，更新在线状态
  if (data.userId === targetUserId.value) {
    isOnline.value = data.isOnline;
    // 同时更新 messageStore 中的在线状态
    messageStore.updateUserOnlineStatus(data.userId, data.isOnline);
  }
};

// 处理撤回成功响应
const handleRevokeSuccess = (data) => {
  // 从消息列表中移除该消息
  const messageIndex = messageStore.currentChatMessages.findIndex((msg) => msg.id === data.messageId);
  if (messageIndex > -1) {
    const isLastMessage = messageIndex === messageStore.currentChatMessages.length - 1;
    messageStore.currentChatMessages.splice(messageIndex, 1);

    // 如果撤回的是最后一条消息，更新会话列表中的最后一条消息内容
    if (isLastMessage) {
      messageStore.updateConversationLastMessage(targetUserId.value, "你撤回了一条消息");
    }
  }

  // 显示撤回提示
  showRevokeNotification("你撤回了一条消息");
  ElMessage.success("消息已撤回");
};

// 处理撤回失败响应
const handleRevokeFailed = (data) => {
  ElMessage.error(data.message || "撤回消息失败");
  console.error("撤回消息失败:", data.message);
};

// 处理消息撤回通知（对方撤回）
const handleMessageRevoke = (data) => {
  // 查找并从列表中移除被撤回的消息
  const messageIndex = messageStore.currentChatMessages.findIndex((msg) => msg.id === data.messageId);
  if (messageIndex > -1) {
    const message = messageStore.currentChatMessages[messageIndex];
    const isLastMessage = messageIndex === messageStore.currentChatMessages.length - 1;
    messageStore.currentChatMessages.splice(messageIndex, 1);

    // 如果是对方撤回的消息，显示提示
    if (message.fromUserId === targetUserId.value) {
      const revokeText = `${message.fromUserNickname || "对方"}撤回了一条消息`;
      showRevokeNotification(revokeText);

      // 如果撤回的是最后一条消息，更新会话列表中的最后一条消息内容
      if (isLastMessage) {
        messageStore.updateConversationLastMessage(targetUserId.value, revokeText);
      }
    } else {
      // 自己在其他设备撤回的消息
      if (isLastMessage) {
        messageStore.updateConversationLastMessage(targetUserId.value, "你撤回了一条消息");
      }
    }
  }
};

// 组件挂载
onMounted(async () => {
  // 设置当前聊天用户
  messageStore.setCurrentChatUser(targetUserId.value);
  fetchTargetUser();
  fetchChatHistory();

  // 如果会话列表为空（刷新页面时），主动获取会话列表以获取在线状态
  if (messageStore.conversationList.length === 0) {
    try {
      const res = await getConversationList();
      messageStore.setConversationList(res.data.data);

      // 在获取会话列表后立即更新在线状态
      const conversation = messageStore.conversationList.find((conv) => conv.targetUserId === targetUserId.value);
      if (conversation) {
        isOnline.value = conversation.isOnline || false;
      }
    } catch (error) {
      console.error("获取会话列表失败:", error);
    }
  } else {
    // 从已有的会话列表中获取初始在线状态
    const conversation = messageStore.conversationList.find((conv) => conv.targetUserId === targetUserId.value);
    if (conversation) {
      isOnline.value = conversation.isOnline || false;
    }
  }

  // 注意：不要在这里初始化 WebSocket，Header.vue 已经全局初始化了
  // 只需要监听消息事件
  WebSocketClient.on("NEW_MESSAGE", handleNewMessage);
  WebSocketClient.on("SEND_SUCCESS", handleSendSuccess);
  WebSocketClient.on("MESSAGE_READ", handleMessageRead);
  WebSocketClient.on("USER_ONLINE_STATUS", handleUserOnlineStatus);
  WebSocketClient.on("REVOKE_SUCCESS", handleRevokeSuccess);
  WebSocketClient.on("REVOKE_FAILED", handleRevokeFailed);
  WebSocketClient.on("MESSAGE_REVOKED", handleMessageRevoke);

  // 监听点击事件，关闭右键菜单
  document.addEventListener("click", closeContextMenu);
});

// 组件卸载
onUnmounted(() => {
  WebSocketClient.off("NEW_MESSAGE", handleNewMessage);
  WebSocketClient.off("SEND_SUCCESS", handleSendSuccess);
  WebSocketClient.off("MESSAGE_READ", handleMessageRead);
  WebSocketClient.off("USER_ONLINE_STATUS", handleUserOnlineStatus);
  WebSocketClient.off("REVOKE_SUCCESS", handleRevokeSuccess);
  WebSocketClient.off("REVOKE_FAILED", handleRevokeFailed);
  WebSocketClient.off("MESSAGE_REVOKED", handleMessageRevoke);
  messageStore.setCurrentChatUser(null);

  // 清理事件监听器
  document.removeEventListener("click", closeContextMenu);

  // 清理长按定时器
  if (longPressTimer.value) {
    clearTimeout(longPressTimer.value);
  }

  // 清理撤回提示定时器
  if (revokeNotificationTimer.value) {
    clearTimeout(revokeNotificationTimer.value);
  }
});
</script>

<style lang="scss" scoped>
.chat-window-page {
  min-height: 100vh; // 减去 Layout 的 padding-top
  padding-top: 70px !important;

  .container {
    max-width: 1000px;
    margin: 0 auto;
    padding: 0 10px;
  }

  .chat-window-container {
    background: var(--el-bg-color);
    border-radius: 8px;
    box-shadow: 0 2px 12px var(--el-border-color-light);
    overflow: hidden;
    display: flex;
    flex-direction: column;
    height: calc(100vh - 100px);

    .chat-header {
      display: flex;
      align-items: center;
      padding: 15px 20px;
      border-bottom: 1px solid var(--el-border-color-light);

      .user-info {
        display: flex;
        align-items: center;
        margin-left: 15px;

        .avatar-wrapper {
          position: relative;
          cursor: pointer;
          transition: transform 0.3s;

          &:hover {
            transform: scale(1.05);
          }

          .online-indicator {
            position: absolute;
            bottom: 0;
            right: 0;
            width: 12px;
            height: 12px;
            background: var(--el-color-success);
            border: 2px solid var(--el-bg-color);
            border-radius: 50%;
          }
        }

        .user-details {
          margin-left: 10px;

          .username {
            font-size: 16px;
            font-weight: 600;
            color: var(--el-text-color-primary);
            display: block;
          }

          .online-status-text {
            font-size: 12px;
            color: var(--el-color-success);
          }
        }
      }
    }

    .message-list {
      flex: 1;
      overflow-y: auto;
      padding: 20px;

      // 撤回提示
      .revoke-notification {
        text-align: center;
        padding: 8px 0;
        margin: 10px 0;

        span {
          display: inline-block;
          padding: 6px 16px;
          background: var(--el-fill-color-light);
          color: var(--el-text-color-regular);
          font-size: 13px;
          border-radius: 4px;
        }
      }

      .message-item {
        display: flex;
        margin-bottom: 20px;

        &.is-mine {
          flex-direction: row-reverse;

          .message-content {
            align-items: flex-end;
            margin-left: 0;
            margin-right: 12px;

            .message-header {
              flex-direction: row-reverse;
            }

            .message-body {
              background: var(--el-color-primary);
              color: white;

              // 图片消息不需要蓝色背景
              &:has(.image-message) {
                background: transparent;
              }
            }
          }
        }

        .message-avatar {
          flex-shrink: 0;
          cursor: pointer;
          transition: transform 0.3s;

          &:hover {
            transform: scale(1.05);
          }
        }

        .message-content {
          display: flex;
          flex-direction: column;
          margin-left: 10px;
          max-width: 60%;

          .message-header {
            display: flex;
            align-items: center;
            gap: 10px;
            margin-bottom: 5px;

            .message-nickname {
              font-size: 14px;
              color: var(--el-text-color-regular);
            }

            .message-time {
              font-size: 12px;
              color: var(--el-text-color-secondary);
            }
          }

          .message-body {
            padding: 10px 15px;
            border-radius: 8px;
            background: var(--el-bg-color-page);

            // 图片消息不需要气泡样式
            &:has(.image-message) {
              padding: 0;
              background: transparent;
            }

            .text-message {
              word-break: break-word;
            }

            .image-message {
              max-width: 200px;
              max-height: 200px;
              border-radius: 8px;
              cursor: pointer;
              transition: transform 0.3s ease;

              &:hover {
                transform: scale(1.02);
              }

              .image-loading {
                display: flex;
                justify-content: center;
                align-items: center;
                width: 200px;
                height: 200px;
                background: var(--el-fill-color-light);
                border-radius: 8px;
                font-size: 14px;
                color: var(--el-text-color-secondary);
              }

              .image-error {
                display: flex;
                flex-direction: column;
                justify-content: center;
                align-items: center;
                gap: 8px;
                width: 200px;
                height: 200px;
                background: var(--el-fill-color-light);
                border-radius: 8px;
                color: var(--el-text-color-secondary);

                .el-icon {
                  font-size: 32px;
                }

                span {
                  font-size: 14px;
                }
              }
            }
          }

          // 已读/未读状态
          .read-status {
            margin-top: 4px;
            font-size: 12px;
            text-align: right;

            .read-text {
              color: var(--el-text-color-secondary);
            }

            .unread-text {
              color: var(--el-text-color-placeholder);
            }
          }
        }
      }
    }

    .message-input-area {
      position: relative;
      border-top: 1px solid var(--el-border-color-light);
      padding: 15px 20px;

      .input-actions {
        display: flex;
        justify-content: space-between;
        align-items: center;
        gap: 10px;
        margin-top: 10px;

        .input-actions-left {
          display: flex;
          gap: 10px;
        }
      }

      // Emoji 表情选择器
      .emoji-picker {
        position: absolute;
        bottom: calc(100% + 10px);
        left: 20px;
        width: 360px;
        max-height: 320px;
        background: var(--el-bg-color-overlay);
        backdrop-filter: blur(10px);
        border: 1px solid var(--el-border-color-light);
        border-radius: 12px;
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
        z-index: 1000;
        overflow: hidden;
        animation: emojiPickerFadeIn 0.2s ease;

        @keyframes emojiPickerFadeIn {
          from {
            opacity: 0;
            transform: translateY(10px);
          }
          to {
            opacity: 1;
            transform: translateY(0);
          }
        }

        // 表情选择器头部
        .emoji-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 12px 16px;
          border-bottom: 1px solid var(--el-border-color-light);
          background: var(--el-bg-color);

          .emoji-title {
            font-size: 14px;
            font-weight: 600;
            color: var(--el-text-color-primary);
          }
        }

        // 表情列表
        .emoji-list {
          display: grid;
          grid-template-columns: repeat(8, 1fr);
          gap: 4px;
          padding: 12px;
          max-height: 260px;
          overflow-y: auto;

          // 滚动条样式
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

          &::-webkit-scrollbar-track {
            background: transparent;
          }

          // 表情项
          .emoji-item {
            display: flex;
            justify-content: center;
            align-items: center;
            width: 36px;
            height: 36px;
            font-size: 24px;
            cursor: pointer;
            border-radius: 6px;
            transition: all 0.2s ease;
            user-select: none;

            &:hover {
              background: var(--el-fill-color-light);
              transform: scale(1.2);
            }

            &:active {
              transform: scale(1.1);
            }
          }
        }

        // 移动端适配
        @media (max-width: 768px) {
          left: 10px;
          right: 10px;
          width: auto;

          .emoji-list {
            grid-template-columns: repeat(6, 1fr);
          }
        }
      }
    }
  }

  // 右键菜单
  .context-menu {
    position: fixed;
    background: var(--el-bg-color-overlay);
    backdrop-filter: blur(10px);
    border: 1px solid var(--el-border-color-light);
    border-radius: 8px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
    padding: 8px;
    z-index: 9999;
    min-width: 120px;

    .context-menu-item {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 10px 16px;
      cursor: pointer;
      border-radius: 6px;
      transition: all 0.2s ease;
      color: var(--el-text-color-primary);
      font-size: 14px;

      &:not(:last-child) {
        margin-bottom: 4px;
      }

      &:hover {
        background: var(--el-fill-color-light);
      }

      // 复制菜单项悬停效果
      &:first-child:hover {
        color: var(--el-color-primary);
      }

      // 撤回菜单项悬停效果（危险操作）
      &:last-child:hover {
        color: var(--el-color-danger);
      }

      .el-icon {
        font-size: 16px;
      }
    }
  }
}
</style>
