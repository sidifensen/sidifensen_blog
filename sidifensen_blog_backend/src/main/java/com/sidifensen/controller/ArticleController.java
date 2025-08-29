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
    @GetMapping("/user/list")
    public Result getUserArticleList(@NotNull Integer pageNum, @NotNull Integer pageSize, ArticleStatusDto articleStatusDto) {
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
    @PutMapping("/admin/examine")
    public Result adminExamineArticle(@RequestBody ArticleAuditDto articleAuditDto) {
        articleService.adminExamineArticle(articleAuditDto);
        return Result.success();
    }

}
