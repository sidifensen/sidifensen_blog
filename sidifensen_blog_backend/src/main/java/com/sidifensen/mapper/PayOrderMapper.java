package com.sidifensen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sidifensen.domain.entity.PayOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付订单数据访问层。
 */
@Mapper
public interface PayOrderMapper extends BaseMapper<PayOrder> {
}
