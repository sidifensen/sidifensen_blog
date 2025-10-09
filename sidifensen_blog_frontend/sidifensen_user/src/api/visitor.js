import request from "@/utils/Request";

/**
 * 获取今日访问量（实时）
 * 公开接口，无需登录
 */
export function getTodayVisitorCount() {
  return request({
    url: "/visitorLog/today/count",
    method: "get",
  });
}
