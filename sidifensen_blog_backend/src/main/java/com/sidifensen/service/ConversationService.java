package com.sidifensen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sidifensen.domain.entity.Conversation;
import com.sidifensen.domain.entity.PrivateMessage;
import com.sidifensen.domain.vo.ConversationVo;

import java.util.List;

/**
 * 会话服务接口
 */
public interface ConversationService extends IService<Conversation> {

    /**
     * 获取会话列表
     */
    List<ConversationVo> getConversationList(Integer userId);

    /**
     * 更新会话（发送消息后调用）
     */
    void updateConversation(Integer fromUserId, Integer toUserId, PrivateMessage message);

    /**
     * 清空未读数
     */
    void clearUnreadCount(Integer userId, Integer targetUserId);

    /**
     * 删除会话
     */
    void deleteConversation(Integer userId, Integer targetUserId);

    /**
     * 获取未读数
     */
    Integer getUnreadCount(Integer userId, Integer targetUserId);

    /**
     * 获取与指定用户有会话的所有用户ID列表
     */
    List<Integer> getConversationUserIds(Integer userId);

    /**
     * 撤回消息后更新会话的最后一条消息内容
     * 
     * @param userId       用户ID
     * @param targetUserId 目标用户ID
     * @param revokeText   撤回提示文本
     */
    void updateLastMessageAfterRevoke(Integer userId, Integer targetUserId, String revokeText);
}
