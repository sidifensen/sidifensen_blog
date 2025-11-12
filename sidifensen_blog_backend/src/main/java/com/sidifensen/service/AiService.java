package com.sidifensen.service;

import reactor.core.publisher.Flux;

import java.util.List;

/**
 * AI 服务接口
 */
public interface AiService {

    /**
     * 根据文章内容提取摘要
     * 
     * @param content 文章内容（HTML 格式）
     * @return 提取的摘要文本
     */
    String extractSummary(String content);

    /**
     * 获取当前用户今日剩余的AI调用配额
     *
     * @return 剩余调用次数
     */
    Integer getRemainingQuota();

    /**
     * 智能客服聊天（流式返回）
     *
     * @param message 用户消息
     * @param chatId  会话ID（用于维持上下文）
     * @return 流式响应
     */
    Flux<String> customerServiceChat(String message, String chatId);

    /**
     * 生成文章标题建议
     *
     * @param content 文章内容
     * @return 标题建议列表（5个）
     */
    List<String> generateTitleSuggestions(String content);

    /**
     * 推荐文章标签
     *
     * @param title   文章标题
     * @param content 文章内容
     * @return 标签建议列表（5-8个）
     */
    List<String> recommendTags(String title, String content);

    /**
     * 生成评论回复建议
     *
     * @param articleTitle   文章标题
     * @param commentContent 评论内容
     * @return 回复建议列表（3个）
     */
    List<String> generateCommentReplySuggestions(String articleTitle, String commentContent);
}
