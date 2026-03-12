package com.sidifensen.service;

import com.sidifensen.domain.dto.VipAdminMemberAdjustDto;
import com.sidifensen.domain.dto.VipAdminMemberPageDto;
import com.sidifensen.domain.dto.VipAdminOrderPageDto;
import com.sidifensen.domain.dto.VipAdminPlanUpdateDto;
import com.sidifensen.domain.vo.PageVo;
import com.sidifensen.domain.vo.VipAdminDashboardVo;
import com.sidifensen.domain.vo.VipAdminMemberDetailVo;
import com.sidifensen.domain.vo.VipAdminMemberPageVo;
import com.sidifensen.domain.vo.VipAdminOrderPageVo;
import com.sidifensen.domain.vo.VipAdminPlanVo;

import java.util.List;

/**
 * 管理端 VIP 运营台服务。
 */
public interface VipAdminService {

    /**
     * 获取会员经营总览数据。
     */
    VipAdminDashboardVo getDashboard();

    /**
     * 分页查询会员列表。
     */
    PageVo<List<VipAdminMemberPageVo>> pageMembers(VipAdminMemberPageDto vipAdminMemberPageDto);

    /**
     * 获取单个用户的会员详情。
     */
    VipAdminMemberDetailVo getMemberDetail(Integer userId);

    /**
     * 管理端手动调整会员状态。
     */
    void adjustMember(VipAdminMemberAdjustDto vipAdminMemberAdjustDto);

    /**
     * 分页查询会员订单列表。
     */
    PageVo<List<VipAdminOrderPageVo>> pageOrders(VipAdminOrderPageDto vipAdminOrderPageDto);

    /**
     * 获取套餐列表。
     */
    List<VipAdminPlanVo> listPlans();

    /**
     * 修改 VIP 套餐配置。
     */
    void updatePlan(VipAdminPlanUpdateDto vipAdminPlanUpdateDto);
}
