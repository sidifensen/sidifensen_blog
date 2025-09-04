package com.sidifensen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.dto.MessageDto;
import com.sidifensen.domain.entity.Message;
import com.sidifensen.domain.vo.MessageVo;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.MessageMapper;
import com.sidifensen.service.MessageService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author sidifensen
 * @since 2025-08-17
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Resource
    private MessageMapper messageMapper;

    // 发送消息
    @Override
    public void send(MessageDto messageDto) {
        Message message = BeanUtil.copyProperties(messageDto, Message.class);
        int save = messageMapper.insert(message);
        if (save <= 0) {
            throw new BlogException(BlogConstants.SaveMessageError);
        }
    }

    // 给用户发送消息
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

}
