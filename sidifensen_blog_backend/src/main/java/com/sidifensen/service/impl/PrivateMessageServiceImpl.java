package com.sidifensen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.constants.RedisConstants;
import com.sidifensen.domain.entity.PrivateMessage;
import com.sidifensen.domain.entity.SysUser;
import com.sidifensen.domain.vo.PageVo;
import com.sidifensen.domain.vo.PrivateMessageVo;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.PrivateMessageMapper;
import com.sidifensen.mapper.SysUserMapper;
import com.sidifensen.utils.RedisUtils;
import com.sidifensen.service.PrivateMessageService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 私信服务实现类
 */
@Service
@Slf4j
public class PrivateMessageServiceImpl extends ServiceImpl<PrivateMessageMapper, PrivateMessage>
        implements PrivateMessageService {

    @Resource
    private PrivateMessageMapper privateMessageMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private RedisUtils redisUtils;

    @Override
    public PageVo<List<PrivateMessageVo>> getChatHistory(Integer userId, Integer targetUserId,
            Integer pageNum, Integer pageSize) {
        Page<PrivateMessage> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<PrivateMessage> qw = new LambdaQueryWrapper<PrivateMessage>()
                .and(wrapper -> wrapper
                        .and(w -> w.eq(PrivateMessage::getFromUserId, userId).eq(PrivateMessage::getToUserId,
                                targetUserId))
                        .or(w -> w.eq(PrivateMessage::getFromUserId, targetUserId).eq(PrivateMessage::getToUserId,
                                userId)))
                .eq(PrivateMessage::getIsRevoked, 0) // 过滤掉已撤回的消息
                .orderByDesc(PrivateMessage::getCreateTime);

        List<PrivateMessage> messages = privateMessageMapper.selectPage(page, qw).getRecords();

        if (messages.isEmpty()) {
            return new PageVo<>(new ArrayList<>(), 0L);
        }

        // 收集所有涉及的用户ID
        Set<Integer> userIds = new HashSet<>();
        messages.forEach(msg -> {
            userIds.add(msg.getFromUserId());
            userIds.add(msg.getToUserId());
        });

        // 批量查询用户信息
        List<SysUser> users = sysUserMapper.selectBatchIds(userIds);
        Map<Integer, SysUser> userMap = users.stream()
                .collect(Collectors.toMap(SysUser::getId, user -> user));

        // 转换为 VO 并填充用户信息
        List<PrivateMessageVo> messageVos = messages.stream().map(msg -> {
            PrivateMessageVo vo = BeanUtil.copyProperties(msg, PrivateMessageVo.class);

            // 从 Map 中获取用户信息
            SysUser fromUser = userMap.get(msg.getFromUserId());
            if (fromUser != null) {
                vo.setFromUserNickname(fromUser.getNickname());
                vo.setFromUserAvatar(fromUser.getAvatar());
            }

            SysUser toUser = userMap.get(msg.getToUserId());
            if (toUser != null) {
                vo.setToUserNickname(toUser.getNickname());
                vo.setToUserAvatar(toUser.getAvatar());
            }

            return vo;
        }).collect(Collectors.toList());

        return new PageVo<>(messageVos, page.getTotal());
    }

    @Override
    public void markAsRead(Integer fromUserId, Integer toUserId) {
        LambdaUpdateWrapper<PrivateMessage> updateWrapper = new LambdaUpdateWrapper<PrivateMessage>()
                .set(PrivateMessage::getIsRead, 1)
                .set(PrivateMessage::getReadTime, new Date())
                .eq(PrivateMessage::getFromUserId, fromUserId)
                .eq(PrivateMessage::getToUserId, toUserId)
                .eq(PrivateMessage::getIsRead, 0);

        privateMessageMapper.update(null, updateWrapper);

        // 更新 Redis 未读数
        String redisKey = RedisConstants.PRIVATE_MESSAGE_UNREAD_COUNT_KEY + toUserId;
        redisUtils.del(redisKey);
    }

    @Override
    public PrivateMessage revokeMessage(Integer messageId, Integer userId) {
        PrivateMessage message = privateMessageMapper.selectById(messageId);

        if (message == null) {
            throw new BlogException(BlogConstants.NotFoundPrivateMessage);
        }

        if (!message.getFromUserId().equals(userId)) {
            throw new BlogException(BlogConstants.CannotRevokeOthersMessage);
        }

        // 检查是否在2分钟内
        long diff = System.currentTimeMillis() - message.getCreateTime().getTime();
        if (diff > 2 * 60 * 1000) {
            throw new BlogException(BlogConstants.MessageRevokeTimeExpired);
        }

        if (message.getIsRevoked() == 1) {
            throw new BlogException(BlogConstants.MessageAlreadyRevoked);
        }

        // 更新为已撤回
        message.setIsRevoked(1);
        privateMessageMapper.updateById(message);

        return message;
    }

    @Override
    public void deleteMessage(Integer messageId, Integer userId) {
        PrivateMessage message = privateMessageMapper.selectById(messageId);

        if (message == null) {
            throw new BlogException(BlogConstants.NotFoundPrivateMessage);
        }

        // 只能删除自己发送或接收的消息
        if (!message.getFromUserId().equals(userId) && !message.getToUserId().equals(userId)) {
            throw new BlogException(BlogConstants.CannotRevokeOthersMessage);
        }

        // 使用 MyBatis-Plus 的逻辑删除
        privateMessageMapper.deleteById(message.getId());
    }

    @Override
    public Integer getUnreadCount(Integer userId) {
        // 先从 Redis 获取
        String redisKey = RedisConstants.PRIVATE_MESSAGE_UNREAD_COUNT_KEY + userId;
        Object cacheObj = redisUtils.get(redisKey);

        if (cacheObj != null) {
            return (Integer) cacheObj;
        }

        // Redis 没有，从数据库查询
        LambdaQueryWrapper<PrivateMessage> qw = new LambdaQueryWrapper<PrivateMessage>()
                .eq(PrivateMessage::getToUserId, userId)
                .eq(PrivateMessage::getIsRead, 0)
                .eq(PrivateMessage::getIsRevoked, 0); // 过滤掉已撤回的消息

        Integer count = Math.toIntExact(privateMessageMapper.selectCount(qw));

        // 缓存到 Redis（30分钟）
        redisUtils.set(redisKey, count, 30, TimeUnit.MINUTES);

        return count;
    }
}
