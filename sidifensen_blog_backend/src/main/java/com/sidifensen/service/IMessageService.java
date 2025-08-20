package com.sidifensen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sidifensen.domain.dto.MessageDto;
import com.sidifensen.domain.entity.Message;
import com.sidifensen.domain.vo.MessageVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author sidifensen
 * @since 2025-08-17
 */
public interface IMessageService extends IService<Message> {

    // 发送消息
    void send(MessageDto messageDto);

    // 发送消息给管理员
    void sendToAdmin(MessageDto messageDto);

    // 获取管理员消息数量
    Integer getMessageCount();

    // 获取管理员消息列表
    List<MessageVo> getMessages();

    // 管理员读取消息
    void readMessage(Integer messageId);
}
