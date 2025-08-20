package com.sidifensen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.dto.MessageDto;
import com.sidifensen.domain.entity.Message;
import com.sidifensen.domain.vo.MessageVo;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.MessageMapper;
import com.sidifensen.service.IMessageService;
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
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements IMessageService {

    @Resource
    private MessageMapper messageMapper;

    @Override
    public void send(MessageDto messageDto) {
        Message message = BeanUtil.copyProperties(messageDto, Message.class);
        int save = messageMapper.insert(message);
        if (save <= 0) {
            throw new BlogException(BlogConstants.CannotSaveMessage);
        }
    }

    // 发送给管理员
    @Override
    public void sendToAdmin(MessageDto messageDto) {
        Message message = BeanUtil.copyProperties(messageDto, Message.class);
        boolean save = this.save(message);
        if (!save) {
            throw new BlogException(BlogConstants.CannotSaveMessage);
        }

    }

    @Override
    public Integer getMessageCount() {
        // 统计未读消息数量
        Long count = messageMapper.selectCount(new LambdaQueryWrapper<Message>().eq(Message::getIsRead, 0));

        return count.intValue();
    }

    @Override
    public List<MessageVo> getMessages() {
        List<Message> messages = this.list(new LambdaQueryWrapper<Message>()
                .orderByDesc(Message::getCreateTime));
        if (ObjectUtil.isNotEmpty(messages)) {
            List<MessageVo> messageVos = BeanUtil.copyToList(messages, MessageVo.class);
            return messageVos;
        }
        return List.of();
    }

    @Override
    public void readMessage(Integer messageId) {
        LambdaQueryWrapper<Message> eq = new LambdaQueryWrapper<Message>()
                .eq(Message::getId, messageId);
        // 读取消息
        Message message = messageMapper.selectOne(eq);
        if (ObjectUtil.isNotEmpty(message)) {
            messageMapper.updateById(message);
        } else {
            throw new BlogException(BlogConstants.CannotReadMessage);
        }
    }

}
