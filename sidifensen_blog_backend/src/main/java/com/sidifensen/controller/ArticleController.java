package com.sidifensen.controller;


import com.sidifensen.domain.dto.ArticleAuditDto;
import com.sidifensen.domain.dto.ArticleDto;
import com.sidifensen.domain.dto.ArticleStatusDto;
import com.sidifensen.domain.result.Result;
import com.sidifensen.domain.vo.ArticleVo;
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
public class ArticleController {

    @Resource
    private ArticleService articleService;

    /**
     * 获取文章列表
     *
     * @return 文章列表
     */
    @GetMapping("/list")
    public Result getArticleList(@NotNull Integer pageNum, @NotNull Integer pageSize) {
        PageVo<List<ArticleVo>> articleVoList = articleService.getArticleList(pageNum, pageSize);
        return Result.success(articleVoList);
    }

    /**
     * 获取用户文章列表
     *
     * @return 用户文章列表
     */
    @PostMapping("/user/list")
    public Result getUserArticleList(@NotNull Integer pageNum, @NotNull Integer pageSize, @RequestBody ArticleStatusDto articleStatusDto) {
        PageVo<List<ArticleVo>> articleVoList = articleService.getUserArticleList(pageNum, pageSize, articleStatusDto);
        return Result.success(articleVoList);
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
     * 管理员批量删除文章
     *
     * @return 管理员批量删除文章
     */
    @PreAuthorize("hasAuthority('article:delete')")
    @DeleteMapping("/admin/delete/batch")
    public Result adminDeleteBatchArticle(@RequestBody List<ArticleAuditDto> articleAuditDtos) {
        articleService.adminDeleteBatchArticle(articleAuditDtos);
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

}
