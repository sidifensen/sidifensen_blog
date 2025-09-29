import request from "@/utils/Request";

/**
 * 申请友链
 * @param {Object} data 友链申请信息
 * @returns {Promise} 请求结果
 */
export function applyLink(data) {
  return request({
    url: "/link/apply",
    method: "post",
    data,
  });
}

/**
 * 删除友链
 * @param {Number} linkId 友链ID
 * @returns {Promise} 请求结果
 */
export function deleteLink(linkId) {
  return request({
    url: `/link/${linkId}`,
    method: "delete",
  });
}

/**
 * 分页获取审核通过的友链列表
 * @param {Number} pageNum 页码
 * @param {Number} pageSize 页面大小
 * @returns {Promise} 友链列表
 */
export function getLinkList(pageNum, pageSize) {
  return request({
    url: `/link/list?pageNum=${pageNum}&pageSize=${pageSize}`,
    method: "get",
  });
}
