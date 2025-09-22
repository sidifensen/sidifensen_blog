import request from "@/utils/Request";

export const getColumnList = () => {
  return request({
    url: "/column/list",
    method: "get",
  });
};

export const addColumn = (data) => {
  return request({
    url: "/column/add",
    method: "post",
    data,
  });
};

export const getUserColumnManageList = (pageNum, pageSize, columnFilterDto) => {
  return request({
    url: "/column/manage/list",
    method: "post",
    params: {
      pageNum,
      pageSize,
    },
    data: columnFilterDto,
  });
};

export const updateColumn = (data) => {
  return request({
    url: "/column/update",
    method: "put",
    data,
  });
};

export const deleteColumn = (id) => {
  return request({
    url: `/column/${id}`,
    method: "delete",
  });
};

// 获取专栏详情（包含文章列表）
export const getColumnDetail = (columnId) => {
  return request({
    url: `/column/detail/${columnId}`,
    method: "get",
  });
};

// 调整专栏内文章排序
export const updateColumnArticleSort = (columnId, sortList) => {
  return request({
    url: `/column/${columnId}/article/sort`,
    method: "put",
    data: sortList,
  });
};

// 从专栏中删除文章
export const removeArticleFromColumn = (columnId, articleId) => {
  return request({
    url: `/column/${columnId}/article/${articleId}`,
    method: "delete",
  });
};

// 获取用户公开专栏列表（用于用户主页展示）
export const getUserColumnList = (pageNum, pageSize, userId) => {
  return request({
    url: `/column/list/${userId}`,
    method: "get",
    params: {
      pageNum,
      pageSize,
    },
  });
};
