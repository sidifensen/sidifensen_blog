import request from "@/utils/Request";

// 获取管理员消息数量
export function getMessagesCount() {
  return request({
    url: '/message/admin/count',
    method: 'get'
  });
}

// 获取管理员消息列表（仅 type=0 系统通知，用于 Bell 下拉，全量返回）
export function getMessageList() {
  return request({
    url: '/message/admin/list',
    method: 'get'
  });
}

// 分页获取管理员消息列表（仅 type=0 系统通知，用于管理页面）
export function getMessagePage(params) {
  return request({
    url: '/message/admin/page',
    method: 'get',
    params
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


