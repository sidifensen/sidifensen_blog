import request from "@/utils/Request";

/**
 * 获取用户通知列表
 * @param {number} type - 消息类型：1-评论 2-点赞 3-收藏 4-关注
 * @param {number} pageNum - 页码
 * @param {number} pageSize - 每页数量
 */
export function getUserNotifications(type, pageNum, pageSize) {
  return request({
    url: "/message/notifications",
    method: "get",
    params: {
      type,
      pageNum,
      pageSize,
    },
  });
}

/**
 * 获取未读通知数量（按类型统计）
 */
export function getUnreadNotificationCount() {
  return request({
    url: "/message/notifications/unread/count",
    method: "get",
  });
}

/**
 * 标记通知为已读
 * @param {Array<number>} messageIds - 消息ID列表
 */
export function markNotificationsAsRead(messageIds) {
  return request({
    url: "/message/notifications/read",
    method: "put",
    data: messageIds,
  });
}

/**
 * 删除通知
 * @param {Array<number>} messageIds - 消息ID列表
 */
export function deleteNotifications(messageIds) {
  return request({
    url: "/message/notifications",
    method: "delete",
    data: messageIds,
  });
}
