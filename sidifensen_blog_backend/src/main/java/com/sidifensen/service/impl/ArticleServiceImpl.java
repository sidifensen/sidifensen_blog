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
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
@Slf4j
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    ExecutorService executorService = new ThreadPoolExecutor(
            2, 4, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(500),
            new MyThreadFactory("ArticleServiceImpl")
    );
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
    @Resource
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
                .and(ObjectUtil.isNotEmpty(articleStatusDto.getKeyword()), wrapper -> wrapper
                        .like(Article::getTag, articleStatusDto.getKeyword()) // 标签
                        .or()
                        .like(Article::getTitle, articleStatusDto.getKeyword()) // 标题
                        .or()
                        .like(Article::getDescription, articleStatusDto.getKeyword())); // 描述

        // 添加根据年月查询的条件
        if (ObjectUtil.isNotEmpty(articleStatusDto.getYear())) {
            if (ObjectUtil.isNotEmpty(articleStatusDto.getMonth())) {
                // 如果指定了年份和月份，查询该月
                LocalDateTime firstDayOfMonth = LocalDateTime.of(articleStatusDto.getYear(), articleStatusDto.getMonth(), 1, 0, 0, 0);
                LocalDateTime lastDayOfMonth = firstDayOfMonth.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);
                qw.between(Article::getCreateTime, firstDayOfMonth, lastDayOfMonth);
            } else {
                // 如果只指定了年份，查询整年
                LocalDateTime firstDayOfYear = LocalDateTime.of(articleStatusDto.getYear(), 1, 1, 0, 0, 0);
                LocalDateTime lastDayOfYear = LocalDateTime.of(articleStatusDto.getYear(), 12, 31, 23, 59, 59);
                qw.between(Article::getCreateTime, firstDayOfYear, lastDayOfYear);
            }
        } else if (ObjectUtil.isNotEmpty(articleStatusDto.getCreateTime())) {
            // 兼容原有的createTime参数
            LocalDateTime dateTime = DateUtil.toLocalDateTime(articleStatusDto.getCreateTime());
            LocalDateTime firstDayOfMonth = dateTime.with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN);
            LocalDateTime lastDayOfMonth = dateTime.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);
            qw.between(Article::getCreateTime, firstDayOfMonth, lastDayOfMonth);
        }


        if (ObjectUtil.isEmpty(articleStatusDto.getOrderBy()) || articleStatusDto.getOrderBy() == 0) {
            qw.orderByDesc(Article::getCreateTime);
        } else {
            qw.orderByDesc(Article::getReadCount);
        }

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
        auditArticle(articleDto.setId(article.getId()));
    }

    // 文章审核
    private void auditArticle(ArticleDto articleDto) {
        executorService.execute(() -> {

            MessageDto messageDto = new MessageDto();
            messageDto.setType(MessageTypeEnum.SYSTEM.getCode());
            // 进行自动审核
            if (sidifensenConfig.isArticleAutoAudit()) {
                ImageAuditResult auditResult = textAuditUtils.auditTextWithDetailsSplit(articleDto.getTitle() + " " + articleDto.getContent());

                // 先更新文章的审核状态
                Article updateArticle = new Article();
                updateArticle.setId(articleDto.getId());
                
                if (auditResult.getStatus().equals(ExamineStatusEnum.PASS.getCode())) {
                    // 文字审核通过，更新审核状态为通过
                    updateArticle.setExamineStatus(ExamineStatusEnum.PASS.getCode());
                    articleMapper.updateById(updateArticle);
                    log.info("文章审核通过，ID: {}", String.valueOf(articleDto.getId()));

                    // 文字审核通过，更新文章所属的专栏
                    if (ObjectUtil.isNotEmpty(articleDto.getColumnIds())) {
                        List<Integer> columnIds = articleDto.getColumnIds();
                        columnIds.forEach(columnId -> {
                            ArticleColumn articleColumn = articleColumnMapper.selectOne(new LambdaQueryWrapper<ArticleColumn>().select(ArticleColumn::getSort)
                                    .eq(ArticleColumn::getColumnId, columnId).orderByDesc(ArticleColumn::getSort).last("limit 1"));
                            Integer sort = articleColumn == null ? 0 : articleColumn.getSort();
                            ArticleColumn newArticleColumn = new ArticleColumn();
                            newArticleColumn.setArticleId(articleDto.getId());
                            newArticleColumn.setColumnId(columnId);
                            newArticleColumn.setSort(sort + 1); // 最大排序值加1
                            articleColumnMapper.insert(newArticleColumn);
                        });
                    }
                } else if (auditResult.getStatus().equals(ExamineStatusEnum.NO_PASS.getCode())) {
                    // 文字审核不通过，更新审核状态为不通过，并记录原因，并发送消息给用户
                    updateArticle.setExamineStatus(ExamineStatusEnum.NO_PASS.getCode());
                    articleMapper.updateById(updateArticle);
                    log.info("文章审核不通过，ID: {}，标题: {}，原因: {}", articleDto.getId(), articleDto.getTitle(), auditResult.getErrorMessage());
                    
                    messageDto.setContent(MessageConstants.ArticleAuditNotPass(articleDto.getId(), articleDto.getTitle(), auditResult.getErrorMessage()));
                    messageDto.setReceiverId(articleDto.getUserId());
                    messageService.sendToUser(messageDto);
                } else if (auditResult.getStatus().equals(ExamineStatusEnum.WAIT.getCode())) {
                    // 需要人工审核，更新审核状态为待审核，并记录原因，并发送消息给管理员
                    updateArticle.setExamineStatus(ExamineStatusEnum.WAIT.getCode());
                    articleMapper.updateById(updateArticle);
                    log.info("文章需要人工审核，ID: {}", articleDto.getId());
                    
                    messageDto.setContent(MessageConstants.ArticleNeedReview(articleDto.getId(), articleDto.getTitle(), auditResult.getErrorMessage()));
                    messageService.sendToAdmin(messageDto);

                    HashMap<String, Object> sendEmail = new HashMap<>();
                    sendEmail.put("text", String.format(MessageConstants.ARTICLE_NEED_REVIEW, articleDto.getId(), auditResult.getErrorMessage()));
                    rabbitTemplate.convertAndSend(RabbitMQConstants.Examine_Exchange, RabbitMQConstants.Examine_Routing_Key, sendEmail);
                }
            } else {
                // 没有开启自动审核, 需要人工审核，发送消息给管理员
                messageDto.setContent(MessageConstants.ArticleNeedReview(articleDto.getId(), articleDto.getTitle(), null));
                messageService.sendToAdmin(messageDto);

                HashMap<String, Object> sendEmail = new HashMap<>();
                sendEmail.put("text", String.format(MessageConstants.ARTICLE_NEED_REVIEW, articleDto.getId(), null));
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
        if (articleDto.getEditStatus().equals(EditStatusEnum.DRAFT.getCode()) || articleDto.getEditStatus().equals(EditStatusEnum.RECYCLE.getCode())) {
            // 如果设置草稿箱和回收站,则删除专栏文章
            articleColumnMapper.delete(new LambdaQueryWrapper<ArticleColumn>().in(ArticleColumn::getArticleId, articleDto.getId()));
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
        LambdaQueryWrapper<Article> qw = new LambdaQueryWrapper<Article>()
                .orderByDesc(Article::getCreateTime);
        List<Article> articleList = articleMapper.selectList(qw);
        List<ArticleVo> articleVoList = BeanUtil.copyToList(articleList, ArticleVo.class);
        return articleVoList;
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
                .select(Article::getTitle)
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
            articleAuditDto.setExamineReason(MessageConstants.ArticleAuditNotPass(articleAuditDto.getArticleId(), article.getTitle(), examineReason));
        }

        // 更新文章审核状态
        Article updateArticle = new Article();
        updateArticle.setId(articleAuditDto.getArticleId());
        updateArticle.setExamineStatus(examineStatus);
        articleMapper.updateById(updateArticle);

        // 发送消息给用户
        MessageDto messageDto = new MessageDto();
        messageDto.setContent(MessageConstants.ArticleAuditNotPass(articleAuditDto.getArticleId(), article.getTitle(), examineReason));
        messageDto.setReceiverId(article.getUserId());
        messageService.sendToUser(messageDto);
    }



}
