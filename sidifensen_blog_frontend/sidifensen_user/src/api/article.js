import request from "@/utils/Request";

export function addArticle(data) {
  return request({
    url: "/article/add",
    method: "post",
    data,
  });
}