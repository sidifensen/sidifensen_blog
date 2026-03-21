package com.sidifensen.controller;

import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.dto.CreateVipOrderDto;
import com.sidifensen.domain.result.Result;
import com.sidifensen.domain.vo.PageVo;
import com.sidifensen.domain.vo.VipMemberVo;
import com.sidifensen.domain.vo.VipOrderCreateVo;
import com.sidifensen.domain.vo.VipOrderVo;
import com.sidifensen.domain.vo.VipPlanVo;
import com.sidifensen.exception.BlogException;
import com.sidifensen.service.PayOrderService;
import com.sidifensen.service.VipService;
import com.sidifensen.utils.SecurityUtils;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * VIP 会员中心接口，负责套餐、订单和会员状态查询。
 */
@Validated
@RestController
@RequestMapping("/vip")
public class VipController {

    @Resource
    private PayOrderService payOrderService;

    @Resource
    private VipService vipService;

    /**
     * 获取当前启用的 VIP 套餐列表。
     */
    @GetMapping("/plans")
    public Result<List<VipPlanVo>> listPlans() {
        // 套餐展示统一复用支付域的套餐查询口径，避免会员中心和支付下单看到不同配置。
        return Result.success(payOrderService.listVipPlans());
    }

    /**
     * 创建一笔 VIP 会员订单，并返回支付宝跳转参数。
     */
    @PostMapping("/orders")
    public Result<VipOrderCreateVo> createOrder(@Valid @RequestBody CreateVipOrderDto createVipOrderDto) {
        // 用户态订单只能为当前登录用户创建，避免前端透传其他用户ID。
        Integer userId = getRequiredUserId();
        // 下单、写订单快照和组装支付宝参数全部由支付服务统一处理。
        return Result.success(payOrderService.createVipOrder(userId, createVipOrderDto));
    }

    /**
     * 查询当前登录用户自己的订单状态。
     */
    @GetMapping("/orders/{orderNo}")
    public Result<VipOrderVo> getOrder(@PathVariable @NotNull(message = "订单号不能为空") String orderNo) {
        // 订单详情同样按当前登录用户隔离，防止通过订单号越权查看。
        Integer userId = getRequiredUserId();
        return Result.success(payOrderService.getCurrentUserOrder(userId, orderNo));
    }

    /**
     * 分页查询当前登录用户的 VIP 订单。
     */
    @GetMapping("/orders")
    public Result<PageVo<List<VipOrderVo>>> listOrders(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        // 会员中心订单列表只返回当前用户自己的支付记录。
        Integer userId = getRequiredUserId();
        return Result.success(payOrderService.listCurrentUserOrders(userId, pageNum, pageSize));
    }

    /**
     * 对待支付订单进行再次支付，返回支付参数。
     */
    @PostMapping("/orders/{orderNo}/pay")
    public Result<VipOrderCreateVo> repayOrder(@PathVariable @NotNull(message = "订单号不能为空") String orderNo) {
        Integer userId = getRequiredUserId();
        return Result.success(payOrderService.repayOrder(userId, orderNo));
    }

    /**
     * 取消待支付订单。
     */
    @PostMapping("/orders/{orderNo}/cancel")
    public Result<Void> cancelOrder(@PathVariable @NotNull(message = "订单号不能为空") String orderNo) {
        Integer userId = getRequiredUserId();
        payOrderService.cancelOrder(userId, orderNo);
        return Result.success();
    }

    /**
     * 返回当前登录用户的会员状态和 AI 配额信息。
     */
    @GetMapping("/me")
    public Result<VipMemberVo> getCurrentVipInfo() {
        // 会员中心需要的状态、到期时间和 AI 配额统一由聚合服务一次性返回。
        Integer userId = getRequiredUserId();
        return Result.success(vipService.getCurrentVipMemberInfo(userId));
    }

    /**
     * 支付宝异步回调入口，只有这里会真正发放会员资格。
     */
    @PostMapping("/pay/alipay/notify")
    public String alipayNotify(@RequestParam Map<String, String> params) {
        try {
            // 支付回调的验签、幂等和会员发放都必须走支付服务，控制器只保留回调入口。
            boolean handled = payOrderService.handleAlipayNotify(params);
            return handled ? "success" : "failure";
        } catch (Exception e) {
            // 回调接口一旦返回 failure，支付宝会按规则重试，避免临时异常丢单。
            return "failure";
        }
    }

    /**
     * VIP 相关接口统一要求登录态，这里复用同一套校验。
     */
    private Integer getRequiredUserId() {
        Integer userId = SecurityUtils.getUserId();
        if (userId == null || userId == 0) {
            // 会员接口默认都依赖登录态，这里统一收口避免每个接口重复写校验。
            throw new BlogException(BlogConstants.LoginRequired);
        }
        return userId;
    }
}
