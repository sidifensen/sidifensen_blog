import request from "@/utils/Request";
import { GetJwt } from "@/utils/Auth";

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

// 智能客服聊天（流式返回）
export function customerServiceChat(message, chatId) {
  // 使用GetJwt()获取token，与Request.js中的认证方式保持一致
  const token = GetJwt() || "";
  return fetch(`${import.meta.env.VITE_BACKEND_SERVER}/ai/customer-service?message=${encodeURIComponent(message)}&chatId=${chatId}`, {
    method: "POST",
    headers: {
      Authorization: token,
    },
  });
}

// 生成文章标题建议
export function generateTitles(content) {
  return request({
    url: "/ai/generate-titles",
    method: "post",
    data: {
      content: content,
    },
  });
}

// 推荐文章标签
export function recommendTags(title, content) {
  return request({
    url: "/ai/recommend-tags",
    method: "post",
    data: {
      title: title,
      content: content,
    },
  });
}

// 生成评论回复建议
export function generateCommentReplySuggestions(articleTitle, commentContent) {
  return request({
    url: "/ai/comment-reply-suggestions",
    method: "post",
    data: {
      articleTitle: articleTitle,
      commentContent: commentContent,
    },
  });
}
