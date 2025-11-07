package com.sidifensen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.entity.Conversation;
import com.sidifensen.domain.entity.PrivateMessage;
import com.sidifensen.domain.entity.SysUser;
import com.sidifensen.domain.vo.ConversationVo;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.ConversationMapper;
import com.sidifensen.mapper.SysUserMapper;
import com.sidifensen.redis.RedisComponent;
import com.sidifensen.service.ConversationService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 会话服务实现类
 */
@Service
@Slf4j
public class ConversationServiceImpl extends ServiceImpl<ConversationMapper, Conversation>
        implements ConversationService {

    @Resource
    private ConversationMapper conversationMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private RedisComponent redisComponent;

    @Override
    public List<ConversationVo> getConversationList(Integer userId) {
        LambdaQueryWrapper<Conversation> qw = new LambdaQueryWrapper<Conversation>()
                .eq(Conversation::getUserId, userId)
                .orderByDesc(Conversation::getUpdateTime);

        List<Conversation> conversations = conversationMapper.selectList(qw);

        if (conversations.isEmpty()) {
            return new ArrayList<>();
        }

        // 收集所有目标用户ID
        List<Integer> targetUserIds = conversations.stream()
                .map(Conversation::getTargetUserId)
                .distinct()
                .collect(Collectors.toList());

        // 批量查询用户信息（避免 N+1 查询）
        List<SysUser> targetUsers = sysUserMapper.selectBatchIds(targetUserIds);
        Map<Integer, SysUser> userMap = targetUsers.stream()
                .collect(Collectors.toMap(SysUser::getId, user -> user));

        // 批量查询在线状态（避免 N+1 Redis 查询）
        Map<Integer, Boolean> onlineStatusMap = redisComponent.batchGetUserOnlineStatus(targetUserIds);

        // 转换为 VO 并填充数据
        return conversations.stream().map(conv -> {
            ConversationVo vo = BeanUtil.copyProperties(conv, ConversationVo.class);

            // 从 Map 中获取目标用户信息
            SysUser targetUser = userMap.get(conv.getTargetUserId());
            if (targetUser != null) {
                vo.setTargetUserNickname(targetUser.getNickname());
                vo.setTargetUserAvatar(targetUser.getAvatar());
            }

            // 从 Map 中获取在线状态
            vo.setIsOnline(onlineStatusMap.getOrDefault(conv.getTargetUserId(), false));

            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public void updateConversation(Integer fromUserId, Integer toUserId, PrivateMessage message) {
        // 更新发送者的会话
        updateUserConversation(fromUserId, toUserId, message, false);

        // 更新接收者的会话
        updateUserConversation(toUserId, fromUserId, message, true);
    }

    private void updateUserConversation(Integer userId, Integer targetUserId, PrivateMessage message,
            boolean incrementUnread) {
        LambdaQueryWrapper<Conversation> qw = new LambdaQueryWrapper<Conversation>()
                .eq(Conversation::getUserId, userId)
                .eq(Conversation::getTargetUserId, targetUserId);

        Conversation conversation = conversationMapper.selectOne(qw);

        if (conversation == null) {
            // 创建新会话
            conversation = new Conversation();
            conversation.setUserId(userId);
            conversation.setTargetUserId(targetUserId);
            conversation.setLastMessageId(message.getId());
            conversation.setLastMessageContent(message.getContent());
            conversation.setLastMessageTime(message.getCreateTime());
            conversation.setUnreadCount(incrementUnread ? 1 : 0);
            conversation.setCreateTime(new Date());
            conversation.setUpdateTime(new Date());
            conversationMapper.insert(conversation);
        } else {
            // 更新已有会话
            conversation.setLastMessageId(message.getId());
            conversation.setLastMessageContent(message.getContent());
            conversation.setLastMessageTime(message.getCreateTime());
            if (incrementUnread) {
                conversation.setUnreadCount(conversation.getUnreadCount() + 1);
            }
            conversation.setUpdateTime(new Date());
            conversationMapper.updateById(conversation);
        }
    }

    @Override
    public void clearUnreadCount(Integer userId, Integer targetUserId) {
        LambdaUpdateWrapper<Conversation> updateWrapper = new LambdaUpdateWrapper<Conversation>()
                .eq(Conversation::getUserId, userId)
                .eq(Conversation::getTargetUserId, targetUserId)
                .set(Conversation::getUnreadCount, 0);

        conversationMapper.update(null, updateWrapper);
    }

    @Override
    public void deleteConversation(Integer userId, Integer targetUserId) {
        LambdaQueryWrapper<Conversation> qw = new LambdaQueryWrapper<Conversation>()
                .eq(Conversation::getUserId, userId)
                .eq(Conversation::getTargetUserId, targetUserId);

        Conversation conversation = conversationMapper.selectOne(qw);

        if (conversation == null) {
            throw new BlogException(BlogConstants.NotFoundConversation);
        }

        conversationMapper.deleteById(conversation.getId());
    }

    @Override
    public Integer getUnreadCount(Integer userId, Integer targetUserId) {
        LambdaQueryWrapper<Conversation> qw = new LambdaQueryWrapper<Conversation>()
                .eq(Conversation::getUserId, userId)
                .eq(Conversation::getTargetUserId, targetUserId);

        Conversation conversation = conversationMapper.selectOne(qw);

        return conversation != null ? conversation.getUnreadCount() : 0;
    }

    @Override
    public List<Integer> getConversationUserIds(Integer userId) {
        LambdaQueryWrapper<Conversation> qw = new LambdaQueryWrapper<Conversation>()
                .eq(Conversation::getUserId, userId)
                .select(Conversation::getTargetUserId);

        List<Conversation> conversations = conversationMapper.selectList(qw);

        return conversations.stream()
                .map(Conversation::getTargetUserId)
                .collect(Collectors.toList());
    }

    @Override
    public void updateLastMessageAfterRevoke(Integer userId, Integer targetUserId, String revokeText) {
        // 更新用户自己的会话
        LambdaUpdateWrapper<Conversation> updateWrapper1 = new LambdaUpdateWrapper<Conversation>()
                .eq(Conversation::getUserId, userId)
                .eq(Conversation::getTargetUserId, targetUserId)
                .set(Conversation::getLastMessageContent, revokeText)
                .set(Conversation::getUpdateTime, new Date());

        conversationMapper.update(null, updateWrapper1);

        // 更新对方的会话
        LambdaUpdateWrapper<Conversation> updateWrapper2 = new LambdaUpdateWrapper<Conversation>()
                .eq(Conversation::getUserId, targetUserId)
                .eq(Conversation::getTargetUserId, userId)
                .set(Conversation::getLastMessageContent, revokeText)
                .set(Conversation::getUpdateTime, new Date());

        conversationMapper.update(null, updateWrapper2);
    }
}
