package com.sidifensen.config;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AI 配置类
 * 配置 DeepSeek API（使用 OpenAI 兼容接口）
 */
@Configuration
public class AiConfig {

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Value("${spring.ai.openai.base-url}")
    private String baseUrl;

    @Value("${spring.ai.openai.chat.options.model}")
    private String model;

    @Value("${spring.ai.openai.chat.options.temperature}")
    private Double temperature;

    /**
     * 配置 OpenAI Chat 模型（使用 DeepSeek API）
     * 使用自定义配置连接 DeepSeek API
     */
    @Bean
    public OpenAiChatModel openAiChatModel() {
        // 创建 OpenAI API 客户端，指向 DeepSeek API 地址
        OpenAiApi openAiApi = new OpenAiApi(baseUrl, apiKey);

        // 创建聊天选项（使用新的 API 方式）
        OpenAiChatOptions options = new OpenAiChatOptions();
        options.setModel(model);
        options.setTemperature(temperature);

        // 创建并返回 OpenAI Chat 模型
        return new OpenAiChatModel(openAiApi, options);
    }
}
