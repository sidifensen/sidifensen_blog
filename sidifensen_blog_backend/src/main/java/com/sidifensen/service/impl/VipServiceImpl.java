package com.sidifensen.service.impl;

import com.sidifensen.domain.entity.VipMember;
import com.sidifensen.domain.enums.VipMemberStatusEnum;
import com.sidifensen.domain.vo.VipMemberVo;
import com.sidifensen.service.AiUsageService;
import com.sidifensen.service.VipMemberService;
import com.sidifensen.service.VipService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * VIP 聚合服务，为用户态和文章权限提供统一读模型。
 */
@Service
public class VipServiceImpl implements VipService {

    @Resource
    private VipMemberService vipMemberService;

    @Resource
    private AiUsageService aiUsageService;

    /**
     * 统一透出会员可访问判断，避免各业务层重复读表。
     */
    @Override
    public boolean hasVipAccess(Integer userId) {
        return vipMemberService.hasVipAccess(userId);
    }

    /**
     * 会员状态展示和值机逻辑解耦，便于前端直接使用。
     */
    @Override
    public String getVipStatus(Integer userId) {
        // 先读取会员真相源，没有记录时前端直接按“未开通”展示。
        VipMember vipMember = vipMemberService.getVipMemberByUserId(userId);
        if (vipMember == null) {
            return VipMemberStatusEnum.NONE.getCode();
        }
        // 有记录时仍按当前时间重新判断，避免定时任务延迟导致状态展示不准。
        if (vipMember.getExpireTime() != null && vipMember.getExpireTime().after(new Date())) {
            return VipMemberStatusEnum.ACTIVE.getCode();
        }
        return VipMemberStatusEnum.EXPIRED.getCode();
    }

    /**
     * 到期时间为空时表示未开通过会员。
     */
    @Override
    public Date getVipExpireTime(Integer userId) {
        // 会员到期时间直接复用真相源字段，不在聚合层二次推导。
        VipMember vipMember = vipMemberService.getVipMemberByUserId(userId);
        return vipMember == null ? null : vipMember.getExpireTime();
    }

    /**
     * AI 日额度与会员状态绑定，由这里统一对外返回。
     */
    @Override
    public int getDailyAiQuota(Integer userId) {
        return aiUsageService.getDailyLimit(userId);
    }

    /**
     * 会员中心一次性需要的展示信息在这里聚合完成。
     */
    @Override
    public VipMemberVo getCurrentVipMemberInfo(Integer userId) {
        VipMemberVo vipMemberVo = new VipMemberVo();
        // 会员中心页面一次性需要状态、到期时间和额度信息，这里统一聚合后返回。
        vipMemberVo.setIsVip(hasVipAccess(userId));
        vipMemberVo.setVipStatus(getVipStatus(userId));
        vipMemberVo.setVipExpireTime(getVipExpireTime(userId));
        vipMemberVo.setAiDailyQuota(getDailyAiQuota(userId));
        // 剩余额度按当前用户实时计算，避免前端再发二次请求。
        vipMemberVo.setAiRemainingQuota(aiUsageService.getRemainingQuota(userId));
        return vipMemberVo;
    }
}
