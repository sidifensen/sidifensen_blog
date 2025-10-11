package com.sidifensen.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.constants.RabbitMQConstants;
import com.sidifensen.domain.dto.BlacklistAddDto;
import com.sidifensen.domain.dto.BlacklistSearchDto;
import com.sidifensen.domain.dto.BlacklistUpdateDto;
import com.sidifensen.domain.dto.MessageDto;
import com.sidifensen.domain.entity.SysBlacklist;
import com.sidifensen.domain.enums.BlacklistTypeEnum;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.SysBlacklistMapper;
import com.sidifensen.redis.RedisComponent;
import com.sidifensen.service.MessageService;
import com.sidifensen.service.SysBlacklistService;
import com.sidifensen.utils.IpUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 系统黑名单服务实现类
 * </p>
 *
 * @author sidifensen
 * @since 2025-10-02
 */
@Service
@Slf4j
public class SysBlacklistServiceImpl extends ServiceImpl<SysBlacklistMapper, SysBlacklist> implements SysBlacklistService {

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private MessageService messageService;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private IpUtils ipUtils;

    @Override
    public void addToBlacklist(String identifier, String reason, long banDurationSeconds) {
        try {
            SysBlacklist blacklist = new SysBlacklist();
            String userId = "未知";
            String ip = "未知";

            // 解析用户标识，判断是用户类型还是IP类型
            if (identifier.startsWith("user:")) {
                // 用户类型
                blacklist.setType(BlacklistTypeEnum.USER.getCode());
                String userIdStr = identifier.substring(5); // 去掉 "user:" 前缀
                blacklist.setUserId(Integer.parseInt(userIdStr));
                userId = userIdStr;
            } else if (identifier.startsWith("ip:")) {
                // IP类型
                blacklist.setType(BlacklistTypeEnum.IP.getCode());
                String ipStr = identifier.substring(3); // 去掉 "ip:" 前缀
                blacklist.setIp(ipStr);
                ip = ipStr;
            } else {
                log.error("无效的用户标识格式: {}", identifier);
                return;
            }

            // 设置拉黑信息
            blacklist.setReason(reason);
            Date now = new Date();
            blacklist.setBanTime(now);
            blacklist.setExpireTime(new Date(now.getTime() + banDurationSeconds * 1000));

            // 保存到数据库
            this.save(blacklist);

            // 获取IP地址信息
            String address = "未知地址";
            if (!ip.equals("未知")) {
                try {
                    address = ipUtils.getAddress(ip);
                } catch (Exception e) {
                    log.error("获取IP地址信息失败: {}", ip, e);
                }
            }

            // 格式化封禁时长
            String banDuration = formatBanDuration(banDurationSeconds);

            // 格式化拉黑时间
            String banTime = DateUtil.format(now, "yyyy-MM-dd HH:mm:ss");

            // 发送站内消息给管理员
            try {
                MessageDto messageDto = new MessageDto();
                messageDto.setType(0); // 系统消息
                messageDto.setIsRead(0); // 未读
                messageDto.setContent(String.format(
                        "黑名单拉黑通知：用户ID为 %s，IP为 %s，地址为 %s 的用户因 %s 被拉入黑名单，封禁时长：%s",
                        userId, ip, address, reason, banDuration
                ));
                messageService.sendToAdmin(messageDto);
            } catch (Exception e) {
                log.error("发送站内消息给管理员失败", e);
            }

            // 发送 RabbitMQ 消息（用于邮件通知）
            try {
                Map<String, Object> emailMessage = new HashMap<>();
                emailMessage.put("userId", userId);
                emailMessage.put("ip", ip);
                emailMessage.put("address", address);
                emailMessage.put("reason", reason);
                emailMessage.put("banDuration", banDuration);
                emailMessage.put("banTime", banTime);

                rabbitTemplate.convertAndSend(
                        RabbitMQConstants.Blacklist_Exchange,
                        RabbitMQConstants.Blacklist_Routing_Key,
                        emailMessage
                );
            } catch (Exception e) {
                log.error("发送 RabbitMQ 消息失败", e);
            }

        } catch (Exception e) {
            log.error("保存黑名单记录失败 - 标识: {}, 原因: {}", identifier, reason, e);
        }
    }

    /**
     * 格式化封禁时长
     * @param banDurationSeconds 封禁时长（秒）
     * @return 格式化后的时长字符串
     */
    private String formatBanDuration(long banDurationSeconds) {
        if (banDurationSeconds < 60) {
            return banDurationSeconds + "秒";
        } else if (banDurationSeconds < 3600) {
            return (banDurationSeconds / 60) + "分钟";
        } else if (banDurationSeconds < 86400) {
            return (banDurationSeconds / 3600) + "小时";
        } else {
            return (banDurationSeconds / 86400) + "天";
        }
    }

    @Override
    public List<SysBlacklist> adminGetBlacklistList() {
        try {
            // 获取所有黑名单记录，按封禁时间倒序
            LambdaQueryWrapper<SysBlacklist> queryWrapper = new LambdaQueryWrapper<SysBlacklist>()
                    .eq(SysBlacklist::getIsDeleted, 0)
                    .orderByDesc(SysBlacklist::getBanTime);

            return list(queryWrapper);
        } catch (Exception e) {
            log.error("管理员获取黑名单列表失败", e);
            throw new BlogException(BlogConstants.IllegalRequest);
        }
    }

