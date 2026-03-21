package com.sidifensen.controller;

import com.sidifensen.aspect.OperationLog;
import com.sidifensen.domain.dto.VipAdminMemberAdjustDto;
import com.sidifensen.domain.dto.VipAdminMemberPageDto;
import com.sidifensen.domain.dto.VipAdminOrderPageDto;
import com.sidifensen.domain.dto.VipAdminPlanUpdateDto;
import com.sidifensen.domain.enums.OperationTypeEnum;
import com.sidifensen.domain.result.Result;
import com.sidifensen.domain.vo.PageVo;
import com.sidifensen.domain.vo.VipAdminDashboardVo;
import com.sidifensen.domain.vo.VipAdminMemberDetailVo;
import com.sidifensen.domain.vo.VipAdminMemberPageVo;
import com.sidifensen.domain.vo.VipAdminOrderPageVo;
import com.sidifensen.domain.vo.VipAdminPlanVo;
import com.sidifensen.service.VipAdminService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理端会员管理接口，负责会员运营总览、会员调整、订单和套餐配置。
 */
@Validated
@RestController
@RequestMapping("/vip/admin")
public class VipAdminController {

    @Resource
    private VipAdminService vipAdminService;

    /**
     * 获取会员经营总览。
     */
    @OperationLog(module = "会员管理", type = OperationTypeEnum.GET, description = "管理员获取会员经营总览")
    @PreAuthorize("hasAuthority('system:vip:dashboard')")
    @GetMapping("/dashboard")
    public Result<VipAdminDashboardVo> getDashboard() {
        // 仪表盘涉及会员、订单两类口径，统一交给 service 聚合，控制器只负责鉴权入口。
        return Result.success(vipAdminService.getDashboard());
    }

    /**
     * 分页查询会员列表。
     */
    @OperationLog(module = "会员管理", type = OperationTypeEnum.SELECT, description = "管理员分页查询会员列表")
    @PreAuthorize("hasAuthority('system:vip:member:list')")
    @GetMapping("/member/page")
    public Result<PageVo<List<VipAdminMemberPageVo>>> pageMembers(@Valid VipAdminMemberPageDto vipAdminMemberPageDto) {
        // 会员列表需要拼装用户、会员和最近订单快照，controller 不直接分散查询。
        return Result.success(vipAdminService.pageMembers(vipAdminMemberPageDto));
    }

    /**
     * 获取单个会员详情。
     */
    @OperationLog(module = "会员管理", type = OperationTypeEnum.GET, description = "管理员获取会员详情")
    @PreAuthorize("hasAuthority('system:vip:member:list')")
    @GetMapping("/member/{userId}")
    public Result<VipAdminMemberDetailVo> getMemberDetail(@PathVariable @NotNull(message = "用户ID不能为空") Integer userId) {
        // 详情页复用聚合服务，保证列表与详情的会员状态口径一致。
        return Result.success(vipAdminService.getMemberDetail(userId));
    }

    /**
     * 管理端手动调整会员状态。
     */
    @OperationLog(module = "会员管理", type = OperationTypeEnum.UPDATE, description = "管理员调整会员状态")
    @PreAuthorize("hasAuthority('system:vip:member:update')")
    @PostMapping("/member/adjust")
    public Result<?> adjustMember(@Valid @RequestBody VipAdminMemberAdjustDto vipAdminMemberAdjustDto) {
        // 手动调整涉及参数校验、事务和会员数据更新，统一在 service 中处理。
        vipAdminService.adjustMember(vipAdminMemberAdjustDto);
        return Result.success();
    }

    /**
     * 分页查询会员订单。
     */
    @OperationLog(module = "会员管理", type = OperationTypeEnum.SELECT, description = "管理员分页查询会员订单")
    @PreAuthorize("hasAuthority('system:vip:order:list')")
    @GetMapping("/order/page")
    public Result<PageVo<List<VipAdminOrderPageVo>>> pageOrders(@Valid VipAdminOrderPageDto vipAdminOrderPageDto) {
        // 订单页统一复用后台订单筛选口径，避免 controller 层重复写查询条件。
        return Result.success(vipAdminService.pageOrders(vipAdminOrderPageDto));
    }

    /**
     * 获取套餐列表。
     */
    @OperationLog(module = "会员管理", type = OperationTypeEnum.SELECT, description = "管理员查询会员套餐")
    @PreAuthorize("hasAuthority('system:vip:plan:list')")
    @GetMapping("/plan/list")
    public Result<List<VipAdminPlanVo>> listPlans() {
        // 套餐管理页需要看到启停状态和完整配置，因此交给后台聚合服务返回。
        return Result.success(vipAdminService.listPlans());
    }

    /**
     * 修改套餐配置。
     */
    @OperationLog(module = "会员管理", type = OperationTypeEnum.UPDATE, description = "管理员修改会员套餐")
    @PreAuthorize("hasAuthority('system:vip:plan:update')")
    @PostMapping("/plan/update")
    public Result<?> updatePlan(@Valid @RequestBody VipAdminPlanUpdateDto vipAdminPlanUpdateDto) {
        // 套餐修改只开放受控字段，具体覆盖规则在 service 层统一维护。
        vipAdminService.updatePlan(vipAdminPlanUpdateDto);
        return Result.success();
    }
}
