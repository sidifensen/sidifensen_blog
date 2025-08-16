package com.sidifensen.service.impl;

import com.sidifensen.domain.entity.UserMessage;
import com.sidifensen.mapper.UserMessageMapper;
import com.sidifensen.service.IUserMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author sidifensen
 * @since 2025-08-17
 */
@Service
public class UserMessageServiceImpl extends ServiceImpl<UserMessageMapper, UserMessage> implements IUserMessageService {

}
