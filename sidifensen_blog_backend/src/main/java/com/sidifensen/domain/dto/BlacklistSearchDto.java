package com.sidifensen.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 黑名单搜索数据传输对象
 *
 * @author sidifensen
 * @since 2025-10-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BlacklistSearchDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 黑名单类型 0-用户 1-ip地址
     */
    @Min(value = 0, message = "黑名单类型错误")
    @Max(value = 1, message = "黑名单类型错误")
    private Integer type;

    /**
     * 用户id（传了用户id的时候设置黑名单类型为用户）
     */
    @Min(value = 1, message = "用户id必须大于0")
    private Integer userId;

    /**
     * 拉黑时间开始
     */
    private Date banTimeStart;

    /**
     * 拉黑时间结束
     */
    private Date banTimeEnd;

    /**
     * 到期时间开始
     */
    private Date expireTimeStart;

    /**
     * 到期时间结束
     */
    private Date expireTimeEnd;
}

