import request from "@/utils/Request";

// 获取菜单
export function getMenuList() {
  return request({
    url: "/menu/list",
    method: "get",
  });
}
