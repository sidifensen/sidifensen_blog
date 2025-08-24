package com.sidifensen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.dto.ArticleDto;
import com.sidifensen.domain.entity.Article;
import com.sidifensen.domain.entity.ArticleTag;
import com.sidifensen.domain.entity.Tag;
import com.sidifensen.domain.enums.EditStatusEnum;
import com.sidifensen.domain.enums.ExamineStatusEnum;
import com.sidifensen.domain.enums.VisibleRangeEnum;
import com.sidifensen.domain.vo.ArticleVo;
import com.sidifensen.domain.vo.PageVo;
import com.sidifensen.mapper.ArticleMapper;
import com.sidifensen.mapper.ArticleTagMapper;
import com.sidifensen.mapper.TagMapper;
import com.sidifensen.service.ArticleService;
import com.sidifensen.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Resource
    private ArticleTagMapper articleTagMapper;

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

        List<Integer> articleIds = articleList.stream().map(Article::getId).toList();
        List<ArticleTag> articleTags = articleTagMapper.selectList(new LambdaQueryWrapper<ArticleTag>().in(ArticleTag::getArticleId, articleIds));
        List<Integer> tagIds = articleTags.stream().map(ArticleTag::getTagId).toList();
        List<Tag> tags = tagMapper.selectByIds(tagIds);

        Map<Integer, String> tagMap = tags.stream().collect(Collectors.toMap(Tag::getId, Tag::getName));
        List<ArticleVo> articleVoList = articleList.stream().map(article -> {
            ArticleVo articleVo = BeanUtil.copyProperties(article, ArticleVo.class);
            // 设置文章标签
            articleVo.setTag(tagIds.stream().map(tagMap::get).toList());
            return articleVo;
        }).toList();

        return new PageVo<>(articleVoList, page.getTotal());
    }

    @Override
    public ArticleVo getArticle(Integer articleId) {
        return null;
    }

    @Override
    public void addArticle(ArticleDto articleDto) {

    }

    @Override
    public void updateArticle(ArticleDto articleDto) {

    }

    @Override
    public void deleteArticle(Integer articleId) {

    }
}
