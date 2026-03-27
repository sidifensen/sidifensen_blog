/**
 * 相册相关 API
 */
import { get, post } from './index'

/**
 * 获取相册列表
 * @param {Object} params 查询参数
 */
export function getAlbumList(params) {
  return get('/album/listAll', params)
}

/**
 * 获取相册详情
 * @param {number} id 相册ID
 */
export function getAlbumDetail(id) {
  return get(`/album/get/${id}`)
}

/**
 * 获取相册照片列表（已废弃，相册详情已包含照片）
 * @param {number} albumId 相册ID
 * @param {Object} params 查询参数
 * @deprecated 请使用 getAlbumDetail 获取相册详情，已包含照片列表
 */
export function getAlbumPhotos(albumId, params) {
  return get(`/album/get/${albumId}/photos`, params)
}
