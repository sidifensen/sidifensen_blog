package com.sidifensen.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.entity.Blacklist;
import com.sidifensen.mapper.BlacklistMapper;
import com.sidifensen.service.BlacklistService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

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
                blacklist.setType(0);
                String userIdStr = identifier.substring(5); // 去掉 "user:" 前缀
                blacklist.setUserId(Integer.parseInt(userIdStr));
            } else if (identifier.startsWith("ip:")) {
                // IP类型
                blacklist.setType(1);
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
            blacklist.setCreateTime(now);
            blacklist.setUpdateTime(now);
            blacklist.setIsDeleted(0);

            // 保存到数据库
            this.save(blacklist);

            log.info("黑名单记录已保存到数据库 - 标识: {}, 原因: {}, 封禁时长: {}秒, 到期时间: {}", 
                    identifier, reason, banDurationSeconds, blacklist.getExpireTime());
        } catch (Exception e) {
            log.error("保存黑名单记录失败 - 标识: {}, 原因: {}", identifier, reason, e);
        }
    }
}
