package com.sidifensen.controller;

import com.sidifensen.aspect.RateLimit;
import com.sidifensen.domain.dto.ArticleAuditDto;
import com.sidifensen.domain.dto.ArticleDto;
import com.sidifensen.domain.dto.ArticleStatusDto;
import com.sidifensen.domain.result.Result;
import com.sidifensen.domain.vo.ArticleStatisticsVo;
import com.sidifensen.domain.vo.ArticleVo;
import com.sidifensen.domain.vo.CreationStatisticsVo;
import com.sidifensen.domain.vo.PageVo;
import com.sidifensen.service.ArticleService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author sidifensen
 * @since 2025-08-24
 */
@RestController
@RequestMapping("/article")
@RateLimit
public class ArticleController {

    @Resource
    private ArticleService articleService;

    /**
     * 获取全部已发布审核通过全部人可见的文章列表（按更新时间倒序）
     *
     * @param pageNum  页码
     * @param pageSize 页大小
     * @return 全部文章列表
     */
    @GetMapping("/listAll")
    public Result getAllArticleList(@RequestParam(defaultValue = "1") @NotNull(message = "页码不能为空") Integer pageNum,
                                    @RequestParam(defaultValue = "10") @NotNull(message = "每页大小不能为空") Integer pageSize) {
        PageVo<List<ArticleVo>> articleVoList = articleService.getAllArticleList(pageNum, pageSize);
        return Result.success(articleVoList);
    }

    /**
     * 获取用户文章列表
     *
     * @return 用户文章列表
     */
    @PostMapping("/user/list")
    public Result getUserArticleList(@RequestParam(defaultValue = "1") @NotNull(message = "页码不能为空") Integer pageNum,
                                     @RequestParam(defaultValue = "10") @NotNull(message = "每页大小不能为空") Integer pageSize,
                                     @RequestBody ArticleStatusDto articleStatusDto) {
        PageVo<List<ArticleVo>> articleVoList = articleService.getUserArticleList(pageNum, pageSize, articleStatusDto);
        return Result.success(articleVoList);
    }

    /**
     * 获取用户文章列表(文章管理)
     *
     * @return 用户文章列表
     */
    @PostMapping("/manage/list")
    public Result getArticleMangeList(@RequestParam(defaultValue = "1") @NotNull(message = "页码不能为空") Integer pageNum,
                                      @RequestParam(defaultValue = "10") @NotNull(message = "每页大小不能为空") Integer pageSize,
                                      @RequestBody ArticleStatusDto articleStatusDto) {
        PageVo<List<ArticleVo>> articleVoList = articleService.getArticleMangeList(pageNum, pageSize, articleStatusDto);
        return Result.success(articleVoList);
    }

    /**
     * 获取当前用户文章状态统计(文章管理)
     *
     * @return 用户文章状态统计
     */
    @GetMapping("/user/statistics")
    public Result getUserArticleStatistics() {
        ArticleStatisticsVo statisticsVo = articleService.getUserArticleStatistics();
        return Result.success(statisticsVo);
    }

    /**
     * 获取指定用户的文章统计
     *
     * @param userId 用户ID
     * @return 用户文章统计
     */
    @GetMapping("/user/{userId}/statistics")
    public Result getUserArticleStatisticsById(@PathVariable Integer userId) {
        ArticleStatisticsVo statisticsVo = articleService.getUserArticleStatisticsById(userId);
        return Result.success(statisticsVo);
    }

    /**
     * 获取创作中心统计数据
     *
     * @return 创作中心统计数据
     */
    @GetMapping("/creation/statistics")
    public Result getCreationStatistics() {
        CreationStatisticsVo statisticsVo = articleService.getCreationStatistics();
        return Result.success(statisticsVo);
    }

    /**
     * 获取用户文章详情
     *
     * @return 用户文章详情
     */
    @GetMapping("/get/{articleId}")
    public Result getArticle(@PathVariable Integer articleId) {
        ArticleVo articleVo = articleService.getArticle(articleId);
        return Result.success(articleVo);
    }

    /**
     * 新增文章
     *
     * @return 新增文章
     */
    @PostMapping("/add")
    public Result addArticle(@RequestBody ArticleDto articleDto) {
        articleService.addArticle(articleDto);
        return Result.success();
    }

    /**
     * 保存为草稿
     */
    @PostMapping("/saveDraft")
    public Result saveDraft(@RequestBody ArticleDto articleDto) {
        articleService.saveDraft(articleDto);
        return Result.success();
    }

