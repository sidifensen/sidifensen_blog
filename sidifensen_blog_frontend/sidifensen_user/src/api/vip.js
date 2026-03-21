import request from "@/utils/Request";

// 获取当前启用的 VIP 套餐列表
export function getVipPlans() {
  return request({
    url: "/vip/plans",
    method: "get",
  });
}

// 创建一笔 VIP 订单
export function createVipOrder(data) {
  return request({
    url: "/vip/orders",
    method: "post",
    data,
  });
}

// 查询单笔 VIP 订单状态
export function getVipOrder(orderNo) {
  return request({
    url: `/vip/orders/${orderNo}`,
    method: "get",
  });
}

// 分页查询当前用户的 VIP 订单
export function getVipOrderList(pageNum = 1, pageSize = 10) {
  return request({
    url: `/vip/orders?pageNum=${pageNum}&pageSize=${pageSize}`,
    method: "get",
  });
}

// 获取当前登录用户的会员状态与 AI 配额
export function getVipMe() {
  return request({
    url: "/vip/me",
    method: "get",
  });
}

// 对待支付订单进行再次支付
export function repayVipOrder(orderNo) {
  return request({
    url: `/vip/orders/${orderNo}/pay`,
    method: "post",
  });
}

// 取消待支付订单
export function cancelVipOrder(orderNo) {
  return request({
    url: `/vip/orders/${orderNo}/cancel`,
    method: "post",
  });
}
