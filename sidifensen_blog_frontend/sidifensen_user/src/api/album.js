import request from '@/utils/Request'

// 查看相册详情
export function getAlbum(albumId) {
  return request({
    url: `/album/${albumId}`,
    method: 'get'
  })
}

// 新建相册
export function createAlbum(data) {
  return request({
    url: '/album/create',
    method: 'post',
    data
  })
}

// 更新相册
export function updateAlbum(data) {
  return request({
    url: '/album/update',
    method: 'put',
    data
  })
}

// 删除相册
export function deleteAlbum(albumId) {
  return request({
    url: `/album/${albumId}`,
    method: 'delete'
  })
}

// 查看用户所有的相册
export function listAlbum() {
  return request({
    url: '/album/list',
    method: 'get'
  })
}

// 修改相册展示状态
export function changeShowStatus(data) {
  return request({
    url: '/album/changeShowStatus',
    method: 'put',
    data
  })
}

// 相册更换封面
export function changeCover(data) {
  return request({
    url: '/album/changeCover',
    method: 'put',
    data
  })
}