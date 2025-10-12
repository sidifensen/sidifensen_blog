package com.sidifensen.service;

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
}
