package com.sidifensen.controller;

import com.sidifensen.domain.result.Result;
import com.sidifensen.domain.vo.HistoryVo;
import com.sidifensen.domain.vo.PageVo;
import com.sidifensen.service.HistoryService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author sidifensen
 * @since 2025-09-27
 */
@RestController
@RequestMapping("/history")
public class HistoryController {

    @Resource
    private HistoryService historyService;

    /**
     * 分页获取当前用户的文章浏览记录（倒序）
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 分页浏览历史数据
     */
    @GetMapping("/list")
    public Result<PageVo<List<HistoryVo>>> getUserHistoryList(@NotNull Integer pageNum, @NotNull Integer pageSize) {
        PageVo<List<HistoryVo>> historyPageVo = historyService.getUserHistoryList(pageNum, pageSize);
        return Result.success(historyPageVo);
    }

    /**
     * 清除当前用户的所有浏览记录
     *
     * @return 清除的记录数量
     */
    @DeleteMapping("/clear")
    public Result<Integer> clearUserHistory() {
        int clearedCount = historyService.clearUserHistory();
        return Result.success(clearedCount);
    }

}
