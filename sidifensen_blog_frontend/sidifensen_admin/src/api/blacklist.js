import request from "@/utils/Request";

// 获取黑名单列表
export function getBlacklistList() {
  return request({
    url: "/blacklist/admin/list",
    method: "get",
  });
}

// 批量新增黑名单
export function addBlacklist(data) {
  return request({
    url: "/blacklist/admin/add",
    method: "post",
    data: data,
  });
}

// 搜索黑名单
export function searchBlacklist(data) {
  return request({
    url: "/blacklist/admin/search",
    method: "post",
    data: data,
  });
}

// 修改黑名单
export function updateBlacklist(data) {
  return request({
    url: "/blacklist/admin/update",
    method: "put",
    data: data,
  });
}

// 批量删除黑名单
export function deleteBlacklist(data) {
  return request({
    url: "/blacklist/admin/delete",
    method: "delete",
    data: data,
  });
}
