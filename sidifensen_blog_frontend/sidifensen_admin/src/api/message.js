import request from "@/utils/Request";

// 获取管理员消息数量
export function getMessagesCount() {
  return request({
    url: '/message/admin/count',
    method: 'get'
  });
}

// 获取管理员消息列表
export function getMessageList() {
  return request({
    url: '/message/admin/list',
    method: 'get'
  });
}

// 管理员读取消息/批量读取消息
export function readAdminMessages(data) {
  return request({
    url: '/message/admin/read',
    method: 'put',
    data
  });
}

// 管理员删除消息/批量删除消息
export function deleteAdminMessages(data) {
  return request({
    url: '/message/admin/delete',
    method: 'delete',
    data
  });
}


