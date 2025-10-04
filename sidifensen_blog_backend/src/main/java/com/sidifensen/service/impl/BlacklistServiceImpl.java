package com.sidifensen.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.dto.BlacklistAddDto;
import com.sidifensen.domain.dto.BlacklistSearchDto;
import com.sidifensen.domain.dto.BlacklistUpdateDto;
import com.sidifensen.domain.entity.Blacklist;
import com.sidifensen.domain.enums.BlacklistTypeEnum;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.BlacklistMapper;
import com.sidifensen.service.BlacklistService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 黑名单服务实现类
 * </p>
 *
 * @author sidifensen
 * @since 2025-10-02
 */
@Service
@Slf4j
public class BlacklistServiceImpl extends ServiceImpl<BlacklistMapper, Blacklist> implements BlacklistService {

    @Override
    public void addToBlacklist(String identifier, String reason, long banDurationSeconds) {
        try {
            Blacklist blacklist = new Blacklist();

            // 解析用户标识，判断是用户类型还是IP类型
            if (identifier.startsWith("user:")) {
                // 用户类型
                blacklist.setType(BlacklistTypeEnum.USER.getCode());
                String userIdStr = identifier.substring(5); // 去掉 "user:" 前缀
                blacklist.setUserId(Integer.parseInt(userIdStr));
            } else if (identifier.startsWith("ip:")) {
                // IP类型
                blacklist.setType(BlacklistTypeEnum.IP.getCode());
                String ip = identifier.substring(3); // 去掉 "ip:" 前缀
                blacklist.setIp(ip);
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

            log.info("黑名单记录已保存到数据库 - 标识: {}, 原因: {}, 封禁时长: {}秒, 到期时间: {}",
                    identifier, reason, banDurationSeconds, blacklist.getExpireTime());
        } catch (Exception e) {
            log.error("保存黑名单记录失败 - 标识: {}, 原因: {}", identifier, reason, e);
        }
    }

    @Override
    public List<Blacklist> adminGetBlacklistList() {
        try {
            // 获取所有黑名单记录，按封禁时间倒序
            LambdaQueryWrapper<Blacklist> queryWrapper = new LambdaQueryWrapper<Blacklist>()
                    .eq(Blacklist::getIsDeleted, 0)
                    .orderByDesc(Blacklist::getBanTime);

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
            List<Blacklist> blacklistList = new ArrayList<>();

            for (Integer userId : blacklistAddDto.getUserIds()) {
                Blacklist blacklist = new Blacklist();
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
    public List<Blacklist> adminSearchBlacklist(BlacklistSearchDto blacklistSearchDto) {
        try {
            // 构建查询条件
            LambdaQueryWrapper<Blacklist> queryWrapper = new LambdaQueryWrapper<Blacklist>()
                    .eq(Blacklist::getIsDeleted, 0)
                    .orderByDesc(Blacklist::getCreateTime);

            // 如果传了用户id，设置黑名单类型为用户
            if (blacklistSearchDto.getUserId() != null && blacklistSearchDto.getUserId() > 0) {
                queryWrapper.eq(Blacklist::getType, BlacklistTypeEnum.USER.getCode())
                        .eq(Blacklist::getUserId, blacklistSearchDto.getUserId());
            } else {
                // 根据黑名单类型筛选
                queryWrapper.eq(blacklistSearchDto.getType() != null, Blacklist::getType, blacklistSearchDto.getType());
            }

            // 根据拉黑时间范围筛选
            queryWrapper.ge(blacklistSearchDto.getBanTimeStart() != null, Blacklist::getBanTime, blacklistSearchDto.getBanTimeStart())
                    .le(blacklistSearchDto.getBanTimeEnd() != null, Blacklist::getBanTime, blacklistSearchDto.getBanTimeEnd());

            // 根据到期时间范围筛选
            queryWrapper.ge(blacklistSearchDto.getExpireTimeStart() != null, Blacklist::getExpireTime, blacklistSearchDto.getExpireTimeStart())
                    .le(blacklistSearchDto.getExpireTimeEnd() != null, Blacklist::getExpireTime, blacklistSearchDto.getExpireTimeEnd());

            List<Blacklist> blacklistList = list(queryWrapper);
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
            Blacklist blacklist = this.getById(blacklistUpdateDto.getId());
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

            // 批量删除（逻辑删除）
            boolean result = this.removeByIds(blacklistIds);
            if (!result) {
                throw new BlogException(BlogConstants.DeleteBlacklistError);
            }
        } catch (BlogException e) {
            throw e;
        } catch (Exception e) {
            log.error("管理员批量删除黑名单失败", e);
            throw new BlogException(BlogConstants.DeleteBlacklistError);
        }
    }
}
