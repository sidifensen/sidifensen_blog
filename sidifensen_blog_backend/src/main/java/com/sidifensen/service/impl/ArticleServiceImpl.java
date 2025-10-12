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
import com.sidifensen.domain.entity.*;
import com.sidifensen.domain.enums.*;
import com.sidifensen.domain.result.AuditResult;
import com.sidifensen.domain.vo.*;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.ArticleColumnMapper;
import com.sidifensen.mapper.ArticleMapper;
import com.sidifensen.mapper.ColumnMapper;
import com.sidifensen.mapper.SysUserMapper;
import com.sidifensen.redis.RedisComponent;
import com.sidifensen.service.*;
import com.sidifensen.utils.IpUtils;
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
import java.util.*;
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
            new MyThreadFactory("ArticleServiceImpl"));
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
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private LikeService likeService;

    @Resource
    private HistoryService historyService;

    @Resource
    private IpUtils ipUtils;

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private CommentService commentService;

    @Override
    public PageVo<List<ArticleVo>> getAllArticleList(Integer pageNum, Integer pageSize) {
        Page<Article> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Article> qw = new LambdaQueryWrapper<Article>()
                .eq(Article::getExamineStatus, ExamineStatusEnum.PASS.getCode())
                .eq(Article::getEditStatus, EditStatusEnum.PUBLISHED.getCode())
                .eq(Article::getVisibleRange, VisibleRangeEnum.ALL.getCode())
                .orderByDesc(Article::getUpdateTime);

        List<Article> articleList = articleMapper.selectPage(page, qw).getRecords();
        List<ArticleVo> articleVoList = articleList.stream().map(article -> {
            ArticleVo articleVo = BeanUtil.copyProperties(article, ArticleVo.class);
            return articleVo;
        }).collect(Collectors.toList());

        // 添加用户昵称
        if (!articleVoList.isEmpty()) {
            List<Integer> userIds = articleVoList.stream().map(ArticleVo::getUserId).collect(Collectors.toList());
            List<SysUser> users = sysUserMapper
                    .selectList(new LambdaQueryWrapper<SysUser>().in(SysUser::getId, userIds));
            Map<Integer, String> userIdToNicknameMap = users.stream()
                    .collect(Collectors.toMap(SysUser::getId, SysUser::getNickname));
            Map<Integer, String> userIdToAvatarMap = users.stream()
                    .collect(Collectors.toMap(SysUser::getId, SysUser::getAvatar));
            articleVoList.forEach(articleVo -> articleVo.setNickname(userIdToNicknameMap.get(articleVo.getUserId())));
            articleVoList.forEach(articleVo -> articleVo.setAvatar(userIdToAvatarMap.get(articleVo.getUserId())));
        }

        return new PageVo<>(articleVoList, page.getTotal());
    }

    @Override
    public PageVo<List<ArticleVo>> getUserArticleList(Integer pageNum, Integer pageSize,
            ArticleStatusDto articleStatusDto) {
        Integer currentUserId = SecurityUtils.getUserId(); // 当前登录用户ID

        Page<Article> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Article> qw = new LambdaQueryWrapper<Article>()
                .eq(Article::getUserId, articleStatusDto.getUserId());

        // 权限控制：只有查看自己的文章时才能看到私有内容和审核状态
        if (!currentUserId.equals(articleStatusDto.getUserId())) {
            // 如果当前的用户id不等于要查看他人文章时的限制条件
            qw.eq(Article::getVisibleRange, 0) // 只能查看全部可见的文章
                    .eq(Article::getExamineStatus, ExamineStatusEnum.PASS.getCode()) // 只能查看审核通过的文章
                    .eq(Article::getEditStatus, EditStatusEnum.PUBLISHED.getCode()); // 只能查看已发布的文章
        } else {
            // 查看自己文章时，应用用户指定的筛选条件
            qw.eq(ObjectUtil.isNotEmpty(articleStatusDto.getExamineStatus()), Article::getExamineStatus,
                    articleStatusDto.getExamineStatus())
                    .in(ObjectUtil.isNotEmpty(articleStatusDto.getExamineStatusList()), Article::getExamineStatus,
                            articleStatusDto.getExamineStatusList())
                    .eq(ObjectUtil.isNotEmpty(articleStatusDto.getEditStatus()), Article::getEditStatus,
                            articleStatusDto.getEditStatus())
                    .eq(ObjectUtil.isNotEmpty(articleStatusDto.getVisibleRange()), Article::getVisibleRange,
                            articleStatusDto.getVisibleRange());
        }

        // 通用筛选条件（对所有用户都适用）
        qw.eq(ObjectUtil.isNotEmpty(articleStatusDto.getReprintType()), Article::getReprintType,
                articleStatusDto.getReprintType())
                .and(ObjectUtil.isNotEmpty(articleStatusDto.getKeyword()), wrapper -> wrapper
                        .like(Article::getTag, articleStatusDto.getKeyword()) // 标签
                        .or()
                        .like(Article::getTitle, articleStatusDto.getKeyword()) // 标题
                        .or()
                        .like(Article::getDescription, articleStatusDto.getKeyword())); // 描述

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
    public PageVo<List<ArticleVo>> getArticleMangeList(Integer pageNum, Integer pageSize,
            ArticleStatusDto articleStatusDto) {
        Integer currentUserId = SecurityUtils.getUserId(); // 当前登录用户ID
        Integer targetUserId = ObjectUtil.isNotEmpty(articleStatusDto.getUserId()) ? articleStatusDto.getUserId()
                : currentUserId; // 目标用户ID

        Page<Article> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Article> qw = new LambdaQueryWrapper<Article>()
                .eq(targetUserId != 0, Article::getUserId, targetUserId);

        // 权限控制：只有查看自己的文章时才能看到私有内容和审核状态
        if (!currentUserId.equals(targetUserId)) {
            // 如果当前的用户id不等于要查看他人文章时的限制条件
            qw.eq(Article::getVisibleRange, 0) // 只能查看全部可见的文章
                    .eq(Article::getExamineStatus, ExamineStatusEnum.PASS.getCode()) // 只能查看审核通过的文章
                    .eq(Article::getEditStatus, EditStatusEnum.PUBLISHED.getCode()); // 只能查看已发布的文章
        } else {
            // 查看自己文章时，应用用户指定的筛选条件
            qw.eq(ObjectUtil.isNotEmpty(articleStatusDto.getExamineStatus()), Article::getExamineStatus,
                    articleStatusDto.getExamineStatus())
                    .in(ObjectUtil.isNotEmpty(articleStatusDto.getExamineStatusList()), Article::getExamineStatus,
                            articleStatusDto.getExamineStatusList())
                    .eq(ObjectUtil.isNotEmpty(articleStatusDto.getEditStatus()), Article::getEditStatus,
                            articleStatusDto.getEditStatus())
                    .eq(ObjectUtil.isNotEmpty(articleStatusDto.getVisibleRange()), Article::getVisibleRange,
                            articleStatusDto.getVisibleRange());
        }

        // 通用筛选条件（对所有用户都适用）
        qw.eq(ObjectUtil.isNotEmpty(articleStatusDto.getReprintType()), Article::getReprintType,
                articleStatusDto.getReprintType())
                .and(ObjectUtil.isNotEmpty(articleStatusDto.getKeyword()), wrapper -> wrapper
                        .like(Article::getTag, articleStatusDto.getKeyword()) // 标签
                        .or()
                        .like(Article::getTitle, articleStatusDto.getKeyword()) // 标题
                        .or()
                        .like(Article::getDescription, articleStatusDto.getKeyword())); // 描述

        // 添加根据年月查询的条件
        if (ObjectUtil.isNotEmpty(articleStatusDto.getYear()) || ObjectUtil.isNotEmpty(articleStatusDto.getMonth())) {
            if (ObjectUtil.isNotEmpty(articleStatusDto.getMonth())) {
                // 确定年份：如果有指定年份则使用，否则使用当前年份
                int year = ObjectUtil.isNotEmpty(articleStatusDto.getYear()) ? articleStatusDto.getYear()
                        : LocalDateTime.now().getYear();

                // 查询指定年份的指定月份
                LocalDateTime firstDayOfMonth = LocalDateTime.of(year, articleStatusDto.getMonth(), 1, 0, 0, 0);
                LocalDateTime lastDayOfMonth = firstDayOfMonth.with(TemporalAdjusters.lastDayOfMonth())
                        .with(LocalTime.MAX);
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
    public ArticleStatisticsVo getUserArticleStatistics() {
        Integer userId = SecurityUtils.getUserId();

        LambdaQueryWrapper<Article> qw = new LambdaQueryWrapper<Article>()
                .select(Article::getEditStatus, Article::getExamineStatus)
                .eq(Article::getUserId, userId);
        List<Article> articles = articleMapper.selectList(qw);

        // 统计各种状态的文章数量
        long totalCount = articles.size();
        long publishedCount = articles.stream()
                .filter(article -> Objects.equals(article.getEditStatus(), EditStatusEnum.PUBLISHED.getCode())
                        && Objects.equals(article.getExamineStatus(), ExamineStatusEnum.PASS.getCode()))
                .count();
        long reviewingCount = articles.stream()
                .filter(article -> Objects.equals(article.getEditStatus(), EditStatusEnum.PUBLISHED.getCode())
                        && Objects.equals(article.getExamineStatus(), ExamineStatusEnum.WAIT.getCode()))
                .count();
        long draftCount = articles.stream()
                .filter(article -> Objects.equals(article.getEditStatus(), EditStatusEnum.DRAFT.getCode()))
                .count();
        long garbageCount = articles.stream()
                .filter(article -> Objects.equals(article.getEditStatus(), EditStatusEnum.RECYCLE.getCode()))
                .count();

        // 构建返回对象
        ArticleStatisticsVo statisticsVo = new ArticleStatisticsVo();
        statisticsVo.setTotalCount(totalCount);
        statisticsVo.setPublishedCount(publishedCount);
        statisticsVo.setReviewingCount(reviewingCount);
        statisticsVo.setDraftCount(draftCount);
        statisticsVo.setGarbageCount(garbageCount);

        return statisticsVo;
    }

    @Override
    public ArticleStatisticsVo getUserArticleStatisticsById(Integer userId) {
        // 只查询已发布且审核通过的文章的阅读量字段
        List<Article> articles = articleMapper.selectList(
                new LambdaQueryWrapper<Article>()
                        .select(Article::getReadCount)
                        .eq(Article::getUserId, userId)
                        .eq(Article::getEditStatus, EditStatusEnum.PUBLISHED.getCode())
                        .eq(Article::getExamineStatus, ExamineStatusEnum.PASS.getCode()));

        // 计算总阅读量
        Long totalReadCount = articles.stream()
                .mapToLong(Article::getReadCount)
                .sum();

        // 构建返回对象
        ArticleStatisticsVo statisticsVo = new ArticleStatisticsVo();
        statisticsVo.setTotalReadCount(totalReadCount);

        return statisticsVo;
    }

    @Override
    public ArticleVo getArticle(Integer articleId) {
        Article article = articleMapper.selectById(articleId);
        if (ObjectUtil.isEmpty(article)) {
            throw new BlogException(BlogConstants.NotFoundArticle);
        }
        ArticleVo articleVo = BeanUtil.copyProperties(article, ArticleVo.class);

        // 如果文章状态为已发布, 则查询文章所属的专栏
        if (article.getEditStatus().equals(EditStatusEnum.PUBLISHED.getCode())) {
            List<ArticleColumn> articleColumns = articleColumnMapper.selectList(
                    new LambdaQueryWrapper<ArticleColumn>().in(ArticleColumn::getArticleId, article.getId()));
            if (ObjectUtil.isNotEmpty(articleColumns)) {
                List<Integer> columnIds = articleColumns.stream().map(ArticleColumn::getColumnId)
                        .collect(Collectors.toList());
                List<Column> columns = columnMapper.selectBatchIds(columnIds);
                articleVo.setColumns(BeanUtil.copyToList(columns, ColumnVo.class));
            }
        }

        Boolean isLiked = likeService.isLiked(LikeTypeEnum.ARTICLE.getCode(), articleId);
        articleVo.setIsLiked(isLiked);

        // 获取当前用户ID
        Integer userId = SecurityUtils.getUserId() == 0 ? null : SecurityUtils.getUserId();
        // 获取客户端IP地址
        String ipAddress = ipUtils.getIp();
        // 异步增加阅读量
        this.incrReadCount(articleId, userId, ipAddress);

        return articleVo;
    }

    // 异步增加阅读量
    private void incrReadCount(Integer articleId, Integer userId, String ipAddress) {
        executorService.execute(() -> {
            try {
                // 检查并记录浏览，如果用户/访客已浏览过，则不增加阅读量
                boolean shouldIncrement = historyService.checkAndRecordRead(
                        articleId, userId, ipAddress);

                if (shouldIncrement) {
                    // 用户/访客首次阅读，增加阅读量（使用原子自增操作）
                    boolean updateResult = this.lambdaUpdate()
                            .eq(Article::getId, articleId)
                            .setIncrBy(Article::getReadCount, 1)
                            .update();

                    if (!updateResult) {
                        log.error("更新文章阅读量失败，文章ID: {}", articleId);
                        throw new BlogException(BlogConstants.UpdateArticleReadCountError);
                    }
                }
            } catch (Exception e) {
                log.error("异步增加文章阅读量失败，文章ID: {}, 错误: {}", articleId, e.getMessage(), e);
            }
        });
    }

    @Override
    public PageVo<List<ArticleVo>> searchArticleByTitle(String title, Integer pageNum, Integer pageSize) {
        Page<Article> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Article> qw = new LambdaQueryWrapper<Article>()
                .select(Article.class, info -> !info.getColumn().equals("content")) // 排除 content 字段
                .like(Article::getTitle, title)
                .eq(Article::getExamineStatus, ExamineStatusEnum.PASS.getCode())
                .eq(Article::getEditStatus, EditStatusEnum.PUBLISHED.getCode())
                .eq(Article::getVisibleRange, VisibleRangeEnum.ALL.getCode())
                .orderByDesc(Article::getUpdateTime);

        List<Article> articles = articleMapper.selectPage(page, qw).getRecords();

        // 转换为 ArticleVo 并填充用户信息
        List<ArticleVo> articleVos = articles.stream()
                .map(article -> {
                    ArticleVo articleVo = BeanUtil.copyProperties(article, ArticleVo.class);
                    // 查询作者信息
                    SysUser author = sysUserMapper.selectById(article.getUserId());
                    if (ObjectUtil.isNotEmpty(author)) {
                        articleVo.setNickname(author.getNickname());
                        articleVo.setAvatar(author.getAvatar());
                    }
                    return articleVo;
                })
                .collect(Collectors.toList());

        return new PageVo<>(articleVos, page.getTotal());
    }

    @Override
    public PageVo<List<ArticleVo>> searchArticleByTag(String tag, Integer pageNum, Integer pageSize) {
        Page<Article> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Article> qw = new LambdaQueryWrapper<Article>()
                .select(Article.class, info -> !info.getColumn().equals("content")) // 排除 content 字段
                .like(Article::getTag, tag)
                .eq(Article::getExamineStatus, ExamineStatusEnum.PASS.getCode())
                .eq(Article::getEditStatus, EditStatusEnum.PUBLISHED.getCode())
                .eq(Article::getVisibleRange, VisibleRangeEnum.ALL.getCode())
                .orderByDesc(Article::getUpdateTime);

        List<Article> articles = articleMapper.selectPage(page, qw).getRecords();

        // 转换为 ArticleVo 并填充用户信息
        List<ArticleVo> articleVos = articles.stream()
                .map(article -> {
                    ArticleVo articleVo = BeanUtil.copyProperties(article, ArticleVo.class);
                    // 查询作者信息
                    SysUser author = sysUserMapper.selectById(article.getUserId());
                    if (ObjectUtil.isNotEmpty(author)) {
                        articleVo.setNickname(author.getNickname());
                        articleVo.setAvatar(author.getAvatar());
                    }
                    return articleVo;
                })
                .collect(Collectors.toList());

        return new PageVo<>(articleVos, page.getTotal());
    }

    @Override
    public PageVo<List<ArticleVo>> searchArticleByContent(String content, Integer pageNum, Integer pageSize) {
        Page<Article> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Article> qw = new LambdaQueryWrapper<Article>()
                .like(Article::getContent, content)
                .eq(Article::getExamineStatus, ExamineStatusEnum.PASS.getCode())
                .eq(Article::getEditStatus, EditStatusEnum.PUBLISHED.getCode())
                .eq(Article::getVisibleRange, VisibleRangeEnum.ALL.getCode())
                .orderByDesc(Article::getUpdateTime);

        List<Article> articles = articleMapper.selectPage(page, qw).getRecords();

        // 转换为 ArticleVo 并填充用户信息
        List<ArticleVo> articleVos = articles.stream()
                .map(article -> {
                    ArticleVo articleVo = BeanUtil.copyProperties(article, ArticleVo.class);
                    // 查询作者信息
                    SysUser author = sysUserMapper.selectById(article.getUserId());
                    if (ObjectUtil.isNotEmpty(author)) {
                        articleVo.setNickname(author.getNickname());
                        articleVo.setAvatar(author.getAvatar());
                    }
                    return articleVo;
                })
                .collect(Collectors.toList());

        return new PageVo<>(articleVos, page.getTotal());
    }

    @Override
    public List<String> getTitleSuggestions(String keyword) {
        // 查询标题包含关键字的文章，只返回标题，最多10条
        LambdaQueryWrapper<Article> qw = new LambdaQueryWrapper<Article>()
                .select(Article::getTitle)
                .like(Article::getTitle, keyword)
                .eq(Article::getExamineStatus, ExamineStatusEnum.PASS.getCode())
                .eq(Article::getEditStatus, EditStatusEnum.PUBLISHED.getCode())
                .eq(Article::getVisibleRange, VisibleRangeEnum.ALL.getCode())
                .orderByDesc(Article::getUpdateTime)
                .last("LIMIT 10");

        List<Article> articles = articleMapper.selectList(qw);

        // 返回标题列表
        return articles.stream()
                .map(Article::getTitle)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getTagSuggestions(String keyword) {
        // 查询标签包含关键字的文章，只返回标签，最多返回相关标签
        LambdaQueryWrapper<Article> qw = new LambdaQueryWrapper<Article>()
                .select(Article::getTag)
                .like(Article::getTag, keyword)
                .eq(Article::getExamineStatus, ExamineStatusEnum.PASS.getCode())
                .eq(Article::getEditStatus, EditStatusEnum.PUBLISHED.getCode())
                .eq(Article::getVisibleRange, VisibleRangeEnum.ALL.getCode())
                .isNotNull(Article::getTag)
                .ne(Article::getTag, "")
                .last("LIMIT 50");

        List<Article> articles = articleMapper.selectList(qw);

        // 解析所有标签并去重，返回匹配关键字的标签
        return articles.stream()
                .map(Article::getTag)
                .filter(ObjectUtil::isNotEmpty)
                .flatMap(tagString -> {
                    // 标签是逗号分隔的字符串，需要拆分
                    String[] tags = tagString.split(",");
                    return java.util.Arrays.stream(tags);
                })
                .map(String::trim)
                .filter(tag -> tag.contains(keyword))
                .distinct()
                .limit(10)
                .collect(Collectors.toList());
    }

    @Override
    public void addArticle(ArticleDto articleDto) {
        Article article = BeanUtil.copyProperties(articleDto, Article.class);
        Integer userId = SecurityUtils.getUserId();
        article.setUserId(userId);
        if (!articleMapper.insertOrUpdate(article)) {
            throw new BlogException(BlogConstants.AddArticleError);
        }
        articleDto.setId(article.getId()); // 回显id
        articleDto.setUserId(userId);
        auditArticle(articleDto);
    }

    // 文章审核
    private void auditArticle(ArticleDto articleDto) {
        executorService.execute(() -> {

            MessageDto messageDto = new MessageDto();
            messageDto.setType(MessageTypeEnum.SYSTEM.getCode());
            // 先更新文章的审核状态
            Article updateArticle = new Article();
            updateArticle.setId(articleDto.getId());
            // 进行自动审核
            if (sidifensenConfig.isArticleAutoAudit()) {
                AuditResult auditResult = textAuditUtils
                        .auditTextWithDetailsSplit(articleDto.getTitle() + " " + articleDto.getContent());

                if (auditResult.getStatus().equals(ExamineStatusEnum.PASS.getCode())) {
                    // 文字审核通过，更新审核状态为通过
                    updateArticle.setExamineStatus(ExamineStatusEnum.PASS.getCode());
                    articleMapper.updateById(updateArticle);
                    log.info("文章审核通过，ID: {}", articleDto.getId());

                    // 文字审核通过，更新文章所属的专栏
                    if (ObjectUtil.isNotEmpty(articleDto.getColumnIds())) {
                        List<Integer> columnIds = articleDto.getColumnIds();
                        columnIds.forEach(columnId -> {
                            ArticleColumn articleColumn = articleColumnMapper
                                    .selectOne(new LambdaQueryWrapper<ArticleColumn>().select(ArticleColumn::getSort)
                                            .eq(ArticleColumn::getColumnId, columnId)
                                            .orderByDesc(ArticleColumn::getSort).last("limit 1"));
                            Integer sort = articleColumn == null ? 0 : articleColumn.getSort();
                            ArticleColumn newArticleColumn = new ArticleColumn();
                            newArticleColumn.setArticleId(articleDto.getId());
                            newArticleColumn.setColumnId(columnId);
                            newArticleColumn.setSort(sort + 1); // 最大排序值加1
                            articleColumnMapper.insert(newArticleColumn);

                            // 增加专栏的文章数量
                            Column column = columnMapper.selectById(columnId);
                            if (column != null) {
                                column.setArticleCount(column.getArticleCount() + 1);
                                columnMapper.updateById(column);
                            }
                        });
                    }
                } else if (auditResult.getStatus().equals(ExamineStatusEnum.NO_PASS.getCode())) {
                    // 文字审核不通过，更新审核状态为不通过，并记录原因，并发送消息给用户
                    updateArticle.setExamineStatus(ExamineStatusEnum.NO_PASS.getCode());
                    articleMapper.updateById(updateArticle);
                    log.error("文章审核不通过，ID: {}，标题: {}，原因: {}", articleDto.getId(), articleDto.getTitle(),
                            auditResult.getErrorMessage());

                    messageDto.setContent(MessageConstants.ArticleAuditNotPass(articleDto.getId(),
                            articleDto.getTitle(), auditResult.getErrorMessage()));
                    messageDto.setReceiverId(articleDto.getUserId());
                    messageService.sendToUser(messageDto);
                } else if (auditResult.getStatus().equals(ExamineStatusEnum.WAIT.getCode())) {
                    // 需要人工审核，更新审核状态为待审核，并记录原因，并发送消息给管理员
                    updateArticle.setExamineStatus(ExamineStatusEnum.WAIT.getCode());
                    articleMapper.updateById(updateArticle);
                    log.error("文章需要人工审核，ID: {}", articleDto.getId());

                    messageDto.setContent(MessageConstants.ArticleNeedReview(articleDto.getId(), articleDto.getTitle(),
                            auditResult.getErrorMessage()));
                    messageService.sendToAdmin(messageDto);

                    HashMap<String, Object> sendEmail = new HashMap<>();
                    sendEmail.put("text", String.format(MessageConstants.ARTICLE_NEED_REVIEW, articleDto.getId(),
                            auditResult.getErrorMessage()));
                    rabbitTemplate.convertAndSend(RabbitMQConstants.Examine_Exchange,
                            RabbitMQConstants.Examine_Routing_Key, sendEmail);
                }
            } else {
                updateArticle.setExamineStatus(ExamineStatusEnum.WAIT.getCode());
                articleMapper.updateById(updateArticle);
                // 没有开启自动审核, 需要人工审核，发送消息给管理员
                messageDto.setContent(
                        MessageConstants.ArticleNeedReview(articleDto.getId(), articleDto.getTitle(), null));
                messageService.sendToAdmin(messageDto);

                HashMap<String, Object> sendEmail = new HashMap<>();
                sendEmail.put("text", String.format(MessageConstants.ARTICLE_NEED_REVIEW, articleDto.getId(), null));
                rabbitTemplate.convertAndSend(RabbitMQConstants.Examine_Exchange, RabbitMQConstants.Examine_Routing_Key,
                        sendEmail);
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
        if (articleDto.getEditStatus().equals(EditStatusEnum.DRAFT.getCode())
                || articleDto.getEditStatus().equals(EditStatusEnum.RECYCLE.getCode())) {
            // 如果设置草稿箱和回收站,则删除专栏文章
            articleColumnMapper.delete(
                    new LambdaQueryWrapper<ArticleColumn>().in(ArticleColumn::getArticleId, articleDto.getId()));
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

        // 如果文章已审核通过，需要减少相关专栏的文章数量
        if (article.getExamineStatus().equals(ExamineStatusEnum.PASS.getCode())) {
            List<ArticleColumn> articleColumns = articleColumnMapper.selectList(
                    new LambdaQueryWrapper<ArticleColumn>().eq(ArticleColumn::getArticleId, articleId));

            // 减少每个专栏的文章数量
            articleColumns.forEach(articleColumn -> {
                Column column = columnMapper.selectById(articleColumn.getColumnId());
                if (column != null && column.getArticleCount() > 0) {
                    column.setArticleCount(column.getArticleCount() - 1);
                    columnMapper.updateById(column);
                }
            });
        }

        articleMapper.deleteById(articleId);
        articleColumnMapper.delete(new LambdaQueryWrapper<ArticleColumn>().in(ArticleColumn::getArticleId, articleId));

        // 清除文章的所有浏览记录
        redisComponent.clearArticleReads(articleId);
    }

    @Override
    public List<ArticleVo> adminGetArticleList() {
        LambdaQueryWrapper<Article> qw = new LambdaQueryWrapper<Article>()
                .orderByDesc(Article::getCreateTime);
        List<Article> articleList = articleMapper.selectList(qw);
        List<ArticleVo> articleVoList = BeanUtil.copyToList(articleList, ArticleVo.class);

        // 批量查询用户名并设置到文章中
        if (!articleVoList.isEmpty()) {
            // 收集所有不为空的用户ID
            List<Integer> userIds = articleVoList.stream()
                    .map(ArticleVo::getUserId)
                    .filter(ObjectUtil::isNotNull)
                    .distinct()
                    .toList();

            if (!userIds.isEmpty()) {
                // 批量查询用户信息
                List<SysUser> users = sysUserMapper.selectList(
                        new LambdaQueryWrapper<SysUser>().in(SysUser::getId, userIds));

                // 创建用户ID到用户昵称的映射
                Map<Integer, String> userIdToNicknameMap = users.stream()
                        .collect(Collectors.toMap(SysUser::getId, SysUser::getNickname));

                // 为每篇文章设置用户昵称
                articleVoList.forEach(articleVo -> {
                    if (articleVo.getUserId() != null) {
                        String nickname = userIdToNicknameMap.get(articleVo.getUserId());
                        articleVo.setNickname(nickname);
                    }
                });
            }
        }

        return articleVoList;
    }

    @Override
    public List<ArticleVo> adminGetArticlesByUserId(Integer userId) {
        // 构建查询条件：根据用户ID查询该用户的所有文章
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getUserId, userId)
                .orderByDesc(Article::getId);

        List<Article> articles = this.list(queryWrapper);
        List<ArticleVo> articleVos = BeanUtil.copyToList(articles, ArticleVo.class);

        if (!articleVos.isEmpty()) {
            // 获取文章对应的用户信息
            List<Integer> userIds = articleVos.stream()
                    .map(ArticleVo::getUserId)
                    .distinct()
                    .collect(Collectors.toList());

            if (!userIds.isEmpty()) {
                List<SysUser> users = sysUserMapper.selectList(
                        new LambdaQueryWrapper<SysUser>().in(SysUser::getId, userIds));
                Map<Integer, String> userMap = users.stream()
                        .collect(Collectors.toMap(SysUser::getId, SysUser::getNickname));

                // 设置用户昵称
                articleVos.forEach(articleVo -> {
                    String nickname = userMap.get(articleVo.getUserId());
                    if (nickname != null) {
                        articleVo.setNickname(nickname);
                    }
                });
            }

            // 获取专栏信息
            List<Integer> articleIds = articleVos.stream()
                    .map(ArticleVo::getId)
                    .collect(Collectors.toList());

            List<ArticleColumn> articleColumns = articleColumnMapper.selectList(
                    new LambdaQueryWrapper<ArticleColumn>().in(ArticleColumn::getArticleId, articleIds));

            if (!articleColumns.isEmpty()) {
                List<Integer> columnIds = articleColumns.stream()
                        .map(ArticleColumn::getColumnId)
                        .distinct()
                        .collect(Collectors.toList());

                List<Column> columns = columnMapper.selectList(
                        new LambdaQueryWrapper<Column>().in(Column::getId, columnIds));

                Map<Integer, ColumnVo> columnMap = columns.stream()
                        .collect(Collectors.toMap(Column::getId,
                                column -> BeanUtil.copyProperties(column, ColumnVo.class)));

                // 使用 groupingBy 来处理一个文章可能对应多个专栏的情况
                Map<Integer, List<Integer>> articleColumnMap = articleColumns.stream()
                        .collect(Collectors.groupingBy(
                                ArticleColumn::getArticleId,
                                Collectors.mapping(ArticleColumn::getColumnId, Collectors.toList())));

                // 设置专栏信息
                articleVos.forEach(articleVo -> {
                    List<Integer> articleColumnIds = articleColumnMap.get(articleVo.getId());
                    if (articleColumnIds != null && !articleColumnIds.isEmpty()) {
                        List<ColumnVo> columnList = articleColumnIds.stream()
                                .map(columnMap::get)
                                .filter(columnVo -> columnVo != null)
                                .collect(Collectors.toList());
                        if (!columnList.isEmpty()) {
                            articleVo.setColumns(columnList);
                        }
                    }
                });
            }
        }

        return articleVos;
    }

    @Override
    public ArticleVo adminGetArticle(Integer articleId) {
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            throw new BlogException(BlogConstants.NotFoundArticle);
        }
        ArticleVo articleVo = BeanUtil.copyProperties(article, ArticleVo.class);

        // 获取用户信息
        if (article.getUserId() != null) {
            SysUser user = sysUserMapper.selectById(article.getUserId());
            if (user != null) {
                articleVo.setNickname(user.getNickname());
            }
        }

        // 获取专栏信息
        List<ArticleColumn> articleColumns = articleColumnMapper
                .selectList(new LambdaQueryWrapper<ArticleColumn>().eq(ArticleColumn::getArticleId, article.getId()));
        if (ObjectUtil.isNotEmpty(articleColumns)) {
            List<Integer> columnIds = articleColumns.stream().map(ArticleColumn::getColumnId)
                    .collect(Collectors.toList());
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

    // 管理员搜索文章
    @Override
    public List<ArticleVo> adminSearchArticle(ArticleDto articleDto) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ObjectUtil.isNotEmpty(articleDto.getUserId()), Article::getUserId, articleDto.getUserId())
                .like(ObjectUtil.isNotEmpty(articleDto.getTitle()), Article::getTitle, articleDto.getTitle())
                .like(ObjectUtil.isNotEmpty(articleDto.getDescription()), Article::getDescription,
                        articleDto.getDescription())
                .like(ObjectUtil.isNotEmpty(articleDto.getTag()), Article::getTag, articleDto.getTag())
                .eq(ObjectUtil.isNotEmpty(articleDto.getExamineStatus()), Article::getExamineStatus,
                        articleDto.getExamineStatus())
                .eq(ObjectUtil.isNotEmpty(articleDto.getEditStatus()), Article::getEditStatus,
                        articleDto.getEditStatus())
                .eq(ObjectUtil.isNotEmpty(articleDto.getVisibleRange()), Article::getVisibleRange,
                        articleDto.getVisibleRange())
                .eq(ObjectUtil.isNotEmpty(articleDto.getReprintType()), Article::getReprintType,
                        articleDto.getReprintType())
                .ge(ObjectUtil.isNotEmpty(articleDto.getCreateTimeStart()), Article::getCreateTime,
                        articleDto.getCreateTimeStart())
                .le(ObjectUtil.isNotEmpty(articleDto.getCreateTimeEnd()), Article::getCreateTime,
                        articleDto.getCreateTimeEnd())
                .orderByDesc(Article::getCreateTime);

        List<Article> articles = this.list(queryWrapper);
        List<ArticleVo> articleVos = BeanUtil.copyToList(articles, ArticleVo.class);

        // 用userId把nickname查询出来
        HashMap<Integer, String> userMap = new HashMap<>();
        // 查询所有用户信息
        List<SysUser> users = sysUserMapper.selectList(null);
        for (SysUser user : users) {
            userMap.put(user.getId(), user.getNickname());
        }
        // 将用户昵称设置到ArticleVo中
        for (ArticleVo articleVo : articleVos) {
            articleVo.setNickname(userMap.get(articleVo.getUserId()));
        }

        return articleVos;
    }

    @Override
    public void adminExamineArticle(ArticleAuditDto articleAuditDto) {

        Article article = articleMapper.selectOne(
                new LambdaQueryWrapper<Article>()
                        .select(Article::getUserId, Article::getTitle)
                        .eq(Article::getId, articleAuditDto.getArticleId()));

        if (ObjectUtil.isEmpty(article)) {
            throw new BlogException(BlogConstants.NotFoundArticle);
        }
        Integer examineStatus = articleAuditDto.getExamineStatus();
        String examineReason = articleAuditDto.getExamineReason();

        // 更新文章审核状态
        Article updateArticle = new Article();
        updateArticle.setId(articleAuditDto.getArticleId());
        updateArticle.setExamineStatus(examineStatus);
        articleMapper.updateById(updateArticle);

        // 发送消息给用户
        MessageDto messageDto = new MessageDto();
        messageDto.setReceiverId(article.getUserId());

        // 根据审核状态发送不同的消息
        if (examineStatus.equals(ExamineStatusEnum.PASS.getCode())) {
            // 审核通过
            messageDto
                    .setContent(MessageConstants.ArticleAuditPass(articleAuditDto.getArticleId(), article.getTitle()));
        } else if (examineStatus.equals(ExamineStatusEnum.NO_PASS.getCode())) {
            // 审核不通过
            messageDto.setContent(MessageConstants.ArticleAuditNotPass(articleAuditDto.getArticleId(),
                    article.getTitle(), examineReason));
        }

        messageService.sendToUser(messageDto);
    }

    @Override
    public void adminExamineBatchArticle(List<ArticleAuditDto> articleAuditDtos) {
        if (ObjectUtil.isEmpty(articleAuditDtos)) {
            return;
        }

        // 提取所有文章ID
        List<Integer> articleIds = articleAuditDtos.stream()
                .map(ArticleAuditDto::getArticleId)
                .toList();

        // 批量查询文章信息（只查询需要的字段）
        List<Article> articles = articleMapper.selectList(new LambdaQueryWrapper<Article>()
                .select(Article::getId, Article::getUserId, Article::getTitle)
                .in(Article::getId, articleIds));

        if (articles.size() != articleIds.size()) {
            throw new BlogException(BlogConstants.NotFoundArticle);
        }

        // 创建文章ID到文章的映射
        Map<Integer, Article> articleMap = articles.stream()
                .collect(Collectors.toMap(Article::getId, article -> article));

        // 准备批量更新的数据
        List<Article> updateArticles = new ArrayList<>();
        List<MessageDto> messages = new ArrayList<>();

        for (ArticleAuditDto auditDto : articleAuditDtos) {
            Article article = articleMap.get(auditDto.getArticleId());
            Integer examineStatus = auditDto.getExamineStatus();
            String examineReason = auditDto.getExamineReason();

            // 准备更新的文章对象
            Article updateArticle = new Article();
            updateArticle.setId(auditDto.getArticleId());
            updateArticle.setExamineStatus(examineStatus);
            updateArticles.add(updateArticle);

            // 准备消息通知
            String messageContent;
            if (examineStatus.equals(ExamineStatusEnum.PASS.getCode())) {
                messageContent = MessageConstants.ArticleAuditPass(auditDto.getArticleId(), article.getTitle());
            } else if (examineStatus.equals(ExamineStatusEnum.NO_PASS.getCode())) {
                messageContent = MessageConstants.ArticleAuditNotPass(auditDto.getArticleId(), article.getTitle(),
                        examineReason);
            } else {
                continue; // 跳过无效状态
            }

            MessageDto messageDto = new MessageDto();
            messageDto.setContent(messageContent);
            messageDto.setReceiverId(article.getUserId());
            messages.add(messageDto);
        }

        // 批量更新文章状态
        this.updateBatchById(updateArticles);

        // 批量发送所有消息
        if (!messages.isEmpty()) {
            messageService.sendBatchToUsers(messages);
        }
    }

    @Override
    public void adminDeleteArticle(Integer articleId) {
        // 获取文章信息
        Article article = articleMapper.selectById(articleId);

        // 如果文章已审核通过，需要减少相关专栏的文章数量
        if (article != null && article.getExamineStatus().equals(ExamineStatusEnum.PASS.getCode())) {
            List<ArticleColumn> articleColumns = articleColumnMapper.selectList(
                    new LambdaQueryWrapper<ArticleColumn>().eq(ArticleColumn::getArticleId, articleId));

            // 减少每个专栏的文章数量
            articleColumns.forEach(articleColumn -> {
                Column column = columnMapper.selectById(articleColumn.getColumnId());
                if (column != null && column.getArticleCount() > 0) {
                    column.setArticleCount(column.getArticleCount() - 1);
                    columnMapper.updateById(column);
                }
            });
        }

        if (articleMapper.deleteById(articleId) < 1) {
            throw new BlogException(BlogConstants.DeleteArticleError);
        }

        // 删除文章与专栏的关联关系
        articleColumnMapper.delete(new LambdaQueryWrapper<ArticleColumn>().eq(ArticleColumn::getArticleId, articleId));

        // 清除文章的所有浏览记录
        redisComponent.clearArticleReads(articleId);
    }

    @Override
    public void adminDeleteBatchArticle(List<Integer> articleIds) {
        // 批量处理专栏文章数量
        articleIds.forEach(articleId -> {
            Article article = articleMapper.selectById(articleId);

            // 如果文章已审核通过，需要减少相关专栏的文章数量
            if (article != null && article.getExamineStatus().equals(ExamineStatusEnum.PASS.getCode())) {
                List<ArticleColumn> articleColumns = articleColumnMapper.selectList(
                        new LambdaQueryWrapper<ArticleColumn>().eq(ArticleColumn::getArticleId, articleId));

                // 减少每个专栏的文章数量
                articleColumns.forEach(articleColumn -> {
                    Column column = columnMapper.selectById(articleColumn.getColumnId());
                    if (column != null && column.getArticleCount() > 0) {
                        column.setArticleCount(column.getArticleCount() - 1);
                        columnMapper.updateById(column);
                    }
                });
            }
        });

        // 批量删除文章
        this.removeByIds(articleIds);

        // 删除文章与专栏的关联关系
        articleColumnMapper.delete(new LambdaQueryWrapper<ArticleColumn>().in(ArticleColumn::getArticleId, articleIds));

        // 批量清除文章的所有浏览记录
        redisComponent.batchClearArticleReads(articleIds);
    }

    @Override
    public ArticleStatisticsVo getAdminStatistics() {
        // 查询所有文章总数
        Long totalCount = this.count();

        // 查询已发布数量 (editStatus=0 && examineStatus=1)
        Long publishedCount = this.count(new LambdaQueryWrapper<Article>()
                .eq(Article::getEditStatus, 0)
                .eq(Article::getExamineStatus, 1));

        // 查询审核中数量 (editStatus=0 && examineStatus=0)
        Long reviewingCount = this.count(new LambdaQueryWrapper<Article>()
                .eq(Article::getEditStatus, 0)
                .eq(Article::getExamineStatus, 0));

        // 查询草稿箱数量 (editStatus=1)
        Long draftCount = this.count(new LambdaQueryWrapper<Article>()
                .eq(Article::getEditStatus, 1));

        // 查询回收站数量 (editStatus=2)
        Long garbageCount = this.count(new LambdaQueryWrapper<Article>()
                .eq(Article::getEditStatus, 2));

        ArticleStatisticsVo statisticsVo = new ArticleStatisticsVo();
        statisticsVo.setTotalCount(totalCount);
        statisticsVo.setPublishedCount(publishedCount);
        statisticsVo.setReviewingCount(reviewingCount);
        statisticsVo.setDraftCount(draftCount);
        statisticsVo.setGarbageCount(garbageCount);

        return statisticsVo;
    }

    @Override
    public CreationStatisticsVo getCreationStatistics() {
        Integer userId = SecurityUtils.getUserId();

        // 获取文章统计信息
        ArticleStatisticsVo articleStatistics = getUserArticleStatistics();

        // 获取专栏数量
        Long columnCount = columnMapper.selectCount(new LambdaQueryWrapper<Column>()
                .eq(Column::getUserId, userId));

        // 获取评论数量
        Long commentCount = 0L;
        try {
            commentCount = commentService.count(
                    new LambdaQueryWrapper<Comment>()
                            .eq(Comment::getUserId, userId));
        } catch (Exception e) {
            // 如果Comment实体类或方法不存在，设为0
            commentCount = 0L;
        }

        // 获取总阅读量
        Long totalReadCount = articleMapper.selectList(
                new LambdaQueryWrapper<Article>()
                        .eq(Article::getUserId, userId)
                        .eq(Article::getExamineStatus, ExamineStatusEnum.PASS.getCode()))
                .stream().mapToLong(Article::getReadCount).sum();

        // 获取总点赞数 - 暂时设为0，后续可以实现
        Long totalLikeCount = 0L;

        // 获取用户信息（粉丝数、关注数）
        SysUser user = sysUserMapper.selectById(userId);
        Long fansCount = user.getFansCount().longValue();
        Long followCount = user.getFollowCount().longValue();

        // 构建返回对象
        CreationStatisticsVo creationStatistics = new CreationStatisticsVo();
        creationStatistics.setArticleStatistics(articleStatistics);
        creationStatistics.setColumnCount(columnCount);
        creationStatistics.setCommentCount(commentCount);
        creationStatistics.setTotalReadCount(totalReadCount);
        creationStatistics.setTotalLikeCount(totalLikeCount);
        creationStatistics.setFansCount(fansCount);
        creationStatistics.setFollowCount(followCount);

        return creationStatistics;
    }

    @Override
    public PageVo<List<HotArticleVo>> getHotArticleList(Integer pageNum, Integer pageSize) {
        try {
            // 1. 从Redis获取热门文章ID列表（按访问量倒序）
            // 计算要获取的数据范围，确保有足够的文章用于分页
            int totalNeeded = pageNum * pageSize;
            List<Integer> hotArticleIds = redisComponent.getHotArticles(totalNeeded);

            // 如果Redis中没有数据，返回空结果
            if (ObjectUtil.isEmpty(hotArticleIds)) {
                log.error("Redis中暂无热门文章数据");
                return new PageVo<>(new ArrayList<>(), 0L);
            }

            // 2. 计算分页范围
            int startIndex = (pageNum - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, hotArticleIds.size());

            // 如果起始索引超出范围，返回空结果
            if (startIndex >= hotArticleIds.size()) {
                return new PageVo<>(new ArrayList<>(), (long) hotArticleIds.size());
            }

            // 3. 获取当前页的文章ID列表
            List<Integer> currentPageIds = hotArticleIds.subList(startIndex, endIndex);

            // 4. 根据文章ID列表查询热门文章核心信息（使用 MyBatis-Plus，精简查询，只查必要字段）
            List<Article> articles = articleMapper.selectList(
                    new LambdaQueryWrapper<Article>()
                            // 只查询需要的字段，避免查询大字段（如 content），提升性能
                            .select(Article::getId, Article::getUserId, Article::getTitle, Article::getReadCount)
                            // 根据ID列表查询
                            .in(Article::getId, currentPageIds)
                            // 审核状态为通过
                            .eq(Article::getExamineStatus, ExamineStatusEnum.PASS.getCode())
                            // 编辑状态为已发布
                            .eq(Article::getEditStatus, 0)
                            // 可见范围为公开
                            .eq(Article::getVisibleRange, 0)
                            // 未删除
                            .eq(Article::getIsDeleted, 0));

            // 如果查询结果为空，返回空结果
            if (ObjectUtil.isEmpty(articles)) {
                return new PageVo<>(new ArrayList<>(), (long) hotArticleIds.size());
            }

            // 5. 转换为Map，方便按ID查找
            Map<Integer, Article> articleDataMap = articles.stream()
                    .collect(Collectors.toMap(Article::getId, article -> article));

            // 6. 批量获取热度分数（优化性能：避免循环中多次查询Redis）
            Map<Integer, Double> hotScoreMap = redisComponent.batchGetArticleHotScore(currentPageIds);

            // 7. 按照Redis中的顺序组装结果（保持热度排序）
            List<HotArticleVo> hotArticleVos = new ArrayList<>();
            for (Integer articleId : currentPageIds) {
                Article article = articleDataMap.get(articleId);
                if (article == null) {
                    continue;
                }

                // 创建 HotArticleVo（只包含核心字段：ID、用户ID、标题、阅读量、热度）
                HotArticleVo hotArticleVo = new HotArticleVo();
                hotArticleVo.setId(article.getId());
                hotArticleVo.setUserId(article.getUserId());
                hotArticleVo.setTitle(article.getTitle());
                hotArticleVo.setReadCount(article.getReadCount());

                // 添加热度分数（从批量获取的Map中取值，如果没有则设为0）
                Double hotScore = hotScoreMap.get(articleId);
                hotArticleVo.setHotScore(hotScore != null ? hotScore.longValue() : 0L);

                hotArticleVos.add(hotArticleVo);
            }

            // 8. 返回分页结果
            return new PageVo<>(hotArticleVos, (long) hotArticleIds.size());

        } catch (Exception e) {
            log.error("获取热门文章列表失败: {}", e.getMessage(), e);
            throw new BlogException(BlogConstants.GetHotArticleListError);
        }
    }

}
