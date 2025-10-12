import request from "@/utils/Request";

// AI 提取文章摘要
export function extractSummary(content) {
  return request({
    url: "/ai/extractSummary",
    method: "post",
    data: {
      content: content,
    },
  });
}

// 获取AI调用配额（今日剩余次数）
export function getAiQuota() {
  return request({
    url: "/ai/quota",
    method: "get",
  });
}
