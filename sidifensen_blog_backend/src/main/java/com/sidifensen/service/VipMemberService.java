package com.sidifensen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sidifensen.domain.entity.VipMember;

/**
 * VIP 会员实体服务，负责会员有效期和角色投影同步。
 */
public interface VipMemberService extends IService<VipMember> {

    /**
     * 判断用户当前是否拥有有效 VIP 权限。
     */
    boolean hasVipAccess(Integer userId);

    /**
     * 查询用户的会员记录。
     */
    VipMember getVipMemberByUserId(Integer userId);

    /**
     * 支付成功后开通或续期会员。
     */
    void activateVip(Integer userId, String orderNo, Integer days);

    /**
     * 管理端手动开通会员时，直接用新的有效期覆盖旧记录。
     */
    void overwriteVip(Integer userId, Integer days);

    /**
     * 管理端立即失效会员，同时回收角色投影。
     */
    void expireVipImmediately(Integer userId);

    /**
     * 将已过期会员的状态和角色投影同步为失效。
     */
    void syncExpiredVipRoles();
}
