package com.sidifensen.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.constants.RabbitMQConstants;
import com.sidifensen.domain.dto.AnnouncementSendTask;
import com.sidifensen.domain.entity.Announcement;
import com.sidifensen.domain.vo.AnnouncementVo;
import com.sidifensen.domain.vo.PageVo;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.AnnouncementMapper;
import com.sidifensen.service.AnnouncementService;
import com.sidifensen.utils.XssUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 公告 Service 实现
 */
@Service
@Slf4j
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements AnnouncementService {

    private static final String ANNOUNCEMENT_READ_KEY_PREFIX = "announcement:read:";

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public PageVo<List<AnnouncementVo>> getAnnouncementPage(Integer status, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Announcement::getStatus, status);
        }
        wrapper.orderByDesc(Announcement::getCreateTime);
        IPage<Announcement> page = new Page<>(pageNum, pageSize);
        IPage<Announcement> result = this.page(page, wrapper);
        List<AnnouncementVo> vos = JSON.parseArray(JSON.toJSONString(result.getRecords()), AnnouncementVo.class);
        PageVo<List<AnnouncementVo>> pageVo = new PageVo<>();
        pageVo.setData(vos);
        pageVo.setTotal(result.getTotal());
        return pageVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createAnnouncement(Announcement announcement) {
        // XSS 过滤：防止恶意脚本注入
        announcement.setTitle(XssUtils.cleanPlainText(announcement.getTitle()));
        announcement.setContent(XssUtils.cleanRichText(announcement.getContent()));

        List<String> sendMethods = JSON.parseArray(announcement.getSendMethod(), String.class);
        boolean hasSystemOrEmail = sendMethods.contains("system") || sendMethods.contains("email");

        if (hasSystemOrEmail) {
            // 需要异步发送，先保存为待发送状态
            announcement.setStatus(0);
            this.save(announcement);

            // 投递 MQ 任务
            AnnouncementSendTask task = new AnnouncementSendTask();
            task.setAnnouncementId(announcement.getId());
            task.setTitle(announcement.getTitle());
            task.setContent(announcement.getContent());
            task.setSendMethods(sendMethods);
            task.setTargetType(announcement.getTargetType());
            task.setTargetUsers(JSON.parseArray(announcement.getTargetUsers(), Integer.class));
            task.setLastId(0);

            try {
                rabbitTemplate.convertAndSend(
                        RabbitMQConstants.Announcement_Exchange,
                        RabbitMQConstants.Announcement_Routing_Key,
                        task);
            } catch (Exception e) {
                log.error("公告发送任务投递MQ失败，announcementId={}", announcement.getId(), e);
                // 更新状态为发送失败
                LambdaUpdateWrapper<Announcement> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(Announcement::getId, announcement.getId())
                        .set(Announcement::getStatus, 3);
                this.update(updateWrapper);
            }
        } else {
            // 仅 website 方式，直接标记为已发送
            announcement.setStatus(2);
            announcement.setSendTime(new Date());
            this.save(announcement);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelAnnouncement(Integer id) {
        Announcement announcement = this.getById(id);
        if (announcement == null) {
            throw new BlogException("公告不存在");
        }
        if (!announcement.getStatus().equals(0)) {
            throw new BlogException("只能取消待发送状态的通知");
        }
        LambdaUpdateWrapper<Announcement> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Announcement::getId, id)
                .set(Announcement::getStatus, 3);
        this.update(updateWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAnnouncement(Integer id) {
        Announcement announcement = this.getById(id);
        if (announcement == null) {
            throw new BlogException("公告不存在");
        }
        if (announcement.getStatus().equals(1)) {
            throw new BlogException("发送中的公告无法删除");
        }
        this.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void readAnnouncement(Integer id, Integer readerId) {
        String key = ANNOUNCEMENT_READ_KEY_PREFIX + readerId;
        try {
            redisTemplate.opsForSet().add(key, id);
        } catch (Exception e) {
            log.error("标记公告已读失败，readerId={}, announcementId={}", readerId, id, e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Integer> getReadAnnouncementIds(Integer readerId) {
        String key = ANNOUNCEMENT_READ_KEY_PREFIX + readerId;
        try {
            Set<Object> members = redisTemplate.opsForSet().members(key);
            if (members == null || members.isEmpty()) {
                return List.of();
            }
            List<Integer> readIds = members.stream()
                    .map(m -> (Integer) m)
                    .toList();
            return readIds;
        } catch (Exception e) {
            log.error("获取已读公告ID列表失败，readerId={}", readerId, e);
            return List.of();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAnnouncement(Announcement announcement) {
        Announcement existing = this.getById(announcement.getId());
        if (existing == null) {
            throw new BlogException("公告不存在");
        }
        // XSS 过滤：防止恶意脚本注入
        announcement.setTitle(XssUtils.cleanPlainText(announcement.getTitle()));
        announcement.setContent(XssUtils.cleanRichText(announcement.getContent()));
        // 更新公告信息
        this.updateById(announcement);
    }
}
