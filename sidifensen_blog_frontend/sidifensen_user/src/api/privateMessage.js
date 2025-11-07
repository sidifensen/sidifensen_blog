import request from "@/utils/Request";

/**
 * 获取聊天记录
 */
export function getChatHistory(targetUserId, pageNum, pageSize) {
  return request({
    url: "/message/history",
    method: "get",
    params: {
      targetUserId,
      pageNum,
      pageSize,
    },
  });
}

/**
 * 撤回消息
 */
export function revokeMessage(messageId) {
  return request({
    url: `/message/revoke/${messageId}`,
    method: "put",
  });
}

/**
 * 删除消息
 */
export function deleteMessage(messageId) {
  return request({
    url: `/message/${messageId}`,
    method: "delete",
  });
}

/**
 * 获取未读消息数
 */
export function getUnreadCount() {
  return request({
    url: "/message/unread/count",
    method: "get",
  });
}
