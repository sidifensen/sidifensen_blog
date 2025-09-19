package com.sidifensen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.config.SidifensenConfig;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.constants.MessageConstants;
import com.sidifensen.domain.constants.RabbitMQConstants;
import com.sidifensen.domain.dto.CommentAuditDto;
import com.sidifensen.domain.dto.CommentDto;
import com.sidifensen.domain.dto.CommentSearchDto;
import com.sidifensen.domain.dto.MessageDto;
import com.sidifensen.domain.entity.Article;
import com.sidifensen.domain.entity.Comment;
import com.sidifensen.domain.entity.Like;
import com.sidifensen.domain.entity.SysUser;
import com.sidifensen.domain.enums.ExamineStatusEnum;
import com.sidifensen.domain.enums.MessageTypeEnum;
import com.sidifensen.domain.result.AuditResult;
import com.sidifensen.domain.vo.AdminCommentVo;
import com.sidifensen.domain.vo.CommentVo;
import com.sidifensen.domain.vo.PageVo;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.ArticleMapper;
import com.sidifensen.mapper.CommentMapper;
import com.sidifensen.mapper.LikeMapper;
import com.sidifensen.mapper.SysUserMapper;
import com.sidifensen.service.CommentService;
import com.sidifensen.service.MessageService;
import com.sidifensen.utils.SecurityUtils;
import com.sidifensen.utils.TextAuditUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 评论服务实现类
 *
 * @author sidifensen
 * @since 2025-09-15
 */
