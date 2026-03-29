package com.sidifensen.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * VIP 统计数据 VO
 *
 * @author sidifensen
 * @since 2026-03-28
 */
@Data
@Accessors(chain = true)
public class VipStatisticsVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * VIP 总数
     */
    private Long totalVipCount;

    /**
     * 近7天新增 VIP 数量
     */
    private Long newVipCount;

    /**
     * 近30天新增 VIP 数量
     */
    private Long newVipCount30Days;

    /**
     * 普通用户数（非VIP）
     */
    private Long normalUserCount;

    /**
     * VIP占比百分比
     */
    private Double vipPercentage;

}
