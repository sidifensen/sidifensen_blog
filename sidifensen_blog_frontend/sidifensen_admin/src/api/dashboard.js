import request from '@/utils/Request'

/**
 * 获取管理端首页统计数据
 * @param {Number} trendDays 访客趋势天数，默认 7 天（可选：7/14/30）
 */
export function getDashboardStatistics(trendDays = 7) {
  return request({
    url: '/dashboard/statistics',
    method: 'get',
    params: { trendDays },
  })
}

/**
 * 刷新 Dashboard 缓存
 */
export function refreshDashboardCache() {
  return request({
    url: '/dashboard/refresh',
    method: 'post',
  })
}

/**
 * 获取近7天文章和用户增长趋势
 */
export function getWeeklyTrend() {
  return request({
    url: '/dashboard/weekly-trend',
    method: 'get',
  })
}

/**
 * 获取用户角色分布
 */
export function getUserDistribution() {
  return request({
    url: '/dashboard/user-distribution',
    method: 'get',
  })
}

/**
 * 获取内容模块活跃度
 */
export function getContentActivity() {
  return request({
    url: '/dashboard/content-activity',
    method: 'get',
  })
}

/**
 * 获取待审核数量统计
 */
export function getExamineCount() {
  return request({
    url: '/dashboard/examine-count',
    method: 'get',
  })
}

/**
 * 获取 VIP 统计数据
 */
export function getVipStatistics() {
  return request({
    url: '/dashboard/vip-statistics',
    method: 'get',
  })
}

/**
 * 获取访客趋势数据
 * @param {Number} days 天数（可选：7/14/30）
 */
export function getVisitorTrend(days = 7) {
  return request({
    url: '/dashboard/visitor-trend',
    method: 'get',
    params: { days },
  })
}

/**
 * 获取近7天互动趋势（评论数、点赞数、收藏数）
 */
export function getInteractionTrend() {
  return request({
    url: '/dashboard/interaction-trend',
    method: 'get',
  })
}
