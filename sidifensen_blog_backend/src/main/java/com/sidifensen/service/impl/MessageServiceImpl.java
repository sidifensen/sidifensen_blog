package com.sidifensen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.alibaba.fastjson.JSON;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.dto.MessageDto;
import com.sidifensen.domain.dto.NotificationContentDto;
import com.sidifensen.domain.dto.WebSocketMessage;
import com.sidifensen.domain.entity.Message;
import com.sidifensen.domain.entity.SysUser;
import com.sidifensen.domain.enums.MessageTypeEnum;
import com.sidifensen.domain.enums.WebSocketMessageTypeEnum;
import com.sidifensen.domain.vo.MessageVo;
import com.sidifensen.domain.vo.PageVo;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.MessageMapper;
import com.sidifensen.mapper.SysUserMapper;
import com.sidifensen.service.MessageService;
import com.sidifensen.utils.SecurityUtils;
import com.sidifensen.websocket.WebSocketSessionManager;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author sidifensen
 * @since 2025-08-17
 */
@Service
@Slf4j
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Resource
    private MessageMapper messageMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private WebSocketSessionManager webSocketSessionManager;

    // 发送消息
    @Override
    public void send(MessageDto messageDto) {
        Message message = BeanUtil.copyProperties(messageDto, Message.class);
        int save = messageMapper.insert(message);
        if (save <= 0) {
            throw new BlogException(BlogConstants.SaveMessageError);
        }
    }

    // 给用户发送消息（系统消息）
    @Override
    public void sendToUser(MessageDto messageDto) {
        Message message = BeanUtil.copyProperties(messageDto, Message.class);
        message.setSenderId(1);
        message.setReceiverId(messageDto.getReceiverId());
        int save = messageMapper.insert(message);
        if (save <= 0) {
            throw new BlogException(BlogConstants.SaveMessageError);
        }
    }

    // 批量给用户发送消息
    @Override
    public void sendBatchToUsers(List<MessageDto> messageDtos) {
        if (ObjectUtil.isEmpty(messageDtos)) {
            return;
        }

        // 转换为Message实体列表
        List<Message> messages = messageDtos.stream()
                .map(dto -> {
                    Message message = BeanUtil.copyProperties(dto, Message.class);
                    message.setSenderId(1); // 系统发送
                    message.setReceiverId(dto.getReceiverId());
                    return message;
                })
                .toList();

        // 批量插入消息
        boolean success = this.saveBatch(messages);
        if (!success) {
            throw new BlogException(BlogConstants.SaveMessageError);
        }
    }

    // 发送给管理员
    @Override
    public void sendToAdmin(MessageDto messageDto) {
        Message message = BeanUtil.copyProperties(messageDto, Message.class);
        message.setSenderId(1);
        message.setReceiverId(1);
        int save = messageMapper.insert(message);
        if (save <= 0) {
            throw new BlogException(BlogConstants.SaveMessageError);
        }

    }

    // 统计管理员未读消息数量
    @Override
    public Integer getAdminMessagesCount() {
        Long count = messageMapper.selectCount(new LambdaQueryWrapper<Message>().eq(Message::getIsRead, 0));
        return count.intValue();
    }

    // 获取管理员消息列表
    @Override
    public List<MessageVo> getAdminMessages() {
        List<Message> messages = this.list(new LambdaQueryWrapper<Message>().orderByDesc(Message::getCreateTime));
        if (ObjectUtil.isEmpty(messages)) {
            return List.of();
        }
        List<MessageVo> messageVos = BeanUtil.copyToList(messages, MessageVo.class);
        return messageVos;
    }

    // 管理员读取消息
    @Override
    public void readAdminMessages(List<Integer> messageIds) {
        if (ObjectUtil.isEmpty(messageIds)) {
            return;
        }

        // 批量更新消息状态为已读
        LambdaUpdateWrapper<Message> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Message::getId, messageIds)
                .set(Message::getIsRead, 1);

        messageMapper.update(null, updateWrapper);
    }

    // 管理员删除消息
    @Override
    public void deleteAdminMessages(List<Integer> messageIds) {
        if (ObjectUtil.isEmpty(messageIds)) {
            return;
        }
        int delete = messageMapper.deleteBatchIds(messageIds);
        if (delete <= 0) {
            throw new BlogException(BlogConstants.DeleteMessageError);
        }
    }

    // 发送评论通知
    @Override
    public void sendCommentNotification(Integer commenterId, Integer authorId, String commenterNickname,
            String articleTitle, Integer articleId, String commentContent) {
        // 不给自己发通知
        if (commenterId.equals(authorId)) {
            return;
        }

        try {
            // 查询评论者的用户信息
            SysUser commenter = sysUserMapper.selectById(commenterId);
            if (commenter == null) {
                log.error("发送评论通知失败：评论者不存在，commenterId={}", commenterId);
                return;
            }

            // 构建通知内容
            NotificationContentDto notificationContent = NotificationContentDto.builder()
                    .title(BlogConstants.CommentNotification(commenterNickname, articleTitle))
                    .userId(commenterId)
                    .nickname(commenter.getNickname())
                    .avatar(commenter.getAvatar())
                    .articleId(articleId)
                    .articleTitle(articleTitle)
                    .authorId(authorId) // 文章作者ID
                    .commentContent(commentContent) // 评论内容
                    .build();

            // 将通知内容序列化为JSON
            String contentJson = JSON.toJSONString(notificationContent);

            MessageDto messageDto = new MessageDto();
            messageDto.setType(MessageTypeEnum.COMMENT.getCode());
            messageDto.setContent(contentJson);
            messageDto.setSenderId(commenterId);
            messageDto.setReceiverId(authorId);

            send(messageDto);

            // 通过 WebSocket 实时推送通知
            pushSystemNotificationViaWebSocket(authorId, contentJson, MessageTypeEnum.COMMENT);
        } catch (Exception e) {
            log.error("发送评论通知失败：commenterId={}, authorId={}, articleTitle={}",
                    commenterId, authorId, articleTitle, e);
        }
    }

    // 发送回复通知
    @Override
    public void sendReplyNotification(Integer replierId, Integer repliedUserId, String replierNickname,
            String articleTitle, Integer articleId, Integer authorId, String commentContent) {
        // 不给自己发通知
        if (replierId.equals(repliedUserId)) {
            return;
        }

        try {
            // 查询回复者的用户信息
            SysUser replier = sysUserMapper.selectById(replierId);
            if (replier == null) {
                log.error("发送回复通知失败：回复者不存在，replierId={}", replierId);
                return;
            }

            // 构建通知内容
            NotificationContentDto notificationContent = NotificationContentDto.builder()
                    .title(BlogConstants.ReplyNotification(replierNickname, articleTitle))
                    .userId(replierId)
                    .nickname(replier.getNickname())
                    .avatar(replier.getAvatar())
                    .articleId(articleId)
                    .articleTitle(articleTitle)
                    .authorId(authorId) // 文章作者ID
                    .commentContent(commentContent) // 回复内容
                    .build();

            // 将通知内容序列化为JSON
            String contentJson = JSON.toJSONString(notificationContent);

            MessageDto messageDto = new MessageDto();
            messageDto.setType(MessageTypeEnum.COMMENT.getCode());
            messageDto.setContent(contentJson);
            messageDto.setSenderId(replierId);
            messageDto.setReceiverId(repliedUserId);

            send(messageDto);

            // 通过 WebSocket 实时推送通知
            pushSystemNotificationViaWebSocket(repliedUserId, contentJson, MessageTypeEnum.COMMENT);
        } catch (Exception e) {
            log.error("发送回复通知失败：replierId={}, repliedUserId={}, articleTitle={}",
                    replierId, repliedUserId, articleTitle, e);
        }
    }

    // 发送点赞文章通知
    @Override
    public void sendLikeArticleNotification(Integer likerId, Integer authorId, String likerNickname,
            String articleTitle, Integer articleId) {
        // 不给自己发通知
        if (likerId.equals(authorId)) {
            return;
        }

        try {
            // 检查是否已存在相同的点赞文章通知（同一用户对同一文章的点赞通知）
            LambdaQueryWrapper<Message> checkWrapper = new LambdaQueryWrapper<Message>()
                    .eq(Message::getSenderId, likerId)
                    .eq(Message::getReceiverId, authorId)
                    .eq(Message::getType, MessageTypeEnum.LIKE.getCode())
                    .like(Message::getContent, "\"articleId\":" + articleId);

            Long existingCount = messageMapper.selectCount(checkWrapper);
            if (existingCount > 0) {
                log.debug("点赞文章通知已存在，不再重复发送：likerId={}, authorId={}, articleId={}",
                        likerId, authorId, articleId);
                return;
            }

            // 查询点赞者的用户信息
            SysUser liker = sysUserMapper.selectById(likerId);
            if (liker == null) {
                log.error("发送点赞文章通知失败：点赞者不存在，likerId={}", likerId);
                return;
            }

            // 构建通知内容
            NotificationContentDto notificationContent = NotificationContentDto.builder()
                    .title(BlogConstants.LikeArticleNotification(likerNickname, articleTitle))
                    .userId(likerId)
                    .nickname(liker.getNickname())
                    .avatar(liker.getAvatar())
                    .articleId(articleId)
                    .articleTitle(articleTitle)
                    .authorId(authorId) // 文章作者ID
                    .build();

            // 将通知内容序列化为JSON
            String contentJson = JSON.toJSONString(notificationContent);

            MessageDto messageDto = new MessageDto();
            messageDto.setType(MessageTypeEnum.LIKE.getCode());
            messageDto.setContent(contentJson);
            messageDto.setSenderId(likerId);
            messageDto.setReceiverId(authorId);

            send(messageDto);

            // 通过 WebSocket 实时推送通知
            pushSystemNotificationViaWebSocket(authorId, contentJson, MessageTypeEnum.LIKE);
        } catch (Exception e) {
            log.error("发送点赞文章通知失败：likerId={}, authorId={}, articleTitle={}",
                    likerId, authorId, articleTitle, e);
        }
    }

    // 发送点赞评论通知
    @Override
    public void sendLikeCommentNotification(Integer likerId, Integer commenterId, String likerNickname) {
        // 不给自己发通知
        if (likerId.equals(commenterId)) {
            return;
        }

        try {
            // 检查是否已存在相同的点赞评论通知（同一用户对同一评论者的点赞通知）
            // 点赞评论的通知 content 中不包含 articleId，用 notLike 来区分点赞评论和点赞文章
            LambdaQueryWrapper<Message> checkWrapper = new LambdaQueryWrapper<Message>()
                    .eq(Message::getSenderId, likerId)
                    .eq(Message::getReceiverId, commenterId)
                    .eq(Message::getType, MessageTypeEnum.LIKE.getCode())
                    .notLike(Message::getContent, "\"articleId\""); // 点赞评论的通知没有articleId

            Long existingCount = messageMapper.selectCount(checkWrapper);
            if (existingCount > 0) {
                log.debug("点赞评论通知已存在，不再重复发送：likerId={}, commenterId={}",
                        likerId, commenterId);
                return;
            }

            // 查询点赞者的用户信息
            SysUser liker = sysUserMapper.selectById(likerId);
            if (liker == null) {
                log.error("发送点赞评论通知失败：点赞者不存在，likerId={}", likerId);
                return;
            }

            // 构建通知内容（点赞评论没有文章信息）
            NotificationContentDto notificationContent = NotificationContentDto.builder()
                    .title(BlogConstants.LikeCommentNotification(likerNickname))
                    .userId(likerId)
                    .nickname(liker.getNickname())
                    .avatar(liker.getAvatar())
                    .build();

            // 将通知内容序列化为JSON
            String contentJson = JSON.toJSONString(notificationContent);

            MessageDto messageDto = new MessageDto();
            messageDto.setType(MessageTypeEnum.LIKE.getCode());
            messageDto.setContent(contentJson);
            messageDto.setSenderId(likerId);
            messageDto.setReceiverId(commenterId);

            send(messageDto);

            // 通过 WebSocket 实时推送通知
            pushSystemNotificationViaWebSocket(commenterId, contentJson, MessageTypeEnum.LIKE);
        } catch (Exception e) {
            log.error("发送点赞评论通知失败：likerId={}, commenterId={}",
                    likerId, commenterId, e);
        }
    }

    // 发送收藏文章通知
    @Override
    public void sendCollectArticleNotification(Integer collectorId, Integer authorId, String collectorNickname,
            String articleTitle, Integer articleId) {
        // 不给自己发通知
        if (collectorId.equals(authorId)) {
            return;
        }

        try {
            // 检查是否已存在相同的收藏文章通知（同一用户对同一文章的收藏通知）
            LambdaQueryWrapper<Message> checkWrapper = new LambdaQueryWrapper<Message>()
                    .eq(Message::getSenderId, collectorId)
                    .eq(Message::getReceiverId, authorId)
                    .eq(Message::getType, MessageTypeEnum.COLLECT.getCode())
                    .like(Message::getContent, "\"articleId\":" + articleId);

            Long existingCount = messageMapper.selectCount(checkWrapper);
            if (existingCount > 0) {
                log.debug("收藏文章通知已存在，不再重复发送：collectorId={}, authorId={}, articleId={}",
                        collectorId, authorId, articleId);
                return;
            }

            // 查询收藏者的用户信息
            SysUser collector = sysUserMapper.selectById(collectorId);
            if (collector == null) {
                log.error("发送收藏文章通知失败：收藏者不存在，collectorId={}", collectorId);
                return;
            }

            // 构建通知内容
            NotificationContentDto notificationContent = NotificationContentDto.builder()
                    .title(BlogConstants.CollectArticleNotification(collectorNickname, articleTitle))
                    .userId(collectorId)
                    .nickname(collector.getNickname())
                    .avatar(collector.getAvatar())
                    .articleId(articleId)
                    .articleTitle(articleTitle)
                    .authorId(authorId) // 文章作者ID
                    .build();

            // 将通知内容序列化为JSON
            String contentJson = JSON.toJSONString(notificationContent);

            MessageDto messageDto = new MessageDto();
            messageDto.setType(MessageTypeEnum.COLLECT.getCode());
            messageDto.setContent(contentJson);
            messageDto.setSenderId(collectorId);
            messageDto.setReceiverId(authorId);

            send(messageDto);

            // 通过 WebSocket 实时推送通知
            pushSystemNotificationViaWebSocket(authorId, contentJson, MessageTypeEnum.COLLECT);
        } catch (Exception e) {
            log.error("发送收藏文章通知失败：collectorId={}, authorId={}, articleTitle={}",
                    collectorId, authorId, articleTitle, e);
        }
    }

    // 发送关注通知
    @Override
    public void sendFollowNotification(Integer followerId, Integer followedId, String followerNickname) {
        // 不给自己发通知
        if (followerId.equals(followedId)) {
            return;
        }

        try {
            // 检查是否已存在相同的关注通知（同一用户对同一被关注者的关注通知）
            LambdaQueryWrapper<Message> checkWrapper = new LambdaQueryWrapper<Message>()
                    .eq(Message::getSenderId, followerId)
                    .eq(Message::getReceiverId, followedId)
                    .eq(Message::getType, MessageTypeEnum.FOLLOW.getCode());

            Long existingCount = messageMapper.selectCount(checkWrapper);
            if (existingCount > 0) {
                log.debug("关注通知已存在，不再重复发送：followerId={}, followedId={}",
                        followerId, followedId);
                return;
            }

            // 查询关注者的用户信息
            SysUser follower = sysUserMapper.selectById(followerId);
            if (follower == null) {
                log.error("发送关注通知失败：关注者不存在，followerId={}", followerId);
                return;
            }

            // 构建通知内容（关注通知没有文章信息）
            NotificationContentDto notificationContent = NotificationContentDto.builder()
                    .title(BlogConstants.FollowNotification(followerNickname))
                    .userId(followerId)
                    .nickname(follower.getNickname())
                    .avatar(follower.getAvatar())
                    .build();

            // 将通知内容序列化为JSON
            String contentJson = JSON.toJSONString(notificationContent);

            MessageDto messageDto = new MessageDto();
            messageDto.setType(MessageTypeEnum.FOLLOW.getCode());
            messageDto.setContent(contentJson);
            messageDto.setSenderId(followerId);
            messageDto.setReceiverId(followedId);

            send(messageDto);

            // 通过 WebSocket 实时推送通知
            pushSystemNotificationViaWebSocket(followedId, contentJson, MessageTypeEnum.FOLLOW);
        } catch (Exception e) {
            log.error("发送关注通知失败：followerId={}, followedId={}",
                    followerId, followedId, e);
        }
    }

    /**
     * 通过 WebSocket 实时推送系统通知
     * 
     * @param receiverId  接收者ID
     * @param content     通知内容
     * @param messageType 消息类型
     */
    private void pushSystemNotificationViaWebSocket(Integer receiverId, String content, MessageTypeEnum messageType) {
        try {
            // 检查用户是否在线
            if (!webSocketSessionManager.isOnline(receiverId)) {
                log.debug("用户 {} 不在线，无法实时推送通知", receiverId);
                return;
            }

            // 构造 WebSocket 消息
            WebSocketMessage wsMessage = WebSocketMessage.builder()
                    .type(WebSocketMessageTypeEnum.NEW_NOTIFICATION.getType())
                    .content(content)
                    .notificationType(messageType.getCode())
                    .build();

            // 转换为 JSON 并发送
            String message = JSON.toJSONString(wsMessage);
            webSocketSessionManager.sendMessage(receiverId, message);

            log.debug("成功通过 WebSocket 推送新通知给用户 {}, 类型: {}", receiverId, messageType.getDesc());
        } catch (Exception e) {
            log.error("通过 WebSocket 推送新通知失败：receiverId={}, messageType={}",
                    receiverId, messageType.getDesc(), e);
        }
    }

    // 获取用户通知列表（按类型）
    @Override
    public PageVo<List<MessageVo>> getUserNotifications(Integer type, Integer pageNum, Integer pageSize) {
        Integer currentUserId = SecurityUtils.getUserId();

        Page<Message> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<Message>()
                .eq(Message::getReceiverId, currentUserId)
                .orderByDesc(Message::getCreateTime);

        // 如果指定了类型，按类型筛选
        if (type != null && type >= 0) {
            queryWrapper.eq(Message::getType, type);
        }

        Page<Message> messagePage = messageMapper.selectPage(page, queryWrapper);
        List<MessageVo> messageVos = BeanUtil.copyToList(messagePage.getRecords(), MessageVo.class);

        return new PageVo<>(messageVos, messagePage.getTotal());
    }

    // 获取用户未读通知数量（按类型统计）
    @Override
    public Map<String, Integer> getUnreadNotificationCount() {
        Integer currentUserId = SecurityUtils.getUserId();

        Map<String, Integer> countMap = new HashMap<>();

        // 系统通知
        Long systemCount = messageMapper.selectCount(
                new LambdaQueryWrapper<Message>()
                        .eq(Message::getReceiverId, currentUserId)
                        .eq(Message::getIsRead, 0)
                        .eq(Message::getType, MessageTypeEnum.SYSTEM.getCode()));
        countMap.put("system", systemCount.intValue());

        // 评论通知
        Long commentCount = messageMapper.selectCount(
                new LambdaQueryWrapper<Message>()
                        .eq(Message::getReceiverId, currentUserId)
                        .eq(Message::getIsRead, 0)
                        .eq(Message::getType, MessageTypeEnum.COMMENT.getCode()));
        countMap.put("comment", commentCount.intValue());

        // 点赞通知
        Long likeCount = messageMapper.selectCount(
                new LambdaQueryWrapper<Message>()
                        .eq(Message::getReceiverId, currentUserId)
                        .eq(Message::getIsRead, 0)
                        .eq(Message::getType, MessageTypeEnum.LIKE.getCode()));
        countMap.put("like", likeCount.intValue());

        // 收藏通知
        Long collectCount = messageMapper.selectCount(
                new LambdaQueryWrapper<Message>()
                        .eq(Message::getReceiverId, currentUserId)
                        .eq(Message::getIsRead, 0)
                        .eq(Message::getType, MessageTypeEnum.COLLECT.getCode()));
        countMap.put("collect", collectCount.intValue());

        // 关注通知
        Long followCount = messageMapper.selectCount(
                new LambdaQueryWrapper<Message>()
                        .eq(Message::getReceiverId, currentUserId)
                        .eq(Message::getIsRead, 0)
                        .eq(Message::getType, MessageTypeEnum.FOLLOW.getCode()));
        countMap.put("follow", followCount.intValue());

        // 总未读数
        Long totalCount = messageMapper.selectCount(
                new LambdaQueryWrapper<Message>()
                        .eq(Message::getReceiverId, currentUserId)
                        .eq(Message::getIsRead, 0));
        countMap.put("total", totalCount.intValue());

        return countMap;
    }

    // 标记通知为已读
    @Override
    public void markNotificationsAsRead(List<Integer> messageIds) {
        if (ObjectUtil.isEmpty(messageIds)) {
            return;
        }

        Integer currentUserId = SecurityUtils.getUserId();

        // 只能标记自己的消息为已读
        LambdaUpdateWrapper<Message> updateWrapper = new LambdaUpdateWrapper<Message>()
                .set(Message::getIsRead, 1)
                .eq(Message::getReceiverId, currentUserId)
                .in(Message::getId, messageIds);

        int updated = messageMapper.update(null, updateWrapper);
        if (updated <= 0) {
            throw new BlogException(BlogConstants.MarkMessageAsReadError);
        }
    }

    // 删除通知
    @Override
    public void deleteNotifications(List<Integer> messageIds) {
        if (ObjectUtil.isEmpty(messageIds)) {
            return;
        }

        Integer currentUserId = SecurityUtils.getUserId();

        // 只能删除自己的消息（逻辑删除）
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<Message>()
                .eq(Message::getReceiverId, currentUserId)
                .in(Message::getId, messageIds);

        int deleted = messageMapper.delete(queryWrapper);
        if (deleted <= 0) {
            throw new BlogException(BlogConstants.DeleteMessageError);
        }
    }

}
