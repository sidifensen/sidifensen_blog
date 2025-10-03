package com.sidifensen.domain.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 修改黑名单数据传输对象
 *
 * @author sidifensen
 * @since 2025-10-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BlacklistUpdateDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 黑名单id
     */
    @NotNull(message = "黑名单ID不能为空")
    @Min(value = 1, message = "黑名单ID必须大于0")
    private Integer id;

    /**
     * 拉黑原因
     */
    private String reason;

    /**
     * 到期时间
     */
    private Date expireTime;
}

