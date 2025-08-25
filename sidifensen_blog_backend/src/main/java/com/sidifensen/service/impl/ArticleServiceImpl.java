package com.sidifensen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.dto.ArticleDto;
import com.sidifensen.domain.dto.ArticleStatusDto;
import com.sidifensen.domain.entity.Article;
import com.sidifensen.domain.enums.EditStatusEnum;
import com.sidifensen.domain.enums.ExamineStatusEnum;
import com.sidifensen.domain.enums.VisibleRangeEnum;
import com.sidifensen.domain.vo.ArticleVo;
import com.sidifensen.domain.vo.PageVo;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.ArticleMapper;
import com.sidifensen.mapper.TagMapper;
import com.sidifensen.service.ArticleService;
import com.sidifensen.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author sidifensen
 * @since 2025-08-24
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private TagMapper tagMapper;

    @Override
    public PageVo<List<ArticleVo>> getArticleList(Integer pageNum, Integer pageSize) {
        Page<Article> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Article> qw = new LambdaQueryWrapper<Article>()
                .eq(Article::getUserId, SecurityUtils.getUserId())
                .eq(Article::getExamineStatus, ExamineStatusEnum.PASS.getCode())
                .eq(Article::getEditStatus, EditStatusEnum.PUBLISHED.getCode())
                .eq(Article::getVisibleRange, VisibleRangeEnum.ALL.getCode())
                .orderByDesc(Article::getCreateTime);

        List<Article> articleList = articleMapper.selectPage(page, qw).getRecords();
        List<ArticleVo> articleVoList = articleList.stream().map(article -> {
            ArticleVo articleVo = BeanUtil.copyProperties(article, ArticleVo.class);
            return articleVo;
        }).toList();

        return new PageVo<>(articleVoList, page.getTotal());
    }

    @Override
    public PageVo<List<ArticleVo>> getUserArticleList(Integer pageNum, Integer pageSize, ArticleStatusDto articleStatusDto) {
        Integer userId = SecurityUtils.getUserId();
        Page<Article> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Article> qw = new LambdaQueryWrapper<Article>()
                .eq(userId != 0, Article::getUserId, userId)
                .eq(ObjectUtil.isNotEmpty(articleStatusDto.getExamineStatus()), Article::getExamineStatus, articleStatusDto.getExamineStatus())
                .eq(ObjectUtil.isNotEmpty(articleStatusDto.getEditStatus()), Article::getEditStatus, articleStatusDto.getEditStatus())
                .eq(ObjectUtil.isNotEmpty(articleStatusDto.getVisibleRange()), Article::getVisibleRange, articleStatusDto.getVisibleRange())
                .eq(ObjectUtil.isNotEmpty(articleStatusDto.getReprintType()), Article::getReprintType, articleStatusDto.getReprintType())
                .like(ObjectUtil.isNotEmpty(articleStatusDto.getTitle()), Article::getTitle, articleStatusDto.getTitle())
                .like(ObjectUtil.isNotEmpty(articleStatusDto.getDescription()), Article::getDescription, articleStatusDto.getDescription())
                .orderByDesc(articleStatusDto.getOrderBy() == 0 ? Article::getCreateTime : Article::getReadCount);

        List<Article> articleList = articleMapper.selectPage(page, qw).getRecords();
        List<ArticleVo> articleVoList = articleList.stream().map(article -> {
            ArticleVo articleVo = BeanUtil.copyProperties(article, ArticleVo.class);
            return articleVo;
        }).toList();

        return new PageVo<>(articleVoList, page.getTotal());
    }

    @Override
    public ArticleVo getArticle(Integer articleId) {
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            throw new BlogException(BlogConstants.NotFoundArticle);
        }
        return BeanUtil.copyProperties(article, ArticleVo.class);
    }

    @Override
    public void addArticle(ArticleDto articleDto) {
        Article article = BeanUtil.copyProperties(articleDto, Article.class);
        article.setUserId(SecurityUtils.getUserId());
        if (articleMapper.insert(article) < 1) {
            throw new BlogException(BlogConstants.AddArticleError);
        }
    }

    @Override
    public void updateArticle(ArticleDto articleDto) {
        Article article = BeanUtil.copyProperties(articleDto, Article.class);
        if (articleMapper.updateById(article) < 1) {
            throw new BlogException(BlogConstants.UpdateArticleError);
        }
    }

    @Override
    public void deleteArticle(Integer articleId) {
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            throw new BlogException(BlogConstants.NotFoundArticle);
        }
        if (article.getUserId() != SecurityUtils.getUserId()) {
            throw new BlogException(BlogConstants.CannotHandleOthersArticle);
        }
        articleMapper.deleteById(articleId);
    }

    @Override
    public List<ArticleVo> adminGetArticleList() {
        return List.of();
    }

    @Override
    public ArticleVo adminGetArticle(Integer articleId) {
        return null;
    }

    @Override
    public void adminUpdateArticle(ArticleDto articleDto) {

    }

    @Override
    public void adminDeleteArticle(Integer articleId) {

    }
}
