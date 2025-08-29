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
