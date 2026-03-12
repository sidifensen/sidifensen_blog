package com.sidifensen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.entity.VipPlan;
import com.sidifensen.mapper.VipPlanMapper;
import com.sidifensen.service.VipPlanService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * VIP 套餐实现，统一从数据库读取会员价格和时长配置。
 */
@Service
@Slf4j
public class VipPlanServiceImpl extends ServiceImpl<VipPlanMapper, VipPlan> implements VipPlanService {

    @Resource
    private VipPlanMapper vipPlanMapper;

    /**
     * 会员中心只展示启用中的套餐，并按时长从短到长排序。
     */
    @Override
    public List<VipPlan> listEnabledPlans() {
        return vipPlanMapper.selectList(new LambdaQueryWrapper<VipPlan>()
                .eq(VipPlan::getEnabled, true)
                .orderByAsc(VipPlan::getDays)
                .orderByAsc(VipPlan::getId));
    }

    /**
     * 新下单只能命中启用套餐，避免下线套餐继续被购买。
     */
    @Override
    public VipPlan getEnabledPlanByCode(String code) {
        if (!StringUtils.hasText(code)) {
            return null;
        }
        return vipPlanMapper.selectOne(new LambdaQueryWrapper<VipPlan>()
                .eq(VipPlan::getCode, code)
                .eq(VipPlan::getEnabled, true)
                .last("limit 1"));
    }

    /**
     * 支付回调需要允许读取历史套餐，避免套餐下线后旧订单无法发放权益。
     */
    @Override
    public VipPlan getPlanByCode(String code) {
        if (!StringUtils.hasText(code)) {
            return null;
        }
        return vipPlanMapper.selectOne(new LambdaQueryWrapper<VipPlan>()
                .eq(VipPlan::getCode, code)
                .last("limit 1"));
    }
}
