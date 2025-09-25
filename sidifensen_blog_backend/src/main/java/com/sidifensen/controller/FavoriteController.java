package com.sidifensen.controller;

import com.sidifensen.domain.dto.AddFavoriteDto;
import com.sidifensen.domain.result.Result;
import com.sidifensen.domain.vo.ArticleVo;
import com.sidifensen.domain.vo.FavoriteVo;
import com.sidifensen.service.FavoriteService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author sidifensen
 * @since 2025-09-16
 */
@RestController
@RequestMapping("/favorite")
public class FavoriteController {

    @Resource
    private FavoriteService favoriteService;

    /**
     * 新增收藏夹
     *
     * @param addFavoriteDto 收藏夹信息
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result<String> addFavorite(@RequestBody @Valid AddFavoriteDto addFavoriteDto) {
        favoriteService.addFavorite(addFavoriteDto);
        return Result.success();
    }

    /**
     * 根据articleId把文章添加到文章-收藏夹关联表，并增加收藏夹的文章数量
     *
     * @param articleId  文章ID
     * @param favoriteId 收藏夹ID
     * @return 操作结果
     */
    @PostMapping("/addArticle")
    public Result<String> addArticleToFavorite(@RequestParam @NotNull Integer articleId,
                                               @RequestParam @NotNull Integer favoriteId) {
        favoriteService.addArticleToFavorite(articleId, favoriteId);
        return Result.success();
    }

    /**
     * 根据articleId把文章从文章-收藏夹关联表中移除，并减少收藏夹的文章数量
     *
     * @param articleId  文章ID
     * @param favoriteId 收藏夹ID
     * @return 操作结果
     */
    @DeleteMapping("/removeArticle")
    public Result<String> removeArticleFromFavorite(@RequestParam @NotNull Integer articleId,
                                                    @RequestParam @NotNull Integer favoriteId) {
        favoriteService.removeArticleFromFavorite(articleId, favoriteId);
        return Result.success();
    }

    /**
     * 获取当前用户的收藏夹列表
     *
     * @return 收藏夹列表
     */
    @GetMapping("/list")
    public Result<List<FavoriteVo>> getCurrentUserFavoriteList() {
        List<FavoriteVo> favoriteList = favoriteService.getCurrentUserFavoriteList();
        return Result.success(favoriteList);
    }

    /**
     * 根据文章ID获取当前用户的收藏夹列表，并标识该文章在各收藏夹中的收藏状态
     *
     * @param articleId 文章ID
     * @return 收藏夹列表（包含收藏状态）
     */
    @GetMapping("/listByArticle")
    public Result<List<FavoriteVo>> getFavoriteListByArticleId(@RequestParam @NotNull Integer articleId) {
        List<FavoriteVo> favoriteList = favoriteService.getFavoriteListByArticleId(articleId);
        return Result.success(favoriteList);
    }

    /**
     * 根据用户ID获取收藏夹列表
     *
     * @param userId 用户ID，可选参数
     * @return 收藏夹列表
     */
    @GetMapping("/listByUser")
    public Result<List<FavoriteVo>> getFavoriteListByUserId(@RequestParam(required = false) Integer userId) {
        List<FavoriteVo> favoriteList = favoriteService.getFavoriteListByUserId(userId);
        return Result.success(favoriteList);
    }

    /**
     * 根据收藏夹ID获取收藏夹中的文章列表
     *
     * @param favoriteId 收藏夹ID
     * @return 文章列表
     */
    @GetMapping("/articles")
    public Result<List<ArticleVo>> getArticleListByFavoriteId(@RequestParam @NotNull Integer favoriteId) {
        List<ArticleVo> articleList = favoriteService.getArticleListByFavoriteId(favoriteId);
        return Result.success(articleList);
    }

}
