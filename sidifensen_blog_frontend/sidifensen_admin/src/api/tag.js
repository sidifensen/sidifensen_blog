import request from "@/utils/Request";

// 获取标签列表
export function getTagList() {
  return request({
    url: "/tag/list",
    method: "get",
  });
}

// 新增标签
export function addTag(data) {
  return request({
    url: "/tag/add",
    method: "post",
    data: data,
  });
}

// 批量删除标签
export function deleteTags(data) {
  return request({
    url: "/tag/delete",
    method: "delete",
    data: data,
  });
}

// 更新分类排序
export function updateCategorySort(data) {
  return request({
    url: "/tag/sort/category",
    method: "put",
    data: data,
  });
}
