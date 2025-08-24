package com.sidifensen.controller;


import com.sidifensen.domain.dto.ArticleDto;
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

    @GetMapping("/list")
    public Result getArticleList(@NotNull Integer pageNum, @NotNull Integer pageSize) {
        PageVo<List<ArticleVo>> articleVoList = articleService.getArticleList(pageNum, pageSize);
        return Result.success(articleVoList);
    }

    @GetMapping("/{articleId}")
    public Result getArticle(@PathVariable Integer articleId) {
        ArticleVo articleVo = articleService.getArticle(articleId);
        return Result.success(articleVo);
    }

    @PostMapping("/add")
    public Result addArticle(@RequestBody ArticleDto articleDto) {
        articleService.addArticle(articleDto);
        return Result.success();
    }

    @PostMapping("/update")
    public Result updateArticle(@RequestBody ArticleDto articleDto) {
        articleService.updateArticle(articleDto);
        return Result.success();
    }

    @DeleteMapping("/{articleId}")
    public Result deleteArticle(@PathVariable Integer articleId) {
        articleService.deleteArticle(articleId);
        return Result.success();
    }
}