    @Override
    public void adminAddBlacklist(BlacklistAddDto blacklistAddDto) {
        try {
            // 参数校验
            if (ObjectUtil.isEmpty(blacklistAddDto.getUserIds())) {
                throw new BlogException(BlogConstants.UserIdsRequired);
            }
            if (ObjectUtil.isEmpty(blacklistAddDto.getReason())) {
                throw new BlogException(BlogConstants.BlacklistReasonRequired);
            }
            if (ObjectUtil.isEmpty(blacklistAddDto.getExpireTime())) {
                throw new BlogException(BlogConstants.BlacklistExpireTimeRequired);
            }

            // 批量创建黑名单记录
            Date now = new Date();
            List<SysBlacklist> blacklistList = new ArrayList<>();

            for (Integer userId : blacklistAddDto.getUserIds()) {
                SysBlacklist blacklist = new SysBlacklist();
                blacklist.setType(BlacklistTypeEnum.USER.getCode()); // 用户类型
                blacklist.setUserId(userId);
                blacklist.setReason(blacklistAddDto.getReason());
                blacklist.setBanTime(now);
                blacklist.setExpireTime(blacklistAddDto.getExpireTime());
                blacklistList.add(blacklist);
            }

            // 批量保存
            boolean result = this.saveBatch(blacklistList);
            if (!result) {
                throw new BlogException(BlogConstants.AddBlacklistError);
            }
        } catch (Exception e) {
            log.error("管理员批量添加黑名单失败", e);
            throw new BlogException(BlogConstants.AddBlacklistError);
        }
    }

    @Override
    public List<SysBlacklist> adminSearchBlacklist(BlacklistSearchDto blacklistSearchDto) {
        try {
            // 构建查询条件
            LambdaQueryWrapper<SysBlacklist> queryWrapper = new LambdaQueryWrapper<SysBlacklist>()
                    .eq(SysBlacklist::getIsDeleted, 0)
                    .orderByDesc(SysBlacklist::getCreateTime);

            // 如果传了用户id，设置黑名单类型为用户
            if (blacklistSearchDto.getUserId() != null && blacklistSearchDto.getUserId() > 0) {
                queryWrapper.eq(SysBlacklist::getType, BlacklistTypeEnum.USER.getCode())
                        .eq(SysBlacklist::getUserId, blacklistSearchDto.getUserId());
            } else {
                // 根据黑名单类型筛选
                queryWrapper.eq(blacklistSearchDto.getType() != null, SysBlacklist::getType, blacklistSearchDto.getType());
            }

            // 根据拉黑时间范围筛选
            queryWrapper.ge(blacklistSearchDto.getBanTimeStart() != null, SysBlacklist::getBanTime, blacklistSearchDto.getBanTimeStart())
                    .le(blacklistSearchDto.getBanTimeEnd() != null, SysBlacklist::getBanTime, blacklistSearchDto.getBanTimeEnd());

            // 根据到期时间范围筛选
            queryWrapper.ge(blacklistSearchDto.getExpireTimeStart() != null, SysBlacklist::getExpireTime, blacklistSearchDto.getExpireTimeStart())
                    .le(blacklistSearchDto.getExpireTimeEnd() != null, SysBlacklist::getExpireTime, blacklistSearchDto.getExpireTimeEnd());

            List<SysBlacklist> blacklistList = list(queryWrapper);
            return blacklistList;
        } catch (Exception e) {
            log.error("管理员搜索黑名单失败", e);
            throw new BlogException(BlogConstants.IllegalRequest);
        }
    }

    @Override
    public void adminUpdateBlacklist(BlacklistUpdateDto blacklistUpdateDto) {
        try {
            // 参数校验
            if (ObjectUtil.isEmpty(blacklistUpdateDto.getId())) {
                throw new BlogException(BlogConstants.BlacklistIdRequired);
            }

            // 检查黑名单记录是否存在
            SysBlacklist blacklist = this.getById(blacklistUpdateDto.getId());
            if (ObjectUtil.isEmpty(blacklist)) {
                throw new BlogException(BlogConstants.NotFoundBlacklist);
            }

            // 更新黑名单信息
            if (ObjectUtil.isNotEmpty(blacklistUpdateDto.getReason())) {
                blacklist.setReason(blacklistUpdateDto.getReason());
            }
            if (ObjectUtil.isNotEmpty(blacklistUpdateDto.getExpireTime())) {
                blacklist.setExpireTime(blacklistUpdateDto.getExpireTime());
            }

            boolean result = this.updateById(blacklist);
            if (!result) {
                throw new BlogException(BlogConstants.UpdateBlacklistError);
            }
        } catch (BlogException e) {
            throw e;
        } catch (Exception e) {
            log.error("管理员更新黑名单失败", e);
            throw new BlogException(BlogConstants.UpdateBlacklistError);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adminDeleteBlacklist(List<Integer> blacklistIds) {
        try {
            // 参数校验
            if (ObjectUtil.isEmpty(blacklistIds)) {
                throw new BlogException(BlogConstants.BlacklistIdsRequired);
            }

            // 在删除数据库记录前，先获取黑名单信息用于删除Redis缓存
            List<SysBlacklist> blacklistList = this.listByIds(blacklistIds);
            if (ObjectUtil.isEmpty(blacklistList)) {
                log.warn("未找到要删除的黑名单记录，ID列表: {}", blacklistIds);
                return;
            }

            // 批量删除（逻辑删除）
            boolean result = this.removeByIds(blacklistIds);
            if (!result) {
                throw new BlogException(BlogConstants.DeleteBlacklistError);
            }

            // 删除数据库记录成功后，删除Redis中的黑名单缓存
            redisComponent.batchRemoveBlacklistFromRedis(blacklistList);

        } catch (BlogException e) {
            throw e;
        } catch (Exception e) {
            log.error("管理员批量删除黑名单失败", e);
            throw new BlogException(BlogConstants.DeleteBlacklistError);
        }
    }
}

