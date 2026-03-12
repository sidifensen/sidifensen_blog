package com.sidifensen.service;

import com.sidifensen.domain.vo.VipMemberVo;

import java.util.Date;

/**
 * VIP 领域服务，对外提供统一的会员状态与权益读取能力。
 */
public interface VipService {

    /**
     * 判断用户是否拥有有效 VIP 权限。
     */
    boolean hasVipAccess(Integer userId);

    /**
     * 获取用户当前会员状态。
     */
    String getVipStatus(Integer userId);

    /**
     * 获取用户当前会员到期时间。
     */
    Date getVipExpireTime(Integer userId);

    /**
     * 获取用户当前 AI 日额度。
     */
    int getDailyAiQuota(Integer userId);

    /**
     * 聚合当前用户的会员中心展示数据。
     */
    VipMemberVo getCurrentVipMemberInfo(Integer userId);
}
