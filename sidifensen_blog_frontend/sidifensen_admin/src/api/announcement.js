import request from "@/utils/Request";

// 分页获取公告列表（管理端）
export function getAnnouncementPage(params) {
  return request({
    url: '/announcement/admin/page',
    method: 'get',
    params
  });
}

// 创建公告
export function createAnnouncement(data) {
  return request({
    url: '/announcement',
    method: 'post',
    data
  });
}

// 取消待发送公告
export function cancelAnnouncement(id) {
  return request({
    url: `/announcement/${id}/cancel`,
    method: 'put'
  });
}

// 删除公告
export function deleteAnnouncement(id) {
  return request({
    url: `/announcement/${id}`,
    method: 'delete'
  });
}
