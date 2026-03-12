package com.sidifensen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sidifensen.domain.dto.CreateVipOrderDto;
import com.sidifensen.domain.entity.PayOrder;
import com.sidifensen.domain.vo.PageVo;
import com.sidifensen.domain.vo.VipOrderCreateVo;
import com.sidifensen.domain.vo.VipOrderVo;
import com.sidifensen.domain.vo.VipPlanVo;

import java.util.List;
import java.util.Map;

/**
 * VIP 支付订单服务。
 */
public interface PayOrderService extends IService<PayOrder> {

    /**
     * 获取当前启用的 VIP 套餐列表。
     */
    List<VipPlanVo> listVipPlans();

    /**
     * 创建 VIP 支付订单。
     */
    VipOrderCreateVo createVipOrder(Integer userId, CreateVipOrderDto createVipOrderDto);

    /**
     * 查询当前用户自己的订单详情。
     */
    VipOrderVo getCurrentUserOrder(Integer userId, String orderNo);

    /**
     * 分页查询当前用户订单列表。
     */
    PageVo<List<VipOrderVo>> listCurrentUserOrders(Integer userId, Integer pageNum, Integer pageSize);

    /**
     * 处理支付宝异步回调，并在成功后发放会员资格。
     */
    boolean handleAlipayNotify(Map<String, String> params);
}
