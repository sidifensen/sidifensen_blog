import request from "@/utils/Request";

/**
 * 获取会话列表
 */
export function getConversationList() {
  return request({
    url: "/conversation/list",
    method: "get",
  });
}

/**
 * 删除会话
 */
export function deleteConversation(targetUserId) {
  return request({
    url: `/conversation/${targetUserId}`,
    method: "delete",
  });
}

/**
 * 清空未读数
 */
export function clearUnreadCount(targetUserId) {
  return request({
    url: `/conversation/read/${targetUserId}`,
    method: "put",
  });
}
