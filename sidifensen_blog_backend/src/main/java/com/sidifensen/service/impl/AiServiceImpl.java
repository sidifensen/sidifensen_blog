package com.sidifensen.service.impl;

import cn.hutool.core.util.StrUtil;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.exception.BlogException;
import com.sidifensen.service.AiService;
import com.sidifensen.service.AiUsageService;
import com.sidifensen.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

/**
 * AI 服务实现类
 * 使用 DeepSeek API 提供 AI 能力
 */
@Service
@Slf4j
public class AiServiceImpl implements AiService {

    @Resource
    private OpenAiChatModel openAiChatModel;

    @Resource
    private AiUsageService aiUsageService;

    @Override
    public String extractSummary(String content) {
        // 获取当前用户ID
        Integer userId = SecurityUtils.getUserId();

        try {
            // 1. 校验输入不能为空
            if (StrUtil.isBlank(content)) {
                throw new BlogException(BlogConstants.AiContentEmpty);
            }

            // 2. 使用 Jsoup 提取纯文本内容（去除 HTML 标签）
            String plainText = Jsoup.parse(content).text();

            // 3. 校验内容长度 - 太短的内容不需要AI摘要（节省token）
            if (plainText.length() < 100) {
                log.warn("用户 [ID: {}] 提交的内容过短（{}字符），拒绝处理", userId, plainText.length());
                throw new BlogException(BlogConstants.AiContentTooShort);
            }

            // 4. 检查是否为重复内容（防止重复提交消耗token）
            if (aiUsageService.isDuplicateContent(userId, plainText)) {
                throw new BlogException(BlogConstants.AiDuplicateRequest);
            }

            // 5. 检查每日调用次数限制
            if (!aiUsageService.checkDailyLimit(userId)) {
                throw new BlogException(BlogConstants.AiDailyLimitExceeded);
            }

            // 6. 记录调用日志
            log.info("用户 [ID: {}] 调用AI摘要提取 - 内容长度: {} 字符, 剩余配额: {}",
                    userId, plainText.length(), aiUsageService.getRemainingQuota(userId));

            // 限制文本长度，避免超过 token 限制（取前 3000 个字符）
            if (plainText.length() > 3000) {
                plainText = plainText.substring(0, 3000);
            }

            // 构建提示词，严格要求字数限制
            String promptText = String.format(
                    "请为以下文章内容提取一段简洁的摘要。\n\n" +
                            "重要要求：\n" +
                            "1. 摘要总字数（包括所有中文、英文、标点符号）必须严格控制在150字以内\n" +
                            "2. 准确概括文章的核心内容\n" +
                            "3. 语言简洁流畅，避免冗余\n" +
                            "4. 突出文章的主要观点或重点\n" +
                            "5. 直接输出摘要内容，不要添加任何前缀、后缀或说明性文字\n" +
                            "6. 请务必遵守150字的字数限制\n\n" +
                            "文章内容：\n%s",
                    plainText);

            // 配置选项（减少 max-tokens 来限制输出长度）
            OpenAiChatOptions options = new OpenAiChatOptions();
            options.setMaxTokens(250);

            // 创建提示
            Prompt prompt = new Prompt(promptText, options);

            // 调用 AI 模型
            ChatResponse response = openAiChatModel.call(prompt);

            // 提取生成的摘要
            String summary = response.getResult().getOutput().getContent().trim();

            // 强制确保摘要不超过 180 字（双重保险）
            if (summary.length() > 180) {
                // 截断到 180 字，并尝试在合适的位置结束（句号、感叹号、问号）
                summary = summary.substring(0, 180);

                // 尝试找到最后一个句号、感叹号或问号，在此处截断使语义更完整
                int lastPeriod = Math.max(summary.lastIndexOf('。'),
                        Math.max(summary.lastIndexOf('！'),
                                summary.lastIndexOf('？')));

                // 如果找到了标点符号且位置不是太靠前（至少保留100字），则在此截断
                if (lastPeriod > 100) {
                    summary = summary.substring(0, lastPeriod + 1);
                }
            }

            // 7. 调用成功后记录使用次数和内容hash
            aiUsageService.recordUsage(userId);
            aiUsageService.recordContentHash(userId, plainText);

            return summary;
        } catch (BlogException e) {
            log.error("用户 [ID: {}] AI摘要提取失败: {}", userId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("用户 [ID: {}] AI摘要提取异常", userId, e);
            throw new BlogException(BlogConstants.AiExtractSummaryError + ": " + e.getMessage());
        }
    }

    @Override
    public Integer getRemainingQuota() {
        Integer userId = SecurityUtils.getUserId();
        return aiUsageService.getRemainingQuota(userId);
    }
}
