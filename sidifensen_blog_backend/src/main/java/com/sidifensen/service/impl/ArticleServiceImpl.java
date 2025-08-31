package com.sidifensen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.config.SidifensenConfig;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.constants.MessageConstants;
import com.sidifensen.domain.constants.RabbitMQConstants;
import com.sidifensen.domain.dto.ArticleAuditDto;
import com.sidifensen.domain.dto.ArticleDto;
import com.sidifensen.domain.dto.ArticleStatusDto;
import com.sidifensen.domain.dto.MessageDto;
import com.sidifensen.domain.entity.Article;
import com.sidifensen.domain.entity.ArticleColumn;
import com.sidifensen.domain.entity.Column;
import com.sidifensen.domain.enums.EditStatusEnum;
import com.sidifensen.domain.enums.ExamineStatusEnum;
import com.sidifensen.domain.enums.MessageTypeEnum;
import com.sidifensen.domain.enums.VisibleRangeEnum;
import com.sidifensen.domain.result.ImageAuditResult;
import com.sidifensen.domain.vo.ArticleVo;
import com.sidifensen.domain.vo.ColumnVo;
import com.sidifensen.domain.vo.PageVo;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.ArticleColumnMapper;
import com.sidifensen.mapper.ArticleMapper;
import com.sidifensen.mapper.ColumnMapper;
import com.sidifensen.service.ArticleService;
import com.sidifensen.service.MessageService;
import com.sidifensen.utils.MyThreadFactory;
import com.sidifensen.utils.SecurityUtils;
import com.sidifensen.utils.TextAuditUtils;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
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
    private ArticleColumnMapper articleColumnMapper;

    @Resource
    private ColumnMapper columnMapper;

    @Resource
    private TextAuditUtils textAuditUtils;

    @Resource
    private SidifensenConfig sidifensenConfig;

    @Resource
    private MessageService messageService;

    ExecutorService executorService = new ThreadPoolExecutor(
            2, 4, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(500),
            new MyThreadFactory("ArticleServiceImpl")
    );
    @Autowired
    private RabbitTemplate rabbitTemplate;

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
                .and(ObjectUtil.isNotEmpty(articleStatusDto.getSearch()), wrapper -> wrapper
                        .like(Article::getTag, articleStatusDto.getSearch()) // 标签
                        .or()
                        .like(Article::getTitle, articleStatusDto.getSearch()) // 标题
                        .or()
                        .like(Article::getDescription, articleStatusDto.getSearch())); // 描述

        // 添加根据年月查询的条件
        if (ObjectUtil.isNotEmpty(articleStatusDto.getCreateTime())) {
            // 获取指定年月的第一天和最后一天
            LocalDateTime dateTime = DateUtil.toLocalDateTime(articleStatusDto.getCreateTime());
            LocalDateTime firstDayOfMonth = dateTime.with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN);
            LocalDateTime lastDayOfMonth = dateTime.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);

            qw.between(Article::getCreateTime, firstDayOfMonth, lastDayOfMonth);
        }

        qw.orderByDesc(articleStatusDto.getOrderBy() == 0 ? Article::getCreateTime : Article::getReadCount);
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
        if (ObjectUtil.isEmpty(article)) {
            throw new BlogException(BlogConstants.NotFoundArticle);
        }
        if (article.getUserId() != SecurityUtils.getUserId()) {
            throw new BlogException(BlogConstants.CannotHandleOthersArticle);
        }
        ArticleVo articleVo = BeanUtil.copyProperties(article, ArticleVo.class);

        // 如果文章状态为已发布, 则查询文章所属的专栏
        if (article.getEditStatus().equals(EditStatusEnum.PUBLISHED.getCode())) {
            List<ArticleColumn> articleColumns = articleColumnMapper.selectList(new LambdaQueryWrapper<ArticleColumn>().in(ArticleColumn::getArticleId, article.getId()));
            if (ObjectUtil.isNotEmpty(articleColumns)) {
                List<Integer> columnIds = articleColumns.stream().map(ArticleColumn::getColumnId).collect(Collectors.toList());
                List<Column> columns = columnMapper.selectByIds(columnIds);
                articleVo.setColumns(BeanUtil.copyToList(columns, ColumnVo.class));
            }
        }
        return articleVo;
    }

    @Override
    public void addArticle(ArticleDto articleDto) {
        Article article = BeanUtil.copyProperties(articleDto, Article.class);
        article.setUserId(SecurityUtils.getUserId());
        if (!articleMapper.insertOrUpdate(article)) {
            throw new BlogException(BlogConstants.AddArticleError);
        }

        if (ObjectUtil.isNotEmpty(articleDto.getColumnIds())) {
            executorService.execute(() -> {
                List<Integer> columnIds = articleDto.getColumnIds();
                columnIds.forEach(columnId -> {
                    ArticleColumn articleColumn = articleColumnMapper.selectOne(new LambdaQueryWrapper<ArticleColumn>().select(ArticleColumn::getSort)
                            .eq(ArticleColumn::getColumnId, columnId).orderByDesc(ArticleColumn::getSort).last("limit 1"));
                    Integer sort = articleColumn == null ? 0 : articleColumn.getSort();
                    ArticleColumn newArticleColumn = new ArticleColumn();
                    newArticleColumn.setArticleId(article.getId());
                    newArticleColumn.setColumnId(columnId);
                    newArticleColumn.setSort(sort + 1);
                    articleColumnMapper.insert(newArticleColumn);
                });
            });
        }

        auditArticle(article);
    }

    // 文章审核
    private void auditArticle(Article article) {
        executorService.execute(() -> {

            MessageDto messageDto = new MessageDto();
            messageDto.setType(MessageTypeEnum.SYSTEM.getCode());
            // 进行自动审核
            if (sidifensenConfig.isArticleAutoAudit()) {
                ImageAuditResult auditResult = textAuditUtils.auditTextWithDetails(article.getTitle() + " " + article.getContent());

                if (auditResult.getStatus().equals(ExamineStatusEnum.PASS.getCode())) {
                    // 文字审核通过，更新审核状态为通过
                    article.setExamineStatus(ExamineStatusEnum.PASS.getCode());
                } else if (auditResult.getStatus().equals(ExamineStatusEnum.NO_PASS)) {
                    // 文字审核不通过，更新审核状态为不通过，并记录原因，并发送消息给用户
                    article.setExamineStatus(ExamineStatusEnum.NO_PASS.getCode());
                    messageDto.setContent(MessageConstants.ArticleAuditNotPass(article.getId(), auditResult.getErrorMessage()));
                    messageDto.setReceiverId(article.getUserId());
                    messageService.sendToUser(messageDto);

                } else if (auditResult.getStatus().equals(ExamineStatusEnum.WAIT.getCode())) {
                    // 需要人工审核，更新审核状态为待审核，并记录原因，并发送消息给管理员
                    messageDto.setContent(MessageConstants.ArticleNeedReview(article.getId(), auditResult.getErrorMessage()));
                    messageService.sendToAdmin(messageDto);
                    // 发送邮件给管理员
                    HashMap<String, Object> sendEmail = new HashMap<>();
                    sendEmail.put("text", MessageConstants.ArticleNeedReview(article.getId(), auditResult.getErrorMessage()));
                    rabbitTemplate.convertAndSend(RabbitMQConstants.Examine_Exchange, RabbitMQConstants.Examine_Routing_Key, sendEmail);
                }
                articleMapper.updateById(article);
            } else {
                // 需要人工审核，发送消息给管理员
                messageDto.setContent(MessageConstants.ArticleNeedReview(article.getId(), null));
                messageService.sendToAdmin(messageDto);

                // 发送邮件给管理员
                HashMap<String, Object> sendEmail = new HashMap<>();
                sendEmail.put("text", MessageConstants.ArticleNeedReview(article.getId(), null));
                rabbitTemplate.convertAndSend(RabbitMQConstants.Examine_Exchange, RabbitMQConstants.Examine_Routing_Key, sendEmail);
            }
        });
    }

    // 保存草稿
    @Override
    public void saveDraft(ArticleDto articleDto) {
        Article article = BeanUtil.copyProperties(articleDto, Article.class);
        article.setUserId(SecurityUtils.getUserId());
        article.setEditStatus(EditStatusEnum.DRAFT.getCode());
        articleMapper.insertOrUpdate(article);
    }

    @Override
    public void updateArticle(ArticleDto articleDto) {
        Article userArticle = articleMapper.selectById(articleDto.getId());
        if (ObjectUtil.isEmpty(userArticle)) {
            throw new BlogException(BlogConstants.NotFoundArticle);
        }
        if (userArticle.getUserId() != SecurityUtils.getUserId()) {
            throw new BlogException(BlogConstants.CannotHandleOthersArticle);
        }
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
        articleColumnMapper.delete(new LambdaQueryWrapper<ArticleColumn>().in(ArticleColumn::getArticleId, articleId));
    }

    @Override
    public List<ArticleVo> adminGetArticleList() {
        return List.of();
    }

    @Override
    public ArticleVo adminGetArticle(Integer articleId) {
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            throw new BlogException(BlogConstants.NotFoundArticle);
        }
        ArticleVo articleVo = BeanUtil.copyProperties(article, ArticleVo.class);
        List<ArticleColumn> articleColumns = articleColumnMapper.selectList(new LambdaQueryWrapper<ArticleColumn>().eq(ArticleColumn::getId, article.getId()));
        if (ObjectUtil.isNotEmpty(articleColumns)) {
            List<Integer> columnIds = articleColumns.stream().map(ArticleColumn::getColumnId).collect(Collectors.toList());
            List<Column> columns = columnMapper.selectByIds(columnIds);
            articleVo.setColumns(BeanUtil.copyToList(columns, ColumnVo.class));
        }
        return articleVo;
    }

    @Override
    public void adminUpdateArticle(ArticleDto articleDto) {
        Article article = BeanUtil.copyProperties(articleDto, Article.class);
        if (articleMapper.updateById(article) < 1) {
            throw new BlogException(BlogConstants.UpdateArticleError);
        }
    }

    @Override
    public void adminDeleteArticle(Integer articleId) {
        if (articleMapper.deleteById(articleId) < 1) {
            throw new BlogException(BlogConstants.DeleteArticleError);
        }
    }

    @Override
    public void adminExamineArticle(ArticleAuditDto articleAuditDto) {
        // 只查询userId字段
        Article article = articleMapper.selectOne(new LambdaQueryWrapper<Article>()
                .select(Article::getUserId)
                .eq(Article::getId, articleAuditDto.getArticleId()));

        if (ObjectUtil.isEmpty(article)) {
            throw new BlogException(BlogConstants.NotFoundArticle);
        }
        Integer examineStatus = articleAuditDto.getExamineStatus();
        String examineReason = articleAuditDto.getExamineReason();
        // 审核状态为通过
        if (examineStatus.equals(ExamineStatusEnum.PASS.getCode())) {
            articleAuditDto.setExamineStatus(ExamineStatusEnum.PASS.getCode());
        } else if (examineStatus.equals(ExamineStatusEnum.NO_PASS.getCode())) {
            articleAuditDto.setExamineStatus(ExamineStatusEnum.NO_PASS.getCode());
            articleAuditDto.setExamineReason(MessageConstants.ArticleAuditNotPass(articleAuditDto.getArticleId(), examineReason));
        }

        // 更新文章审核状态
        Article updateArticle = new Article();
        updateArticle.setId(articleAuditDto.getArticleId());
        updateArticle.setExamineStatus(examineStatus);
        articleMapper.updateById(updateArticle);

        // 发送消息给用户
        MessageDto messageDto = new MessageDto();
        messageDto.setContent(MessageConstants.ArticleAuditNotPass(articleAuditDto.getArticleId(), examineReason));
        messageDto.setReceiverId(article.getUserId());
        messageService.sendToUser(messageDto);
    }

}
