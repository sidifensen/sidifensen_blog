import request from "@/utils/Request";

// 获取会员经营总览
export function getVipDashboard() {
  return request({
    url: "/vip/admin/dashboard",
    method: "get",
  });
}

// 分页获取会员列表
export function getVipMemberPage(params) {
  return request({
    url: "/vip/admin/member/page",
    method: "get",
    params,
  });
}

// 获取会员详情
export function getVipMemberDetail(userId) {
  return request({
    url: `/vip/admin/member/${userId}`,
    method: "get",
  });
}

// 调整会员状态
export function adjustVipMember(data) {
  return request({
    url: "/vip/admin/member/adjust",
    method: "post",
    data,
  });
}

// 分页获取会员订单
export function getVipOrderPage(params) {
  return request({
    url: "/vip/admin/order/page",
    method: "get",
    params,
  });
}

// 获取套餐列表
export function getVipPlanList() {
  return request({
    url: "/vip/admin/plan/list",
    method: "get",
  });
}

// 更新套餐
export function updateVipPlan(data) {
  return request({
    url: "/vip/admin/plan/update",
    method: "post",
    data,
  });
}
