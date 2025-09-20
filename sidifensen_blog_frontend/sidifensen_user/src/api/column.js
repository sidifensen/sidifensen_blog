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
