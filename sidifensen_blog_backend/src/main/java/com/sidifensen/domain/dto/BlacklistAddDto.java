package com.sidifensen.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 新增黑名单数据传输对象
 *
 * @author sidifensen
 * @since 2025-10-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BlacklistAddDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id列表（支持批量）
     */
    @NotEmpty(message = "用户ID列表不能为空")
    private List<Integer> userIds;

    /**
     * 拉黑原因
     */
    @NotNull(message = "拉黑原因不能为空")
    private String reason;

    /**
     * 到期时间
     */
    @NotNull(message = "到期时间不能为空")
    private Date expireTime;
}

