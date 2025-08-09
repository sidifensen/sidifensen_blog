import request from "@/utils/Request";

// 获取登录用户的菜单
export function getMenuList() {
  return request({
    url: "/menu/list",
    method: "get",
  });
}

// 获取所有菜单
export function getAllMenuList() {
  return request({
    url: "/menu/listAll",
    method: "get",
  });
}

// 新增菜单
export function addMenu(data) {
  return request({
    url: "/menu/add",
    method: "post",
    data,
  });
}
// 更新菜单
export function updateMenu(data) {
  return request({
    url: "/menu/update",
    method: "put",
    data,
  });
}

// 删除菜单
export function deleteMenu(menuId) {
  return request({
    url: `/menu/${menuId}`,
    method: "delete",
  });
}

// 查询菜单
export function queryMenu(name) {
  return request({
    url: "/menu/search",
    method: "get",
    params: {
      name,
    },
  });
}
