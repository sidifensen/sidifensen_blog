import request from "@/utils/Request";

/**
 * 查询所有操作日志（按时间倒序）
 */
export const getOperationLogList = (params) => {
  return request({
    url: "/operationlog/admin/list",
    method: "get",
    params,
  });
};

/**
 * 搜索操作日志
 * @param {Object} queryDto 查询条件
 */
export const searchOperationLog = (queryDto) => {
  return request({
    url: "/operationlog/admin/search",
    method: "post",
    data: queryDto,
  });
};

/**
 * 获取操作日志详情
 * @param {Number} id 操作日志 ID
 */
export const getOperationLogDetail = (id) => {
  return request({
    url: `/operationlog/admin/detail/${id}`,
    method: "get",
  });
};

/**
 * 批量删除操作日志
 * @param {Array} ids 操作日志 ID 列表
 */
export const deleteOperationLogs = (ids) => {
  return request({
    url: "/operationlog/admin/batch",
    method: "delete",
    data: ids,
  });
};