@Service
@Slf4j
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private LikeMapper likeMapper;

    @Resource
    private TextAuditUtils textAuditUtils;

    @Resource
    private SidifensenConfig sidifensenConfig;

    @Resource
    private MessageService messageService;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Override
    @Transactional
    public Integer addComment(CommentDto commentDto) {
        Integer currentUserId = SecurityUtils.getUserId();

        // 验证父评论是否存在
        if (commentDto.getParentId() != null && commentDto.getParentId() > 0) {
            Comment parentComment = getById(commentDto.getParentId());
            if (parentComment == null || parentComment.getIsDeleted() == 1) {
                throw new BlogException(BlogConstants.NotFoundParentComment);
            }

            // 如果父评论的审核状态不是通过，不允许回复
            if (parentComment.getExamineStatus() != 1) {
                throw new BlogException(BlogConstants.CannotReplyUnApprovedComment);
            }
        }

        // 创建评论实体
        Comment comment = new Comment();
        BeanUtil.copyProperties(commentDto, comment);
        comment.setUserId(currentUserId);
        // 先设置为待审核状态
        comment.setExamineStatus(ExamineStatusEnum.WAIT.getCode());

        // 保存评论
        boolean saved = save(comment);
        if (!saved) {
            throw new BlogException(BlogConstants.AddCommentError);
        }

        // 保存成功后进行文字内容审核
        auditComment(comment, commentDto.getContent());

        // 如果是回复评论，更新父评论的回复数
        if (commentDto.getParentId() != null && commentDto.getParentId() > 0) {
            LambdaUpdateWrapper<Comment> replyUpdateWrapper = new LambdaUpdateWrapper<>();
            replyUpdateWrapper.eq(Comment::getId, commentDto.getParentId())
                    .setIncrBy(Comment::getReplyCount, 1);
            commentMapper.update(null, replyUpdateWrapper);
        }

        // 更新文章评论数（所有评论都增加文章评论数）
        LambdaUpdateWrapper<Article> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Article::getId, commentDto.getArticleId())
                .setIncrBy(Article::getCommentCount, 1);
        boolean updateResult = articleMapper.update(null, updateWrapper) > 0;
        if (!updateResult) {
            log.warn("更新文章评论数失败，文章ID：{}", commentDto.getArticleId());
        }

        return comment.getId();
    }

    /**
     * 评论审核
     *
     * @param comment 评论实体
     * @param content 评论内容
     */
    private void auditComment(Comment comment, String content) {
        Integer commentId = comment.getId();
        Integer userId = comment.getUserId();

        MessageDto messageDto = new MessageDto();
        messageDto.setType(MessageTypeEnum.SYSTEM.getCode());

        // 进行文字内容审核
        if (sidifensenConfig.isCommentAutoAudit()) {
            try {
                log.info("开始对评论进行文字审核，评论ID: {}, 用户ID: {}, 评论内容长度: {}", commentId, userId, content.length());
                AuditResult auditResult = textAuditUtils.auditTextWithDetailsSplit(content);

                Comment updateComment = new Comment();
                updateComment.setId(commentId);

                if (auditResult.getStatus().equals(ExamineStatusEnum.PASS.getCode())) {
                    // 审核通过
                    updateComment.setExamineStatus(ExamineStatusEnum.PASS.getCode());
                    updateById(updateComment);
                    log.info("评论文字审核通过，评论ID: {}, 用户ID: {}", commentId, userId);

                } else if (auditResult.getStatus().equals(ExamineStatusEnum.NO_PASS.getCode())) {
                    // 审核不通过，发送消息给用户
                    updateComment.setExamineStatus(ExamineStatusEnum.NO_PASS.getCode());
                    updateById(updateComment);
                    log.warn("评论文字审核不通过，评论ID: {}, 用户ID: {}, 原因: {}", commentId, userId, auditResult.getErrorMessage());

                    messageDto
                            .setContent(MessageConstants.CommentAuditNotPass(commentId, auditResult.getErrorMessage()));
                    messageDto.setReceiverId(userId);
                    messageService.sendToUser(messageDto);

                } else {
                    // 需要人工审核，发送消息给管理员并发送邮件
                    updateComment.setExamineStatus(ExamineStatusEnum.WAIT.getCode());
                    updateById(updateComment);
                    log.info("评论需要人工审核，评论ID: {}, 用户ID: {}, 原因: {}", commentId, userId, auditResult.getErrorMessage());

                    // 发送消息给管理员
                    messageDto.setContent(MessageConstants.CommentNeedReview(commentId, auditResult.getErrorMessage()));
                    messageService.sendToAdmin(messageDto);

                    // 发送邮件给管理员
                    HashMap<String, Object> sendEmail = new HashMap<>();
                    sendEmail.put("text", String.format(MessageConstants.COMMENT_NEED_REVIEW, commentId,
                            auditResult.getErrorMessage()));
                    rabbitTemplate.convertAndSend(RabbitMQConstants.Examine_Exchange,
                            RabbitMQConstants.Examine_Routing_Key, sendEmail);
                }
            } catch (Exception e) {
                log.error("评论文字审核过程中发生异常，评论ID: {}, 用户ID: {}, 错误信息: {}", commentId, userId, e.getMessage(), e);
                // 审核异常时，保持待审核状态，发送消息给管理员
                messageDto.setContent(MessageConstants.CommentNeedReview(commentId, "审核服务异常，需要人工审核"));
                messageService.sendToAdmin(messageDto);

                // 发送邮件给管理员
                HashMap<String, Object> sendEmail = new HashMap<>();
                sendEmail.put("text", String.format(MessageConstants.COMMENT_NEED_REVIEW, commentId, "审核服务异常，需要人工审核"));
                rabbitTemplate.convertAndSend(RabbitMQConstants.Examine_Exchange,
                        RabbitMQConstants.Examine_Routing_Key, sendEmail);
            }
        } else {
            // 如果没有开启自动审核，需要人工审核，发送消息给管理员
            log.info("评论自动审核功能未开启，需要人工审核，评论ID: {}, 用户ID: {}", commentId, userId);
            messageDto.setContent(MessageConstants.CommentNeedReview(commentId, "未开启自动审核"));
            messageService.sendToAdmin(messageDto);

            // 发送邮件给管理员
            HashMap<String, Object> sendEmail = new HashMap<>();
            sendEmail.put("text", String.format(MessageConstants.COMMENT_NEED_REVIEW, commentId, "未开启自动审核"));
            rabbitTemplate.convertAndSend(RabbitMQConstants.Examine_Exchange,
                    RabbitMQConstants.Examine_Routing_Key, sendEmail);
        }
    }

    @Override
    @Transactional
    public void deleteComment(Integer commentId) {
        Integer currentUserId = SecurityUtils.getUserId();

        Comment comment = getById(commentId);
        if (comment == null || comment.getIsDeleted() == 1) {
            throw new BlogException(BlogConstants.NotFoundComment);
        }

        // 只能删除自己的评论
        if (!comment.getUserId().equals(currentUserId)) {
            throw new BlogException(BlogConstants.CannotDeleteOthersComment);
        }

        // 统计要删除的评论总数（包括所有子评论）
        int deletedCommentCount = countCommentAndReplies(commentId);

        // 递归删除评论及其所有子评论
        deleteCommentAndReplies(commentId);

        // 如果是回复评论，更新父评论的回复数（只减少当前评论，不包括子评论）
        if (comment.getParentId() != null && comment.getParentId() > 0) {
            LambdaUpdateWrapper<Comment> replyUpdateWrapper = new LambdaUpdateWrapper<>();
            replyUpdateWrapper.eq(Comment::getId, comment.getParentId())
                    .setIncrBy(Comment::getReplyCount, -1);
            commentMapper.update(null, replyUpdateWrapper);
        }

        // 更新文章评论数（减少所有被删除的评论数量）
        LambdaUpdateWrapper<Article> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Article::getId, comment.getArticleId())
                .setIncrBy(Article::getCommentCount, -deletedCommentCount);
        boolean updateResult = articleMapper.update(null, updateWrapper) > 0;
        if (!updateResult) {
            log.warn("更新文章评论数失败，文章ID：{}", comment.getArticleId());
        }

        log.info("用户删除评论及其所有子评论，评论ID：{}，删除数量：{}", commentId, deletedCommentCount);
    }

    @Override
    public PageVo<List<CommentVo>> getCommentList(Integer articleId, Integer pageNum, Integer pageSize) {
        if (articleId == null || articleId <= 0) {
            throw new BlogException(BlogConstants.ArticleIdRequired);
        }

        Integer offset = (pageNum - 1) * pageSize;
        Integer currentUserId = SecurityUtils.getUserId() == 0 ? null : SecurityUtils.getUserId();

        // 1. 查询顶级评论基础信息
        LambdaQueryWrapper<Comment> topCommentWrapper = new LambdaQueryWrapper<Comment>()
                .eq(Comment::getArticleId, articleId)
                .eq(Comment::getParentId, 0)
                .eq(Comment::getExamineStatus, 1)
                .eq(Comment::getIsDeleted, 0)
                .orderByDesc(Comment::getCreateTime)
                .last("LIMIT " + offset + ", " + pageSize);

        List<Comment> topComments = list(topCommentWrapper);
        if (topComments.isEmpty()) {
            return new PageVo<>(new ArrayList<>(), 0L);
        }

        Set<Integer> topCommentIds = topComments.stream().map(Comment::getId).collect(Collectors.toSet());

        // 2. 批量查询子评论（每个顶级评论最多2条）
        List<Comment> allReplies = batchQueryReplies(articleId, topCommentIds, 2);

        // 3. 批量构建CommentVo
        List<CommentVo> result = batchBuildCommentVos(topComments, allReplies, currentUserId, true);

        // 4. 查询总数
        Long total = count(new LambdaQueryWrapper<Comment>()
                .eq(Comment::getArticleId, articleId)
                .eq(Comment::getParentId, 0)
                .eq(Comment::getExamineStatus, 1)
                .eq(Comment::getIsDeleted, 0));

        return new PageVo<>(result, total);
    }

    @Override
    public PageVo<List<CommentVo>> getReplyList(Integer commentId, Integer pageNum, Integer pageSize) {
        if (commentId == null || commentId <= 0) {
            throw new BlogException(BlogConstants.CommentIdRequired);
        }

        Comment parentComment = getById(commentId);
        if (parentComment == null || parentComment.getIsDeleted() == 1) {
            throw new BlogException(BlogConstants.NotFoundParentComment);
        }

        Integer offset = (pageNum - 1) * pageSize;
        Integer currentUserId = SecurityUtils.getUserId() == 0 ? null : SecurityUtils.getUserId();

        // 1. 查询回复评论基础信息
        LambdaQueryWrapper<Comment> replyWrapper = new LambdaQueryWrapper<Comment>()
                .eq(Comment::getArticleId, parentComment.getArticleId())
                .eq(Comment::getParentId, commentId)
                .eq(Comment::getExamineStatus, 1)
                .eq(Comment::getIsDeleted, 0)
                .orderByDesc(Comment::getCreateTime)
                .last("LIMIT " + offset + ", " + pageSize);

        List<Comment> replies = list(replyWrapper);
        if (replies.isEmpty()) {
            return new PageVo<>(new ArrayList<>(), 0L);
        }

        // 2. 批量构建CommentVo（回复列表不需要子评论）
        List<CommentVo> result = batchBuildCommentVos(replies, new ArrayList<>(), currentUserId, false);

        // 3. 查询总数
        Long total = count(new LambdaQueryWrapper<Comment>()
                .eq(Comment::getArticleId, parentComment.getArticleId())
                .eq(Comment::getParentId, commentId)
                .eq(Comment::getExamineStatus, 1)
                .eq(Comment::getIsDeleted, 0));

        return new PageVo<>(result, total);
    }

    /**
     * 批量查询子评论
     *
     * @param articleId      文章ID
     * @param parentIds      父评论ID集合
     * @param limitPerParent 每个父评论最多查询的子评论数量
     * @return 子评论列表
     */
    private List<Comment> batchQueryReplies(Integer articleId, Set<Integer> parentIds, int limitPerParent) {
        if (parentIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 查询所有子评论
        LambdaQueryWrapper<Comment> replyWrapper = new LambdaQueryWrapper<Comment>()
                .eq(Comment::getArticleId, articleId)
                .in(Comment::getParentId, parentIds)
                .eq(Comment::getExamineStatus, 1)
                .eq(Comment::getIsDeleted, 0)
                .orderByDesc(Comment::getCreateTime);

        List<Comment> allReplies = list(replyWrapper);

        // 按父评论分组并限制数量
        Map<Integer, List<Comment>> replyMap = allReplies.stream()
                .collect(Collectors.groupingBy(Comment::getParentId));

        List<Comment> limitedReplies = new ArrayList<>();
        replyMap.forEach((parentId, replies) -> {
            limitedReplies.addAll(replies.stream().limit(limitPerParent).collect(Collectors.toList()));
        });

        return limitedReplies;
    }

    /**
     * 批量构建CommentVo对象
     *
     * @param mainComments  主要评论列表（顶级评论或回复评论）
     * @param subComments   子评论列表
     * @param currentUserId 当前用户ID
     * @param withChildren  是否包含子评论
     * @return CommentVo列表
     */
    private List<CommentVo> batchBuildCommentVos(List<Comment> mainComments, List<Comment> subComments,
                                                 Integer currentUserId, boolean withChildren) {
        if (mainComments.isEmpty()) {
            return new ArrayList<>();
        }

        // 收集所有需要的用户ID
        Set<Integer> allUserIds = new HashSet<>();
        Set<Integer> allCommentIds = new HashSet<>();

        // 收集主要评论的用户ID和评论ID
        mainComments.forEach(comment -> {
            allUserIds.add(comment.getUserId());
            allCommentIds.add(comment.getId());
            if (comment.getReplyUserId() != null) {
                allUserIds.add(comment.getReplyUserId());
            }
        });

        // 收集子评论的用户ID和评论ID
        subComments.forEach(comment -> {
            allUserIds.add(comment.getUserId());
            allCommentIds.add(comment.getId());
            if (comment.getReplyUserId() != null) {
                allUserIds.add(comment.getReplyUserId());
            }
        });

        // 批量查询用户信息
        final Map<Integer, SysUser> userMap;
        if (!allUserIds.isEmpty()) {
            LambdaQueryWrapper<SysUser> userWrapper = new LambdaQueryWrapper<SysUser>()
                    .in(SysUser::getId, allUserIds)
                    .select(SysUser::getId, SysUser::getNickname, SysUser::getAvatar);

            List<SysUser> users = sysUserMapper.selectList(userWrapper);
            userMap = users.stream().collect(Collectors.toMap(SysUser::getId, u -> u));
        } else {
            userMap = new HashMap<>();
        }

        // 批量查询点赞统计
        final Map<Integer, Long> likeCountMap;
        if (!allCommentIds.isEmpty()) {
            LambdaQueryWrapper<Like> likeCountWrapper = new LambdaQueryWrapper<Like>()
                    .eq(Like::getType, 1)
                    .in(Like::getTypeId, allCommentIds)
                    .select(Like::getTypeId);

            List<Like> likes = likeMapper.selectList(likeCountWrapper);
            likeCountMap = likes.stream()
                    .collect(Collectors.groupingBy(Like::getTypeId, Collectors.counting()));
        } else {
            likeCountMap = new HashMap<>();
        }

        // 查询当前用户点赞状态
        final Set<Integer> userLikedIds;
        if (currentUserId != null && !allCommentIds.isEmpty()) {
            LambdaQueryWrapper<Like> userLikeWrapper = new LambdaQueryWrapper<Like>()
                    .eq(Like::getType, 1)
                    .eq(Like::getUserId, currentUserId)
                    .in(Like::getTypeId, allCommentIds)
                    .select(Like::getTypeId);

            List<Like> userLikes = likeMapper.selectList(userLikeWrapper);
            userLikedIds = userLikes.stream().map(Like::getTypeId).collect(Collectors.toSet());
        } else {
            userLikedIds = new HashSet<>();
        }

        // 构建结果
        if (withChildren && !subComments.isEmpty()) {
            // 构建子评论映射
            Map<Integer, List<CommentVo>> replyMap = subComments.stream()
                    .map(reply -> buildCommentVo(reply, userMap, likeCountMap, userLikedIds))
                    .collect(Collectors.groupingBy(CommentVo::getParentId));

            // 构建主评论并设置子评论
            return mainComments.stream()
                    .map(comment -> {
                        CommentVo vo = buildCommentVo(comment, userMap, likeCountMap, userLikedIds);
                        vo.setChildren(replyMap.getOrDefault(comment.getId(), new ArrayList<>()));
                        return vo;
                    })
                    .collect(Collectors.toList());
        } else {
            // 只构建主评论
            return mainComments.stream()
                    .map(comment -> buildCommentVo(comment, userMap, likeCountMap, userLikedIds))
                    .collect(Collectors.toList());
        }
    }

    /**
     * 构建评论VO对象
     *
     * @param comment      评论实体
     * @param userMap      用户信息映射
     * @param likeCountMap 点赞数映射
     * @param userLikedIds 当前用户点赞的评论ID集合
     * @return 评论VO
     */
    private CommentVo buildCommentVo(Comment comment, Map<Integer, SysUser> userMap,
                                     Map<Integer, Long> likeCountMap, Set<Integer> userLikedIds) {
        CommentVo vo = BeanUtil.copyProperties(comment, CommentVo.class);

        // 设置用户信息
        SysUser user = userMap.get(comment.getUserId());
        if (user != null) {
            vo.setNickname(user.getNickname());
            vo.setAvatar(user.getAvatar());
        }

        // 设置回复用户信息
        if (comment.getReplyUserId() != null) {
            SysUser replyUser = userMap.get(comment.getReplyUserId());
            if (replyUser != null) {
                vo.setReplyUserNickname(replyUser.getNickname());
            }
        }

        // 设置点赞信息
        vo.setLikeCount(Math.toIntExact(likeCountMap.getOrDefault(comment.getId(), 0L)));
        // 设置当前用户是否点赞
        vo.setIsLiked(userLikedIds.contains(comment.getId()));

        return vo;
    }

    @Override
    public List<AdminCommentVo> adminGetCommentList() {
        // 构建查询条件 - 获取所有未删除的评论
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<Comment>()
                .orderByDesc(Comment::getCreateTime);

        List<Comment> comments = list(queryWrapper);

        if (comments.isEmpty()) {
            return new ArrayList<>();
        }

        // 批量构建AdminCommentVo
        return batchBuildAdminCommentVos(comments);
    }


    @Override
    public List<AdminCommentVo> adminGetCommentsByUserId(Integer userId) {
        if (userId == null || userId <= 0) {
            throw new BlogException(BlogConstants.UserIdRequired);
        }

        // 验证用户是否存在
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BlogException(BlogConstants.NotFoundUser);
        }

        // 查询该用户的所有评论（包括已删除的）
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<Comment>()
                .eq(Comment::getUserId, userId)
                .eq(Comment::getIsDeleted, 0)
                .orderByDesc(Comment::getCreateTime);

        List<Comment> comments = list(queryWrapper);

        if (comments.isEmpty()) {
            return new ArrayList<>();
        }

        // 批量构建AdminCommentVo
        return batchBuildAdminCommentVos(comments);
    }

    @Override
    public List<AdminCommentVo> adminSearchComment(CommentSearchDto commentSearchDto) {
        // 构建查询条件
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<Comment>()
                .eq(Comment::getIsDeleted, 0)
                .orderByDesc(Comment::getCreateTime);

        queryWrapper
                // 根据审核状态筛选
                .eq(commentSearchDto.getExamineStatus() != null, Comment::getExamineStatus, commentSearchDto.getExamineStatus())
                // 根据用户ID筛选
                .eq(commentSearchDto.getUserId() != null && commentSearchDto.getUserId() > 0, Comment::getUserId, commentSearchDto.getUserId())
                // 根据文章ID筛选
                .eq(commentSearchDto.getArticleId() != null && commentSearchDto.getArticleId() > 0, Comment::getArticleId, commentSearchDto.getArticleId())
                // 根据关键词搜索评论内容
                .like(commentSearchDto.getKeyword() != null && !commentSearchDto.getKeyword().trim().isEmpty(), Comment::getContent, commentSearchDto.getKeyword() != null ? commentSearchDto.getKeyword().trim() : null)
                // 根据创建时间范围筛选
                .ge(commentSearchDto.getCreateTimeStart() != null, Comment::getCreateTime, commentSearchDto.getCreateTimeStart())
                .le(commentSearchDto.getCreateTimeEnd() != null, Comment::getCreateTime, commentSearchDto.getCreateTimeEnd());

        List<Comment> comments = list(queryWrapper);

        if (comments.isEmpty()) {
            return new ArrayList<>();
        }

        // 批量构建AdminCommentVo
        return batchBuildAdminCommentVos(comments);
    }

    /**
     * 批量构建管理员评论VO对象
     *
     * @param comments 评论列表
     * @return AdminCommentVo列表
     */
    private List<AdminCommentVo> batchBuildAdminCommentVos(List<Comment> comments) {
        if (comments.isEmpty()) {
            return new ArrayList<>();
        }

        // 收集需要查询的用户ID和文章ID
        Set<Integer> userIds = new HashSet<>();
        Set<Integer> articleIds = new HashSet<>();
        Set<Integer> commentIds = new HashSet<>();

        comments.forEach(comment -> {
            userIds.add(comment.getUserId());
            articleIds.add(comment.getArticleId());
            commentIds.add(comment.getId());
            if (comment.getReplyUserId() != null) {
                userIds.add(comment.getReplyUserId());
            }
        });

        // 批量查询用户信息
        final Map<Integer, SysUser> userMap;
        if (!userIds.isEmpty()) {
            LambdaQueryWrapper<SysUser> userWrapper = new LambdaQueryWrapper<SysUser>()
                    .in(SysUser::getId, userIds)
                    .select(SysUser::getId, SysUser::getNickname, SysUser::getAvatar);
            List<SysUser> users = sysUserMapper.selectList(userWrapper);
            userMap = users.stream().collect(Collectors.toMap(SysUser::getId, u -> u));
        } else {
            userMap = new HashMap<>();
        }

        // 批量查询文章信息
        final Map<Integer, Article> articleMap;
        if (!articleIds.isEmpty()) {
            LambdaQueryWrapper<Article> articleWrapper = new LambdaQueryWrapper<Article>()
                    .in(Article::getId, articleIds)
                    .select(Article::getId, Article::getTitle);
            List<Article> articles = articleMapper.selectList(articleWrapper);
            articleMap = articles.stream().collect(Collectors.toMap(Article::getId, a -> a));
        } else {
            articleMap = new HashMap<>();
        }

        // 批量查询点赞统计
        final Map<Integer, Long> likeCountMap;
        if (!commentIds.isEmpty()) {
            LambdaQueryWrapper<Like> likeCountWrapper = new LambdaQueryWrapper<Like>()
                    .eq(Like::getType, 1)
                    .in(Like::getTypeId, commentIds)
                    .select(Like::getTypeId);
            List<Like> likes = likeMapper.selectList(likeCountWrapper);
            likeCountMap = likes.stream()
                    .collect(Collectors.groupingBy(Like::getTypeId, Collectors.counting()));
        } else {
            likeCountMap = new HashMap<>();
        }

        // 构建结果
        return comments.stream()
                .map(comment -> buildAdminCommentVo(comment, userMap, articleMap, likeCountMap))
                .collect(Collectors.toList());
    }

    /**
     * 构建管理员评论VO对象
     *
     * @param comment      评论实体
     * @param userMap      用户信息映射
     * @param articleMap   文章信息映射
     * @param likeCountMap 点赞数映射
     * @return 管理员评论VO
     */
    private AdminCommentVo buildAdminCommentVo(Comment comment, Map<Integer, SysUser> userMap,
                                               Map<Integer, Article> articleMap, Map<Integer, Long> likeCountMap) {
        AdminCommentVo vo = BeanUtil.copyProperties(comment, AdminCommentVo.class);

        // 设置用户信息
        SysUser user = userMap.get(comment.getUserId());
        if (user != null) {
            vo.setNickname(user.getNickname());
            vo.setAvatar(user.getAvatar());
        }

        // 设置回复用户信息
        if (comment.getReplyUserId() != null) {
            SysUser replyUser = userMap.get(comment.getReplyUserId());
            if (replyUser != null) {
                vo.setReplyUserNickname(replyUser.getNickname());
            }
        }

        // 设置文章信息
        Article article = articleMap.get(comment.getArticleId());
        if (article != null) {
            vo.setArticleTitle(article.getTitle());
        }

        // 设置点赞数
        vo.setLikeCount(Math.toIntExact(likeCountMap.getOrDefault(comment.getId(), 0L)));

        return vo;
    }

    @Override
    public void adminExamineComment(CommentAuditDto commentAuditDto) {
        if (commentAuditDto == null || commentAuditDto.getCommentId() == null || commentAuditDto.getCommentId() <= 0) {
            throw new BlogException(BlogConstants.CommentIdRequired);
        }

        Integer examineStatus = commentAuditDto.getExamineStatus();
        if (examineStatus == null || examineStatus < 0 || examineStatus > 2) {
            throw new BlogException(BlogConstants.CommentExamineStatusError);
        }

        Comment comment = getById(commentAuditDto.getCommentId());
        if (comment == null || comment.getIsDeleted() == 1) {
            throw new BlogException(BlogConstants.NotFoundComment);
        }

        comment.setExamineStatus(examineStatus);
        // 如果审核未通过且有原因，可以记录原因（这里暂时不保存到数据库）
        boolean updated = updateById(comment);
        if (!updated) {
            throw new BlogException(BlogConstants.CommentAuditError);
        }

        log.info("管理员审核评论，评论ID：{}，审核状态：{}，原因：{}",
                commentAuditDto.getCommentId(), examineStatus, commentAuditDto.getExamineReason());
    }

    @Override
    public void adminExamineBatchComment(List<CommentAuditDto> commentAuditDtos) {
        if (commentAuditDtos == null || commentAuditDtos.isEmpty()) {
            throw new BlogException(BlogConstants.CommentIdRequired);
        }

        for (CommentAuditDto commentAuditDto : commentAuditDtos) {
            if (commentAuditDto.getCommentId() == null || commentAuditDto.getCommentId() <= 0) {
                continue; // 跳过无效的评论ID
            }

            try {
                adminExamineComment(commentAuditDto);
            } catch (Exception e) {
                log.error("批量审核评论失败，评论ID：{}，错误：{}", commentAuditDto.getCommentId(), e.getMessage());
                // 继续处理其他评论，不中断整个批量操作
            }
        }

        log.info("管理员批量审核评论，共处理{}条评论", commentAuditDtos.size());
    }

    @Override
    public void adminDeleteComment(Integer commentId) {
        if (commentId == null || commentId <= 0) {
            throw new BlogException(BlogConstants.CommentIdRequired);
        }

        Comment comment = getById(commentId);
        if (comment == null || comment.getIsDeleted() == 1) {
            throw new BlogException(BlogConstants.NotFoundComment);
        }

        // 统计要删除的评论总数（包括所有子评论）
        int deletedCommentCount = countCommentAndReplies(commentId);

        // 递归删除评论及其所有子评论
        deleteCommentAndReplies(commentId);

        // 如果是回复评论，更新父评论的回复数（只减少当前评论，不包括子评论）
        if (comment.getParentId() != null && comment.getParentId() > 0) {
            LambdaUpdateWrapper<Comment> replyUpdateWrapper = new LambdaUpdateWrapper<>();
            replyUpdateWrapper.eq(Comment::getId, comment.getParentId())
                    .setIncrBy(Comment::getReplyCount, -1);
            commentMapper.update(null, replyUpdateWrapper);
        }

        // 更新文章评论数（减少所有被删除的评论数量）
        LambdaUpdateWrapper<Article> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Article::getId, comment.getArticleId())
                .setIncrBy(Article::getCommentCount, -deletedCommentCount);
        boolean updateResult = articleMapper.update(null, updateWrapper) > 0;
        if (!updateResult) {
            log.warn("更新文章评论数失败，文章ID：{}", comment.getArticleId());
        }

        log.info("管理员删除评论及其所有子评论，评论ID：{}，删除数量：{}", commentId, deletedCommentCount);
    }

    @Override
    public void adminDeleteBatchComment(List<Integer> commentIds) {
        if (commentIds == null || commentIds.isEmpty()) {
            throw new BlogException(BlogConstants.CommentIdRequired);
        }

        // 过滤无效的评论ID
        List<Integer> validCommentIds = commentIds.stream()
                .filter(id -> id != null && id > 0)
                .collect(Collectors.toList());

        if (validCommentIds.isEmpty()) {
            throw new BlogException(BlogConstants.CommentIdRequired);
        }

        // 验证评论是否存在
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<Comment>()
                .in(Comment::getId, validCommentIds)
                .eq(Comment::getIsDeleted, 0);
        
        List<Comment> existingComments = list(queryWrapper);
        
        if (existingComments.isEmpty()) {
            throw new BlogException(BlogConstants.NotFoundComment);
        }

        // 获取实际存在的评论ID
        List<Integer> existingCommentIds = existingComments.stream()
                .map(Comment::getId)
                .collect(Collectors.toList());

        // 查找所有需要删除的子评论（递归查找）
        Set<Integer> allCommentIdsToDelete = new HashSet<>(existingCommentIds);
        for (Integer commentId : existingCommentIds) {
            collectChildCommentIds(commentId, allCommentIdsToDelete);
        }

        // 批量逻辑删除所有相关评论（MyBatis-Plus 自动处理逻辑删除）
        if (!allCommentIdsToDelete.isEmpty()) {
            boolean deleted = removeByIds(allCommentIdsToDelete);
            if (!deleted) {
                throw new BlogException(BlogConstants.CommentDeleteError);
            }
        }

        log.info("管理员批量删除评论成功，删除评论数量：{}（包含子评论）", allCommentIdsToDelete.size());
    }

    /**
     * 递归统计评论及其所有回复的数量
     *
     * @param commentId 评论ID
     * @return 评论总数（包括当前评论和所有子评论）
     */
    private int countCommentAndReplies(Integer commentId) {
        int count = 1; // 当前评论计数为1

        // 查找所有子评论
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getParentId, commentId)
                .eq(Comment::getIsDeleted, 0);
        List<Comment> childComments = list(queryWrapper);

        // 递归统计子评论
        for (Comment childComment : childComments) {
            count += countCommentAndReplies(childComment.getId());
        }

        return count;
    }

    /**
     * 递归删除评论及其所有回复
     *
     * @param commentId 评论ID
     */
    private void deleteCommentAndReplies(Integer commentId) {
        // 查找所有子评论
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getParentId, commentId)
                .eq(Comment::getIsDeleted, 0);
        List<Comment> childComments = list(queryWrapper);

        // 递归删除子评论
        for (Comment childComment : childComments) {
            deleteCommentAndReplies(childComment.getId());
        }

        // 删除当前评论
        removeById(commentId);
    }

    /**
     * 递归收集评论及其所有子评论的ID
     *
     * @param commentId 评论ID
     * @param commentIds 收集的评论ID集合
     */
    private void collectChildCommentIds(Integer commentId, Set<Integer> commentIds) {
        // 查询子评论
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<Comment>()
                .eq(Comment::getParentId, commentId)
                .eq(Comment::getIsDeleted, 0);
        List<Comment> childComments = list(queryWrapper);

        // 递归收集子评论ID
        for (Comment childComment : childComments) {
            commentIds.add(childComment.getId());
            collectChildCommentIds(childComment.getId(), commentIds);
        }
    }

    @Override
    public Long getCommentTotalCount() {
        return this.count(new LambdaQueryWrapper<Comment>()
                .eq(Comment::getIsDeleted, 0)); // 只统计未删除的评论
    }

}
