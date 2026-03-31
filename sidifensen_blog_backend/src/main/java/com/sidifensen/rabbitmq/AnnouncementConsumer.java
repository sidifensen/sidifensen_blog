package com.sidifensen.rabbitmq;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sidifensen.domain.constants.RabbitMQConstants;
import com.sidifensen.domain.dto.AnnouncementSendTask;
import com.sidifensen.domain.entity.Announcement;
import com.sidifensen.domain.entity.Message;
import com.sidifensen.domain.entity.SysUser;
import com.sidifensen.domain.enums.MessageTypeEnum;
import com.sidifensen.mapper.AnnouncementMapper;
import com.sidifensen.mapper.MessageMapper;
import com.sidifensen.mapper.SysUserMapper;
import com.sidifensen.service.MessageService;
import com.sidifensen.utils.EmailUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 公告发送 Consumer
 * 处理 AnnouncementSendTask 消息，批量发送系统通知/邮件
 */
@Component
@Slf4j
@ConditionalOnProperty(name = "rabbitmq.listener.enabled", havingValue = "true", matchIfMissing = true)
public class AnnouncementConsumer {

    private static final int BATCH_SIZE = 500;
    private static final int EMAIL_BATCH_SIZE = 50;
    private static final long EMAIL_INTERVAL_MS = 100;

    @Resource
    private AnnouncementMapper announcementMapper;

    @Resource
    private MessageMapper messageMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private EmailUtils emailUtils;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @RabbitListener(queues = RabbitMQConstants.Announcement_Queue)
    public void handleAnnouncementSend(AnnouncementSendTask task) {
        Integer announcementId = task.getAnnouncementId();
        log.info("开始处理公告发送任务，announcementId={}", announcementId);

        // 幂等检查：查询 announcement 状态
        Announcement announcement = announcementMapper.selectById(announcementId);
        if (announcement == null) {
            log.warn("公告不存在，跳过，announcementId={}", announcementId);
            return;
        }
        if (!announcement.getIsDeleted().equals(0)) {
            log.warn("公告已删除，跳过，announcementId={}", announcementId);
            return;
        }
        if (!announcement.getStatus().equals(0)) {
            log.warn("公告状态非待发送，跳过，announcementId={}, status={}", announcementId, announcement.getStatus());
            return;
        }

        // 更新状态为发送中
        updateStatus(announcementId, 1);

        try {
            boolean hasFailure = false;

            // system 方式：写入 message 表
            if (task.getSendMethods().contains("system")) {
                hasFailure |= sendSystemMessages(task);
            }

            // email 方式：批量发送邮件
            if (task.getSendMethods().contains("email")) {
                hasFailure |= sendEmails(task);
            }

            // 更新最终状态
            if (hasFailure) {
                updateStatus(announcementId, 3);
                log.error("公告发送部分失败，announcementId={}", announcementId);
            } else {
                updateStatus(announcementId, 2);
                log.info("公告发送成功，announcementId={}", announcementId);
            }
        } catch (Exception e) {
            log.error("公告发送异常，announcementId={}", announcementId, e);
            updateStatus(announcementId, 3);
            throw new RuntimeException("公告发送失败：" + e.getMessage(), e);
        }
    }

    /**
     * 发送系统通知（写入 message 表）
     */
    private boolean sendSystemMessages(AnnouncementSendTask task) {
        boolean hasFailure = false;
        Integer lastId = task.getLastId() != null ? task.getLastId() : 0;
        List<Integer> targetUsers = task.getTargetUsers();

        while (true) {
            // 读取目标用户
            List<SysUser> users;
            if (task.getTargetType() == 1) {
                // 全部用户，游标分页
                LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
                wrapper.gt(SysUser::getId, lastId)
                        .orderByAsc(SysUser::getId)
                        .last("LIMIT " + BATCH_SIZE);
                users = sysUserMapper.selectList(wrapper);
            } else {
                // 指定用户，从 targetUsers 列表中取
                users = new ArrayList<>();
                for (Integer userId : targetUsers) {
                    if (userId > lastId) {
                        SysUser user = sysUserMapper.selectById(userId);
                        if (user != null) {
                            users.add(user);
                        }
                        if (users.size() >= BATCH_SIZE) {
                            break;
                        }
                    }
                }
            }

            if (users.isEmpty()) {
                break;
            }

            // 批量写入 message 表
            List<Message> messages = new ArrayList<>();
            for (SysUser user : users) {
                Message message = new Message();
                message.setSenderId(1); // 系统管理员
                message.setReceiverId(user.getId());
                message.setType(0); // 系统通知
                message.setIsRead(0);
                String content = String.format("{\"text\":\"%s\",\"announcementId\":%d}", task.getContent(), task.getAnnouncementId());
                message.setContent(content);
                message.setCreateTime(new Date()); // RabbitMQ 消费者中手动设置时间，MyBatis-Plus 自动填充不生效
                message.setUpdateTime(new Date());
                messages.add(message);
            }

            try {
                for (Message msg : messages) {
                    messageMapper.insert(msg);
                }
            } catch (Exception e) {
                log.error("批量写入 message 表失败，lastId={}", lastId, e);
                hasFailure = true;
            }

            // 更新游标
            lastId = users.get(users.size() - 1).getId();
            task.setLastId(lastId);

            // 如果是全部用户模式且本页不足 BATCH_SIZE，说明已读完
            if (task.getTargetType() == 1 && users.size() < BATCH_SIZE) {
                break;
            }
        }

        return hasFailure;
    }

    /**
     * 发送邮件
     */
    private boolean sendEmails(AnnouncementSendTask task) {
        boolean hasFailure = false;
        Integer lastId = task.getLastId() != null ? task.getLastId() : 0;
        List<Integer> targetUsers = task.getTargetUsers();

        while (true) {
            // 读取目标用户（分批，每批 EMAIL_BATCH_SIZE）
            List<SysUser> users;
            if (task.getTargetType() == 1) {
                LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
                wrapper.gt(SysUser::getId, lastId)
                        .orderByAsc(SysUser::getId)
                        .last("LIMIT " + EMAIL_BATCH_SIZE);
                users = sysUserMapper.selectList(wrapper);
            } else {
                users = new ArrayList<>();
                for (Integer userId : targetUsers) {
                    if (userId > lastId) {
                        SysUser user = sysUserMapper.selectById(userId);
                        if (user != null) {
                            users.add(user);
                        }
                        if (users.size() >= EMAIL_BATCH_SIZE) {
                            break;
                        }
                    }
                }
            }

            if (users.isEmpty()) {
                break;
            }

            // 发送邮件
            for (SysUser user : users) {
                if (user.getEmail() != null && !user.getEmail().isBlank()) {
                    try {
                        emailUtils.sendEmail(user.getEmail(), task.getTitle(), task.getContent());
                    } catch (Exception e) {
                        log.error("发送邮件失败，userId={}, email={}", user.getId(), user.getEmail(), e);
                        hasFailure = true;
                    }
                }
            }

            // 间隔 100ms，防止 SMTP 限流
            try {
                TimeUnit.MILLISECONDS.sleep(EMAIL_INTERVAL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // 更新游标
            lastId = users.get(users.size() - 1).getId();
            task.setLastId(lastId);

            if (task.getTargetType() == 1 && users.size() < EMAIL_BATCH_SIZE) {
                break;
            }
        }

        return hasFailure;
    }

    private void updateStatus(Integer announcementId, Integer status) {
        Announcement update = new Announcement();
        update.setId(announcementId);
        update.setStatus(status);
        announcementMapper.updateById(update);
    }
}
