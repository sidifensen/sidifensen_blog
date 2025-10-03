package com.sidifensen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sidifensen.domain.entity.Blacklist;

/**
 * <p>
 * 黑名单服务类
 * </p>
 *
 * @author sidifensen
 * @since 2025-10-02
 */
public interface BlacklistService extends IService<Blacklist> {

    /**
     * 添加黑名单记录到数据库
     *
     * @param identifier 用户标识（格式：user:123 或 ip:192.168.1.1）
     * @param reason 拉黑原因
     * @param banDurationSeconds 封禁时长（秒）
     */
    void addToBlacklist(String identifier, String reason, long banDurationSeconds);
}
