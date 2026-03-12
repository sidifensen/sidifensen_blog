package com.sidifensen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sidifensen.domain.entity.VipPlan;

import java.util.List;

/**
 * VIP 套餐服务，统一管理套餐读取规则。
 */
public interface VipPlanService extends IService<VipPlan> {

    /**
     * 查询当前启用的套餐列表，供会员中心展示和下单使用。
     */
    List<VipPlan> listEnabledPlans();

    /**
     * 按套餐编码查询启用中的套餐，仅允许创建新订单使用。
     */
    VipPlan getEnabledPlanByCode(String code);

    /**
     * 按套餐编码查询套餐详情，供历史订单结算兜底使用。
     */
    VipPlan getPlanByCode(String code);
}
