import request from "@/utils/Request";

/**
 * 查询所有访客日志（按时间倒序）
 */
export const getVisitorLogList = () => {
  return request({
    url: "/visitorLog/admin/list",
    method: "get",
  });
};

/**
 * 搜索访客日志
 * @param {Object} queryDto 查询条件
 */
export const searchVisitorLog = (queryDto) => {
  return request({
    url: "/visitorLog/admin/search",
    method: "post",
    data: queryDto,
  });
};

/**
 * 批量删除访客日志
 * @param {Array} ids 访客日志ID列表
 */
export const deleteVisitorLogs = (ids) => {
  return request({
    url: "/visitorLog/admin/batch",
    method: "delete",
    data: ids,
  });
};

/**
 * 获取访客统计数据
 */
export const getVisitorStatistics = () => {
  return request({
    url: "/visitorLog/statistics",
    method: "get",
  });
};

/**
 * 获取访客趋势
 * @param {Number} days 天数，默认7天
 */
export const getVisitorTrend = (days = 7) => {
  return request({
    url: "/visitorLog/trend",
    method: "get",
    params: { days },
  });
};

/**
 * 获取今日访问量
 */
export const getTodayVisitorCount = () => {
  return request({
    url: "/visitorLog/today/count",
    method: "get",
  });
};
