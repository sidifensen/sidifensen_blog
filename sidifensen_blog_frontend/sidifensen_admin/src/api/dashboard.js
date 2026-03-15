import request from "@/utils/Request";

/**
 * 获取管理端首页统计数据
 * @param {Number} trendDays 访客趋势天数，默认 7 天（可选：7/14/30）
 */
export function getDashboardStatistics(trendDays = 7) {
  return request({
    url: "/dashboard/statistics",
    method: "get",
    params: { trendDays },
  });
}

/**
 * 刷新 Dashboard 缓存
 */
export function refreshDashboardCache() {
  return request({
    url: "/dashboard/refresh",
    method: "post",
  });
}
