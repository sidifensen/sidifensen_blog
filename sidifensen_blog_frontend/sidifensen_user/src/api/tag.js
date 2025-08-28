import request from "@/utils/Request";

export const getTagList = () => {
  return request({
    url: "/tag/list",
    method: "get",
  });
};