package com.sidifensen.domain.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 管理端手动调整会员的请求参数。
 */
@Data
public class VipAdminMemberAdjustDto implements Serializable {

    @NotNull(message = "用户ID不能为空")
    private Integer userId;

    @NotBlank(message = "调整动作不能为空")
    private String actionType;

    @Min(value = 1, message = "调整天数必须大于0")
    private Integer days;

    @Size(max = 200, message = "备注长度不能超过200个字符")
    private String remark;
}
