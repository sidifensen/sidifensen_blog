import request from "@/utils/Request";

/**
 * 查询所有登录日志（按时间倒序）
 */
export const getLoginLogList = () => {
  return request({
    url: "/loginLog/admin/list",
    method: "get",
  });
};

/**
 * 搜索登录日志
 * @param {Object} queryDto 查询条件
 */
export const searchLoginLog = (queryDto) => {
  return request({
    url: "/loginLog/admin/search",
    method: "post",
    data: queryDto,
  });
};

/**
 * 批量删除登录日志
 * @param {Array} ids 登录日志ID列表
 */
export const deleteLoginLogs = (ids) => {
  return request({
    url: "/loginLog/admin/batch",
    method: "delete",
    data: ids,
  });
};
