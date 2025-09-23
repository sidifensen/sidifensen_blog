import request from "@/utils/Request";

// 管理员获取所有专栏列表
export function adminGetColumnList(data) {
  return request({
    url: "/column/admin/list",
    method: "post",
    data: data,
  });
}

// 管理员根据用户ID获取专栏列表
export function adminGetColumnsByUserId(userId) {
  return request({
    url: `/column/admin/user/${userId}`,
    method: "get",
  });
}

// 管理员搜索专栏
export function adminSearchColumn(data) {
  return request({
    url: "/column/admin/search",
    method: "post",
    data: data,
  });
}

// 管理员审核专栏
export function adminExamineColumn(columnId, examineStatus) {
  return request({
    url: `/column/admin/${columnId}/examine`,
    method: "put",
    params: {
      examineStatus: examineStatus,
    },
  });
}

// 管理员批量审核专栏
export function adminBatchExamineColumn(columnIds, examineStatus) {
  return request({
    url: "/column/admin/batch/examine",
    method: "put",
    data: columnIds,
    params: {
      examineStatus: examineStatus,
    },
  });
}

// 管理员删除专栏
export function adminDeleteColumn(columnId) {
  return request({
    url: `/column/admin/${columnId}`,
    method: "delete",
  });
}

// 管理员批量删除专栏
export function adminBatchDeleteColumn(columnIds) {
  return request({
    url: "/column/admin/batch",
    method: "delete",
    data: columnIds,
  });
}

// 获取用户列表（包含专栏数量）
export function getUserListWithColumnCount() {
  return request({
    url: "/user/admin/listWithColumnCount",
    method: "get",
  });
}

// 获取专栏统计数据
export function getColumnStatistics() {
  return request({
    url: "/column/admin/statistics",
    method: "get",
  });
}

// 管理员更新专栏信息
export function adminUpdateColumn(data) {
  return request({
    url: "/column/admin/update",
    method: "put",
    data: data,
  });
}