    /**
     * 更新文章
     *
     * @return 更新文章
     */
    @PutMapping("/update")
    public Result updateArticle(@RequestBody ArticleDto articleDto) {
        articleService.updateArticle(articleDto);
        return Result.success();
    }

    /**
     * 删除文章
     *
     * @return 删除文章
     */
    @DeleteMapping("/delete/{articleId}")
    public Result deleteArticle(@PathVariable Integer articleId) {
        articleService.deleteArticle(articleId);
        return Result.success();
    }

    /**
     * 管理员获取文章列表
     *
     * @return 管理员文章列表
     */
    @PreAuthorize("hasAuthority('article:list')")
    @GetMapping("/admin/list")
    public Result adminGetArticleList() {
        List<ArticleVo> articleVoList = articleService.adminGetArticleList();
        return Result.success(articleVoList);
    }


    /**
     * 管理员根据用户ID获取文章列表
     *
     * @return 管理员根据用户ID获取文章列表
     */
    @PreAuthorize("hasAuthority('article:user:list')")
    @GetMapping("/admin/user/{userId}")
    public Result adminGetArticlesByUserId(@PathVariable Integer userId) {
        List<ArticleVo> articleVoList = articleService.adminGetArticlesByUserId(userId);
        return Result.success(articleVoList);
    }

    /**
     * 管理员获取文章详情
     *
     * @return 管理员文章详情
     */
    @PreAuthorize("hasAuthority('article:get')")
    @GetMapping("/admin/{articleId}")
    public Result adminGetArticle(@PathVariable Integer articleId) {
        ArticleVo articleVo = articleService.adminGetArticle(articleId);
        return Result.success(articleVo);
    }

    /**
     * 管理员更新文章
     *
     * @return 管理员更新文章
     */
    @PreAuthorize("hasAuthority('article:update')")
    @PutMapping("/admin/update")
    public Result adminUpdateArticle(@RequestBody ArticleDto articleDto) {
        articleService.adminUpdateArticle(articleDto);
        return Result.success();
    }

    /**
     * 管理员搜索文章
     *
     * @return 管理员搜索文章
     */
    @PreAuthorize("hasAuthority('article:search')")
    @PostMapping("/admin/search")
    public Result adminSearchArticle(@RequestBody ArticleDto articleDto) {
        List<ArticleVo> articleVoList = articleService.adminSearchArticle(articleDto);
        return Result.success(articleVoList);
    }

    /**
     * 管理员审核文章
     *
     * @return 管理员审核文章
     */
    @PreAuthorize("hasAuthority('article:examine')")
    @PutMapping("/admin/examine")
    public Result adminExamineArticle(@RequestBody ArticleAuditDto articleAuditDto) {
        articleService.adminExamineArticle(articleAuditDto);
        return Result.success();
    }

    /**
     * 管理员批量审核文章
     *
     * @return 管理员批量审核文章
     */
    @PreAuthorize("hasAuthority('article:examine')")
    @PutMapping("/admin/examine/batch")
    public Result adminExamineBatchArticle(@RequestBody List<ArticleAuditDto> articleAuditDtos) {
        articleService.adminExamineBatchArticle(articleAuditDtos);
        return Result.success();
    }

    /**
     * 管理员删除文章
     *
     * @return 管理员删除文章
     */
    @PreAuthorize("hasAuthority('article:delete')")
    @DeleteMapping("/admin/{articleId}")
    public Result adminDeleteArticle(@PathVariable Integer articleId) {
        articleService.adminDeleteArticle(articleId);
        return Result.success();
    }

    /**
     * 管理员批量删除文章
     *
     * @param articleIds 文章ID列表
     * @return 管理员批量删除文章
     */
    @PreAuthorize("hasAuthority('article:delete')")
    @DeleteMapping("/admin/delete/batch")
    public Result adminDeleteBatchArticle(@RequestBody List<Integer> articleIds) {
        articleService.adminDeleteBatchArticle(articleIds);
        return Result.success();
    }

    /**
     * 管理员获取文章统计数据
     *
     * @return 文章统计数据
     */
    @PreAuthorize("hasAuthority('article:list')")
    @GetMapping("/admin/statistics")
    public Result getAdminStatistics() {
        ArticleStatisticsVo statisticsVo = articleService.getAdminStatistics();
        return Result.success(statisticsVo);
    }

}
