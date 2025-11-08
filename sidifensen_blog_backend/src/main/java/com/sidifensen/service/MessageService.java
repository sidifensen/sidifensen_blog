package com.sidifensen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sidifensen.domain.dto.MessageDto;
import com.sidifensen.domain.entity.Message;
import com.sidifensen.domain.vo.MessageVo;

import java.util.List;

/**
 * @author sidifensen
 * @since 2025-08-17
 */
public interface MessageService extends IService<Message> {

    // 发送消息
    void send(MessageDto messageDto);

    // 给用户发送消息
    void sendToUser(MessageDto messageDto);

    // 批量给用户发送消息
    void sendBatchToUsers(List<MessageDto> messageDtos);

    // 发送消息给管理员
    void sendToAdmin(MessageDto messageDto);

    // 获取管理员消息数量
    Integer getAdminMessagesCount();

    // 获取管理员消息列表
    List<MessageVo> getAdminMessages();

    // 管理员读取消息
    void readAdminMessages(List<Integer> messageIds);

    // 管理员删除消息
    void deleteAdminMessages(List<Integer> messageIds);

    // 发送评论通知
    void sendCommentNotification(Integer commenterId, Integer authorId, String commenterNickname, String articleTitle,
            Integer articleId, String commentContent);

    // 发送回复通知
    void sendReplyNotification(Integer replierId, Integer repliedUserId, String replierNickname, String articleTitle,
            Integer articleId, Integer authorId, String commentContent);

    // 发送点赞文章通知
    void sendLikeArticleNotification(Integer likerId, Integer authorId, String likerNickname, String articleTitle,
            Integer articleId);

    // 发送点赞评论通知
    void sendLikeCommentNotification(Integer likerId, Integer commenterId, String likerNickname);

    // 发送收藏文章通知
    void sendCollectArticleNotification(Integer collectorId, Integer authorId, String collectorNickname,
            String articleTitle, Integer articleId);

    // 发送关注通知
    void sendFollowNotification(Integer followerId, Integer followedId, String followerNickname);

    // 获取用户通知列表（按类型）
    Object getUserNotifications(Integer type, Integer pageNum, Integer pageSize);

    // 获取用户未读通知数量（按类型统计）
    Object getUnreadNotificationCount();

    // 标记通知为已读
    void markNotificationsAsRead(List<Integer> messageIds);

    // 删除通知
    void deleteNotifications(List<Integer> messageIds);
}
