package com.sidifensen.controller;

import com.sidifensen.aspect.RateLimit;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.dto.AiSummaryDto;
import com.sidifensen.domain.result.Result;
import com.sidifensen.exception.BlogException;
import com.sidifensen.service.AiService;
import com.sidifensen.service.AiUsageService;
import com.sidifensen.utils.SecurityUtils;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Resource
    private AiService aiService;

    @Resource
    private AiUsageService aiUsageService;

    /**
     * 提取文章摘要
     * 限流策略：每10分钟最多5次
     * 需要登录才能使用
     *
     * @param aiSummaryDto 请求参数（包含文章内容）
     * @return 提取的摘要
     */
    @RateLimit(value = 5, period = 600, message = "AI摘要提取过于频繁，请10分钟后再试")
    @PostMapping("/extractSummary")
    public Result<String> extractSummary(@Valid @RequestBody AiSummaryDto aiSummaryDto) {
        if (SecurityUtils.getLoginUser() == null) {
            throw new BlogException(BlogConstants.LoginRequired);
        }
        String summary = aiService.extractSummary(aiSummaryDto.getContent());
        return Result.success(summary);
    }

    /**
     * 查询AI调用配额
     * 返回用户今日剩余调用次数和日限额
     *
     * @return 配额信息
     */
    @GetMapping("/quota")
    public Result<Map<String, Integer>> getAiQuota() {
        Integer userId = SecurityUtils.getUserId();
        Integer remaining = aiService.getRemainingQuota();
        return Result.success(Map.of(
                "remaining", remaining,
                "dailyLimit", aiUsageService.getDailyLimit(userId)
        ));
    }

    /**
     * 智能客服聊天（流式返回）
     * message 长度限制 1000 字符
     * 需要登录才能使用
     *
     * @param message 用户消息
     * @param chatId  会话ID
     * @return 流式响应
     */
    @PostMapping(value = "/customer-service", produces = "text/plain;charset=UTF-8")
    public Flux<String> customerServiceChat(
            @RequestParam @NotBlank(message = "消息内容不能为空") @Size(max = 1000, message = "消息内容不能超过1000字符") String message,
            @RequestParam String chatId) {
        // 检查用户是否登录
        if (SecurityUtils.getLoginUser() == null) {
            return Flux.just("请先登录后再使用智能客服功能");
        }
        return aiService.customerServiceChat(message, chatId);
    }

    /**
     * 生成文章标题建议
     * 限流策略：每小时最多10次
     * 需要登录才能使用
     *
     * @param request 包含文章内容的请求体
     * @return 标题建议列表（5个）
     */
    @RateLimit(value = 10, period = 3600, message = "AI标题生成过于频繁，请稍后再试")
    @PostMapping("/generate-titles")
    public Result<List<String>> generateTitles(@RequestBody Map<String, String> request) {
        if (SecurityUtils.getLoginUser() == null) {
            throw new BlogException(BlogConstants.LoginRequired);
        }
        String content = request.get("content");
        List<String> titles = aiService.generateTitleSuggestions(content);
        return Result.success(titles);
    }

    /**
     * 推荐文章标签
     * 限流策略：每小时最多10次
     * 需要登录才能使用
     *
     * @param request 包含标题和内容的请求体
     * @return 标签建议列表（5-8个）
     */
    @RateLimit(value = 10, period = 3600, message = "AI标签推荐过于频繁，请稍后再试")
    @PostMapping("/recommend-tags")
    public Result<List<String>> recommendTags(@RequestBody Map<String, String> request) {
        if (SecurityUtils.getLoginUser() == null) {
            throw new BlogException(BlogConstants.LoginRequired);
        }
        String title = request.get("title");
        String content = request.get("content");
        List<String> tags = aiService.recommendTags(title, content);
        return Result.success(tags);
    }

    /**
     * 生成评论回复建议
     * 限流策略：每小时最多15次
     * 需要登录才能使用
     *
     * @param request 包含文章标题和评论内容的请求体
     * @return 回复建议列表（3个）
     */
    @RateLimit(value = 15, period = 3600, message = "AI回复建议生成过于频繁，请稍后再试")
    @PostMapping("/comment-reply-suggestions")
    public Result<List<String>> generateCommentReplySuggestions(@RequestBody Map<String, String> request) {
        if (SecurityUtils.getLoginUser() == null) {
            throw new BlogException(BlogConstants.LoginRequired);
        }
        String articleTitle = request.get("articleTitle");
        String commentContent = request.get("commentContent");
        List<String> replies = aiService.generateCommentReplySuggestions(articleTitle, commentContent);
        return Result.success(replies);
    }

}
