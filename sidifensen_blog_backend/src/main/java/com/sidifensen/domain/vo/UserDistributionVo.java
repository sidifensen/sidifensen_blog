package com.sidifensen.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;
import java.io.Serial;
import java.io.Serializable;

/**
 * 用户分布 VO
 *
 * @author sidifensen
 * @since 2026-03-28
 */
@Data
@Accessors(chain = true)
public class UserDistributionVo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户类型：普通用户、VIP用户、管理员
     */
    private String type;

    /**
     * 数量
     */
    private Long count;
}
