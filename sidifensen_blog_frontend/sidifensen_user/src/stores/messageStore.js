import { defineStore } from "pinia";
import { ref } from "vue";

export const useMessageStore = defineStore(
  "message",
  () => {
    // 会话列表
    const conversationList = ref([]);

    // 总未读数
    const totalUnreadCount = ref(0);

    // 当前聊天窗口的用户ID
    const currentChatUserId = ref(null);

    // 当前聊天窗口的消息列表
    const currentChatMessages = ref([]);

    /**
     * 设置会话列表
     */
    const setConversationList = (list) => {
      conversationList.value = list;
      // 计算总未读数
      totalUnreadCount.value = list.reduce((sum, conv) => sum + conv.unreadCount, 0);
    };

    /**
     * 更新会话（收到新消息时）
     * @param userId - 发送消息的用户ID
     * @param message - 消息内容
     * @param unreadCount - 可选，后端返回的最新未读数
     * @param userInfo - 可选，用户信息（昵称、头像等）
     */
    const updateConversation = (userId, message, unreadCount, userInfo) => {
      const index = conversationList.value.findIndex((conv) => conv.targetUserId === userId);

      if (index !== -1) {
        // 更新已有会话
        const conv = conversationList.value[index];
        const oldUnreadCount = conv.unreadCount || 0;

        // 创建新的会话对象（确保触发响应式更新）
        const updatedConv = {
          ...conv,
          lastMessageContent: message.content,
          lastMessageTime: message.createTime,
        };

        // 更新用户信息（如果提供了）
        if (userInfo) {
          if (userInfo.nickname) updatedConv.targetUserNickname = userInfo.nickname;
          if (userInfo.avatar) updatedConv.targetUserAvatar = userInfo.avatar;
        }

        // 如果后端传了最新的未读数，直接使用
        if (typeof unreadCount === "number") {
          updatedConv.unreadCount = unreadCount;
          // 更新总未读数
          totalUnreadCount.value = totalUnreadCount.value - oldUnreadCount + unreadCount;
        } else {
          // 如果后端没传，且不是当前聊天窗口，才增加未读数
          if (currentChatUserId.value !== userId) {
            updatedConv.unreadCount = oldUnreadCount + 1;
            totalUnreadCount.value++;
          } else {
            updatedConv.unreadCount = oldUnreadCount;
          }
        }

        // 将有新消息的会话移到列表最顶（使用 splice 确保触发响应式更新）
        // 先删除，再插入，确保列表最顶部有该会话
        conversationList.value.splice(index, 1);
        conversationList.value.unshift(updatedConv);
      } else {
        // 创建新会话
        const newUnreadCount = typeof unreadCount === "number" ? unreadCount : currentChatUserId.value === userId ? 0 : 1;

        const newConversation = {
          id: userId, // 添加 id 字段，确保 v-for 的 key 正确
          targetUserId: userId,
          targetUserNickname: userInfo?.nickname || "新用户",
          targetUserAvatar: userInfo?.avatar || "",
          lastMessageContent: message.content,
          lastMessageTime: message.createTime,
          unreadCount: newUnreadCount,
          isOnline: false,
        };

        conversationList.value.unshift(newConversation);
        totalUnreadCount.value += newUnreadCount;
      }
    };

    /**
     * 清空某个会话的未读数
     */
    const clearConversationUnread = (userId) => {
      const conv = conversationList.value.find((c) => c.targetUserId === userId);
      if (conv && conv.unreadCount > 0) {
        totalUnreadCount.value -= conv.unreadCount;
        conv.unreadCount = 0;
      }
    };

    /**
     * 设置当前聊天用户
     */
    const setCurrentChatUser = (userId) => {
      currentChatUserId.value = userId;
      clearConversationUnread(userId);
    };

    /**
     * 添加消息到当前聊天窗口
     */
    const addMessageToCurrentChat = (message) => {
      currentChatMessages.value.push(message);
    };

    /**
     * 设置当前聊天消息列表
     */
    const setCurrentChatMessages = (messages) => {
      currentChatMessages.value = messages;
    };

    /**
     * 更新消息状态（撤回）
     */
    const revokeMessageInCurrentChat = (messageId) => {
      const message = currentChatMessages.value.find((m) => m.id === messageId);
      if (message) {
        message.isRevoked = 1;
      }
    };

    /**
     * 删除会话
     */
    const removeConversation = (userId) => {
      const index = conversationList.value.findIndex((conv) => conv.targetUserId === userId);
      if (index !== -1) {
        const conv = conversationList.value[index];
        totalUnreadCount.value -= conv.unreadCount;
        conversationList.value.splice(index, 1);
      }
    };

    /**
     * 更新用户在线状态
     */
    const updateUserOnlineStatus = (userId, isOnline) => {
      const conv = conversationList.value.find((c) => c.targetUserId === userId);
      if (conv) {
        conv.isOnline = isOnline;
      }
    };

    /**
     * 更新会话的最后一条消息内容（用于撤回消息）
     */
    const updateConversationLastMessage = (userId, content) => {
      const conv = conversationList.value.find((c) => c.targetUserId === userId);
      if (conv) {
        conv.lastMessageContent = content;
      }
    };

    /**
     * 清空所有数据
     */
    const clearAll = () => {
      conversationList.value = [];
      totalUnreadCount.value = 0;
      currentChatUserId.value = null;
      currentChatMessages.value = [];
    };

    return {
      conversationList,
      totalUnreadCount,
      currentChatUserId,
      currentChatMessages,
      setConversationList,
      updateConversation,
      clearConversationUnread,
      setCurrentChatUser,
      addMessageToCurrentChat,
      setCurrentChatMessages,
      revokeMessageInCurrentChat,
      removeConversation,
      updateUserOnlineStatus,
      updateConversationLastMessage,
      clearAll,
    };
  },
  {
    persist: false, // 不持久化，每次刷新页面重新获取
  }
);
