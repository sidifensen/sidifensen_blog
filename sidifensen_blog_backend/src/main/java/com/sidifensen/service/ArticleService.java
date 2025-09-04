package com.sidifensen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sidifensen.domain.dto.ArticleAuditDto;
import com.sidifensen.domain.dto.ArticleDto;
import com.sidifensen.domain.dto.ArticleStatusDto;
import com.sidifensen.domain.entity.Article;
import com.sidifensen.domain.vo.ArticleVo;
import com.sidifensen.domain.vo.PageVo;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author sidifensen
 * @since 2025-08-24
 */
public interface ArticleService extends IService<Article> {

    // 获取文章列表
    PageVo<List<ArticleVo>> getArticleList(Integer pageNum, Integer pageSize);

    // 获取用户文章列表
    PageVo<List<ArticleVo>> getUserArticleList(Integer pageNum, Integer pageSize, ArticleStatusDto articleStatusDto);

    // 获取文章详情
    ArticleVo getArticle(Integer articleId);

    // 新增文章
    void addArticle(ArticleDto articleDto);

    // 更新文章
    void updateArticle(ArticleDto articleDto);

    // 删除文章
    void deleteArticle(Integer articleId);

    // 管理员获取文章列表
    List<ArticleVo> adminGetArticleList();

    // 管理员获取文章详情
    ArticleVo adminGetArticle(Integer articleId);

    // 管理员更新文章
    void adminUpdateArticle(ArticleDto articleDto);

    // 管理员删除文章
    void adminDeleteArticle(Integer articleId);

    // 管理员审核文章
    void adminExamineArticle(ArticleAuditDto articleAuditDto);

    // 保存草稿
    void saveDraft(ArticleDto articleDto);

    // 管理员搜索文章
    List<ArticleVo> adminSearchArticle(ArticleDto articleDto);

    // 管理员批量删除文章
    void adminDeleteBatchArticle(List<ArticleAuditDto> articleAuditDtos);

    // 管理员批量审核文章
    void adminExamineBatchArticle(List<ArticleAuditDto> articleAuditDtos);

    // 管理员根据用户ID获取文章列表
    List<ArticleVo> adminGetArticlesByUserId(Integer userId);
}
