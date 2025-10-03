package com.sidifensen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sidifensen.domain.dto.BlacklistAddDto;
import com.sidifensen.domain.dto.BlacklistSearchDto;
import com.sidifensen.domain.dto.BlacklistUpdateDto;
import com.sidifensen.domain.entity.Blacklist;

import java.util.List;

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

    /**
     * 管理员获取黑名单列表
     *
     * @return 黑名单列表
     */
    List<Blacklist> adminGetBlacklistList();

    /**
     * 管理员批量新增黑名单用户
     *
     * @param blacklistAddDto 黑名单新增信息
     */
    void adminAddBlacklist(BlacklistAddDto blacklistAddDto);

    /**
     * 管理员搜索黑名单
     *
     * @param blacklistSearchDto 搜索条件
     * @return 黑名单列表
     */
    List<Blacklist> adminSearchBlacklist(BlacklistSearchDto blacklistSearchDto);

    /**
     * 管理员修改黑名单
     *
     * @param blacklistUpdateDto 黑名单修改信息
     */
    void adminUpdateBlacklist(BlacklistUpdateDto blacklistUpdateDto);

    /**
     * 管理员批量删除黑名单
     *
     * @param blacklistIds 黑名单ID列表
     */
    void adminDeleteBlacklist(List<Integer> blacklistIds);
}
