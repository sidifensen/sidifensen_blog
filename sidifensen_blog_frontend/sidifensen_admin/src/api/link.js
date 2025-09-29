import request from "@/utils/Request";

/**
 * 管理员获取所有友链列表
 * @returns {Promise} 友链列表
 */
export function adminGetLinkList() {
  return request({
    url: "/link/admin/list",
    method: "get",
  });
}

/**
 * 管理员搜索友链
 * @param {Object} searchData 搜索条件
 * @returns {Promise} 搜索结果
 */
export function adminSearchLink(searchData) {
  return request({
    url: "/link/admin/search",
    method: "post",
    data: searchData,
  });
}

/**
 * 管理员审核友链
 * @param {Number} linkId 友链ID
 * @param {Number} examineStatus 审核状态 0-待审核 1-审核通过 2-审核未通过
 * @returns {Promise} 操作结果
 */
export function adminExamineLink(linkId, examineStatus) {
  return request({
    url: "/link/admin/examine",
    method: "put",
    data: {
      linkId,
      examineStatus,
    },
  });
}

/**
 * 管理员批量审核友链
 * @param {Array} linkIds 友链ID数组
 * @param {Number} examineStatus 审核状态 0-待审核 1-审核通过 2-审核未通过
 * @returns {Promise} 操作结果
 */
export function adminBatchExamineLink(linkIds, examineStatus) {
  const linkAuditDtos = linkIds.map((linkId) => ({
    linkId,
    examineStatus,
  }));
  return request({
    url: "/link/admin/examine/batch",
    method: "put",
    data: linkAuditDtos,
  });
}

/**
 * 管理员删除友链
 * @param {Number} linkId 友链ID
 * @returns {Promise} 操作结果
 */
export function adminDeleteLink(linkId) {
  return request({
    url: `/link/admin/${linkId}`,
    method: "delete",
  });
}

/**
 * 管理员批量删除友链
 * @param {Array} linkIds 友链ID数组
 * @returns {Promise} 操作结果
 */
export function adminBatchDeleteLink(linkIds) {
  return request({
    url: "/link/admin/delete/batch",
    method: "delete",
    data: linkIds,
  });
}
