package com.sidifensen.service.impl;

import cn.hutool.core.util.StrUtil;
import com.sidifensen.domain.constants.AiPromptConstants;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.exception.BlogException;
import com.sidifensen.domain.entity.Tag;
import com.sidifensen.domain.dto.LinkRequestDto;
import com.sidifensen.service.AiService;
import com.sidifensen.service.AiUsageService;
import com.sidifensen.service.LinkService;
import com.sidifensen.service.TagService;
import com.sidifensen.utils.SecurityUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.jsoup.Jsoup;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import jakarta.annotation.Resource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;

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

    @Resource
    private ChatClient customerServiceChatClient;

    @Resource
    private ChatClient writingAssistantChatClient;

    @Resource
    private TagService tagService;

    @Resource
    private LinkService linkService;

    private final ObjectMapper objectMapper = new ObjectMapper();

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
            String promptText = AiPromptConstants.extractSummaryPrompt(plainText);

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

    @Override
    public Flux<String> customerServiceChat(String message, String chatId) {
        Integer userId = SecurityUtils.getUserId();

        // 记录用户ID获取情况，便于调试
        if (userId == null || userId == 0) {
            log.warn("智能客服调用时无法获取有效用户ID，可能用户未登录或SecurityContext异常");
        } else {
            log.debug("智能客服调用，获取到用户ID: {}", userId);
        }

        try {
            // 1. 检查每日调用次数限制
            if (!aiUsageService.checkDailyLimit(userId)) {
                log.warn("用户 [ID: {}] 智能客服调用次数已达上限", userId);
                return Flux.just("今日AI调用次数已达上限，请明天再试。");
            }

            // 2. 记录调用日志
            log.info("用户 [ID: {}] 调用智能客服 - 剩余配额: {}", userId, aiUsageService.getRemainingQuota(userId));

            // 3. 使用流式返回，提升用户体验
            // 保存当前的Authentication，以便在异步处理中使用
            Authentication originalAuthentication = SecurityContextHolder.getContext().getAuthentication();

            // 创建流式响应
            Flux<String> aiResponse = customerServiceChatClient.prompt()
                    .user(message)
                    .advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId))
                    .stream()
                    .content();

            // 用于累积完整响应的StringBuilder
            StringBuilder fullResponseBuilder = new StringBuilder();

            // 流式输出每个chunk，同时累积完整响应
            return aiResponse
                    .doOnNext(chunk -> {
                        // 累积每个chunk到完整响应中
                        fullResponseBuilder.append(chunk);
                    })
                    .concatWith(
                            // 在流式输出完成后，检测并处理友链申请
                            Mono.defer(() -> {
                                // 在异步处理中创建新的SecurityContext并设置Authentication
                                if (originalAuthentication != null) {
                                    SecurityContext newContext = new SecurityContextImpl();
                                    newContext.setAuthentication(originalAuthentication);
                                    SecurityContextHolder.setContext(newContext);
                                }
                                try {
                                    // 使用累积的完整响应
                                    String fullResponse = fullResponseBuilder.toString();

                                    // 在异步处理中重新获取用户ID，确保SecurityContext恢复后能正确获取
                                    Integer currentUserId = SecurityUtils.getUserId();
                                    // 如果重新获取的userId有效，使用它；否则使用之前保存的userId
                                    Integer finalUserId = (currentUserId != null && currentUserId != 0) ? currentUserId
                                            : userId;

                                    // 记录用户ID获取情况，便于调试
                                    if (finalUserId == null || finalUserId == 0) {
                                        log.warn("异步处理中无法获取有效用户ID，原始userId: {}, 重新获取的userId: {}", userId, currentUserId);
                                    } else {
                                        log.debug("异步处理中获取用户ID成功，最终使用userId: {}", finalUserId);
                                    }

                                    // 检测并处理友链申请
                                    ProcessedResponse processed = processLinkApplyIfNeeded(fullResponse, finalUserId);

                                    // 如果检测到友链申请，返回处理结果
                                    if (processed.hasLinkApply) {
                                        return Mono.just(processed.successMessage);
                                    } else {
                                        return Mono.empty();
                                    }
                                } finally {
                                    // 清理SecurityContext（可选，但为了安全最好清理）
                                    // SecurityContextHolder.clearContext();
                                }
                            })
                    )
                    .doOnSubscribe(subscription -> {
                        // 流式返回开始时记录使用次数
                        aiUsageService.recordUsage(userId);
                    });
        } catch (BlogException e) {
            log.error("用户 [ID: {}] 智能客服会话失败: {}", userId, e.getMessage());
            return Flux.just(e.getMessage());
        } catch (Exception e) {
            log.error("用户 [ID: {}] 智能客服会话异常 [chatId: {}]", userId, chatId, e);
            return Flux.just("抱歉，客服系统暂时无法响应，请稍后再试。");
        }
    }

    /**
     * 处理结果封装类
     */
    private static class ProcessedResponse {
        String response;
        String successMessage;
        boolean hasLinkApply;

        ProcessedResponse(String response, String successMessage, boolean hasLinkApply) {
            this.response = response;
            this.successMessage = successMessage;
            this.hasLinkApply = hasLinkApply;
        }
    }

    /**
     * 检测并处理友链申请
     * 如果AI返回的内容包含友链申请JSON，则自动调用LinkService提交申请
     *
     * @param response AI返回的完整响应
     * @param userId   用户ID
     * @return 处理后的响应结果
     */
    private ProcessedResponse processLinkApplyIfNeeded(String response, Integer userId) {
        try {
            // 尝试从响应中提取JSON
            String jsonStr = extractLinkApplyJson(response);
            if (jsonStr == null) {
                // 没有友链申请JSON，直接返回原响应
                return new ProcessedResponse(response, "", false);
            }

            // 解析JSON
            LinkApplyJson linkApplyJson;
            try {
                linkApplyJson = objectMapper.readValue(jsonStr, LinkApplyJson.class);
            } catch (Exception e) {
                log.warn("用户 [ID: {}] 友链申请JSON解析失败，JSON内容: {}", userId, jsonStr, e);
                // JSON解析失败，返回原响应（移除JSON部分）
                return new ProcessedResponse(response.replace(jsonStr, "").trim(), "", false);
            }

            if (linkApplyJson.action == null || !"apply_link".equals(linkApplyJson.action)
                    || linkApplyJson.data == null) {
                log.warn("用户 [ID: {}] 友链申请JSON格式不正确，action: {}, data: {}",
                        userId, linkApplyJson.action, linkApplyJson.data);
                return new ProcessedResponse(response.replace(jsonStr, "").trim(), "", false);
            }

            // 验证必填字段
            LinkRequestDto linkData = linkApplyJson.data;
            if (StrUtil.isBlank(linkData.getName()) || StrUtil.isBlank(linkData.getUrl())
                    || StrUtil.isBlank(linkData.getDescription()) || StrUtil.isBlank(linkData.getEmail())) {
                log.warn("用户 [ID: {}] 友链申请信息不完整，跳过自动申请", userId);
                return new ProcessedResponse(response.replace(jsonStr, "").trim(), "", false);
            }

            // 检查用户ID是否有效（防止异步处理中SecurityContext丢失）
            if (userId == null || userId == 0) {
                log.warn("用户ID无效，无法提交友链申请");
                String errorMessage = "\n\n❌ 友链申请需要登录后才能提交，请先登录后再试。";
                return new ProcessedResponse(response.replace(jsonStr, "").trim(), errorMessage, true);
            }

            // 调用LinkService提交友链申请（直接传入userId，避免SecurityContext问题）
            try {
                linkService.applyLink(linkData, userId);
                log.info("用户 [ID: {}] 通过AI智能客服成功申请友链: {}", userId, linkData.getName());

                // 从响应中移除JSON
                String textWithoutJson = response.replace(jsonStr, "").trim();

                // 构建成功提示消息
                String successMessage = String.format(
                        "\n\n✅ 友链申请已成功提交！\n" +
                                "网站名称：%s\n" +
                                "网站地址：%s\n" +
                                "我们会尽快审核您的申请，审核结果将通过邮箱 %s 通知您。",
                        linkData.getName(), linkData.getUrl(), linkData.getEmail());

                return new ProcessedResponse(textWithoutJson, successMessage, true);
            } catch (Exception e) {
                log.error("用户 [ID: {}] 通过AI申请友链失败: {}", userId, e.getMessage(), e);
                String errorMessage = "\n\n❌ 友链申请提交失败，请稍后重试或手动申请。";
                return new ProcessedResponse(response.replace(jsonStr, "").trim(), errorMessage, true);
            }
        } catch (Exception e) {
            log.error("用户 [ID: {}] 处理友链申请JSON时发生异常: {}", userId, e.getMessage(), e);
            // 解析失败，返回原响应
            return new ProcessedResponse(response, "", false);
        }
    }

    /**
     * 从AI响应中提取友链申请JSON
     *
     * @param response AI响应内容
     * @return JSON字符串，如果不存在则返回null
     */
    private String extractLinkApplyJson(String response) {
        if (StrUtil.isBlank(response)) {
            return null;
        }

        // 首先查找包含 "action": "apply_link" 的位置
        int actionIndex = response.indexOf("\"action\"");
        if (actionIndex == -1) {
            actionIndex = response.indexOf("'action'");
        }
        if (actionIndex == -1) {
            return null;
        }

        // 向前查找最近的 { 作为JSON开始
        int startIndex = response.lastIndexOf('{', actionIndex);
        if (startIndex == -1) {
            return null;
        }

        // 从开始位置向后查找匹配的 }，确保提取完整的JSON对象
        int braceCount = 0;
        int endIndex = startIndex;
        boolean inString = false;
        char stringChar = 0;

        for (int i = startIndex; i < response.length(); i++) {
            char c = response.charAt(i);

            // 处理字符串内的字符（忽略字符串内的特殊字符）
            if (inString) {
                if (c == stringChar && (i == 0 || response.charAt(i - 1) != '\\')) {
                    inString = false;
                }
                continue;
            }

            // 检测字符串开始
            if (c == '"' || c == '\'') {
                inString = true;
                stringChar = c;
                continue;
            }

            // 计算大括号
            if (c == '{') {
                braceCount++;
            } else if (c == '}') {
                braceCount--;
                if (braceCount == 0) {
                    // 找到匹配的结束大括号
                    endIndex = i;
                    break;
                }
            }
        }

        // 如果没有找到匹配的结束大括号，返回null
        if (braceCount != 0) {
            log.warn("提取的JSON不完整，大括号不匹配");
            return null;
        }

        // 提取完整的JSON字符串
        String jsonStr = response.substring(startIndex, endIndex + 1);

        // 验证是否包含必要的字段
        if (!jsonStr.contains("apply_link") || !jsonStr.contains("data")) {
            return null;
        }

        return jsonStr;
    }

    /**
     * 友链申请JSON结构
     */
    private static class LinkApplyJson {
        public String action;
        public LinkRequestDto data;
    }

    @Override
    public List<String> generateTitleSuggestions(String content) {
        Integer userId = SecurityUtils.getUserId();

        try {
            // 1. 校验输入
            if (StrUtil.isBlank(content)) {
                throw new BlogException(BlogConstants.AiContentEmpty);
            }

            // 2. 检查每日调用次数限制
            if (!aiUsageService.checkDailyLimit(userId)) {
                throw new BlogException(BlogConstants.AiDailyLimitExceeded);
            }

            // 3. 提取纯文本
            String plainText = Jsoup.parse(content).text();

            // 4. 限制文本长度
            if (plainText.length() > 1000) {
                plainText = plainText.substring(0, 1000);
            }

            // 5. 记录调用日志
            log.info("用户 [ID: {}] 调用AI生成标题建议 - 剩余配额: {}", userId, aiUsageService.getRemainingQuota(userId));

            String prompt = AiPromptConstants.generateTitleSuggestionsPrompt(plainText);

            String result = writingAssistantChatClient.prompt(prompt).call().content();

            // 6. 解析返回的标题列表
            List<String> titles = Arrays.stream(result.split("\n"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .limit(5)
                    .collect(Collectors.toList());

            // 7. 调用成功后记录使用次数
            aiUsageService.recordUsage(userId);

            return titles;
        } catch (BlogException e) {
            log.error("用户 [ID: {}] 生成标题建议失败: {}", userId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("用户 [ID: {}] 生成标题建议异常", userId, e);
            throw new BlogException("生成标题建议失败: " + e.getMessage());
        }
    }

    @Override
    public List<String> recommendTags(String title, String content) {
        Integer userId = SecurityUtils.getUserId();

        try {
            // 1. 校验输入
            if (StrUtil.isBlank(title) && StrUtil.isBlank(content)) {
                throw new BlogException(BlogConstants.AiContentEmpty);
            }

            // 2. 检查每日调用次数限制
            if (!aiUsageService.checkDailyLimit(userId)) {
                throw new BlogException(BlogConstants.AiDailyLimitExceeded);
            }

            // 3. 获取数据库中所有可用的标签名称
            List<Tag> allTags = tagService.list();
            List<String> availableTagNames = allTags.stream()
                    .map(Tag::getName)
                    .filter(name -> name != null && !name.trim().isEmpty())
                    .collect(Collectors.toList());

            if (availableTagNames.isEmpty()) {
                log.warn("用户 [ID: {}] 调用AI推荐标签，但数据库中暂无标签", userId);
                return List.of();
            }

            // 4. 提取纯文本
            String plainText = Jsoup.parse(content).text();

            // 5. 限制文本长度
            if (plainText.length() > 800) {
                plainText = plainText.substring(0, 800);
            }

            // 6. 记录调用日志
            log.info("用户 [ID: {}] 调用AI推荐标签 - 剩余配额: {}", userId, aiUsageService.getRemainingQuota(userId));

            // 7. 构建提示词，传入可用标签列表
            String prompt = AiPromptConstants.recommendTagsPrompt(title, plainText, availableTagNames);

            // 8. 调用 AI 生成推荐
            String result = writingAssistantChatClient.prompt(prompt).call().content();

            // 9. 解析返回的标签列表，并过滤掉不在数据库中的标签
            List<String> recommendedTags = Arrays.stream(result.split("\n"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .filter(availableTagNames::contains) // 只保留数据库中存在的标签
                    .distinct() // 去重
                    .limit(8)
                    .collect(Collectors.toList());

            log.info("用户 [ID: {}] AI推荐标签完成，最终推荐数量: {}", userId, recommendedTags.size());

            // 10. 调用成功后记录使用次数
            aiUsageService.recordUsage(userId);

            return recommendedTags;
        } catch (BlogException e) {
            log.error("用户 [ID: {}] 推荐标签失败: {}", userId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("用户 [ID: {}] 推荐标签异常", userId, e);
            throw new BlogException("推荐标签失败: " + e.getMessage());
        }
    }

    @Override
    public List<String> generateCommentReplySuggestions(String articleTitle, String commentContent) {
        Integer userId = SecurityUtils.getUserId();

        try {
            // 1. 校验输入
            if (StrUtil.isBlank(articleTitle) || StrUtil.isBlank(commentContent)) {
                throw new BlogException("文章标题和评论内容不能为空");
            }

            // 2. 检查每日调用次数限制
            if (!aiUsageService.checkDailyLimit(userId)) {
                throw new BlogException(BlogConstants.AiDailyLimitExceeded);
            }

            // 3. 记录调用日志
            log.info("用户 [ID: {}] 调用AI生成评论回复建议 - 剩余配额: {}", userId, aiUsageService.getRemainingQuota(userId));

            String prompt = AiPromptConstants.generateCommentReplySuggestionsPrompt(articleTitle, commentContent);

            String result = writingAssistantChatClient.prompt(prompt).call().content();

            // 4. 解析返回的回复列表
            List<String> replies = Arrays.stream(result.split("\n"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .limit(3)
                    .collect(Collectors.toList());

            // 如果返回少于3个，补充默认回复
            if (replies.size() < 3) {
                replies.add("感谢你的评论！你的反馈对我很重要。");
                replies.add("很高兴看到你的想法，让我们一起交流学习！");
                replies.add("谢谢你的关注，欢迎继续交流！");
            }

            List<String> finalReplies = replies.stream().limit(3).collect(Collectors.toList());

            // 5. 调用成功后记录使用次数
            aiUsageService.recordUsage(userId);

            return finalReplies;
        } catch (BlogException e) {
            log.error("用户 [ID: {}] 生成评论回复建议失败: {}", userId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("用户 [ID: {}] 生成评论回复建议异常", userId, e);
            throw new BlogException("生成回复建议失败: " + e.getMessage());
        }
    }
}
