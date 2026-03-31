import request from '@/utils/request'

// 分页获取公告列表
export function getAnnouncementPage(pageNum = 1, pageSize = 20) {
  return request({
    url: '/announcement/page',
    method: 'get',
    params: { pageNum, pageSize }
  })
}

// 标记公告已读
export function readAnnouncement(id) {
  return request({
    url: `/announcement/${id}/read`,
    method: 'put'
  })
}

// 获取已读公告ID列表
export function getReadAnnouncementIds() {
  return request({
    url: '/announcement/read-ids',
    method: 'get'
  })
}
