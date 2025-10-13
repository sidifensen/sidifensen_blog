package com.sidifensen.controller;

import com.sidifensen.aspect.RateLimit;
import com.sidifensen.domain.dto.AiSummaryDto;
import com.sidifensen.domain.result.Result;
import com.sidifensen.service.AiService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Resource
    private AiService aiService;

    /**
     * 提取文章摘要
     * 限流策略：每小时最多5次
     *
     * @param aiSummaryDto 请求参数（包含文章内容）
     * @return 提取的摘要
     */
    @RateLimit(value = 5, period = 600, message = "AI摘要提取过于频繁，请10分钟后再试")
    @PostMapping("/extractSummary")
    public Result<String> extractSummary(@Valid @RequestBody AiSummaryDto aiSummaryDto) {
        String summary = aiService.extractSummary(aiSummaryDto.getContent());
        return Result.success(summary);
    }

    /**
     * 查询AI调用配额
     * 返回用户今日剩余的AI调用次数
     *
     * @return 剩余调用次数
     */
    @GetMapping("/quota")
    public Result<Integer> getAiQuota() {
        Integer remaining = aiService.getRemainingQuota();
        return Result.success(remaining);
    }

}
