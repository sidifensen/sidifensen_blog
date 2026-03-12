package com.sidifensen.domain.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 管理端修改 VIP 套餐的请求参数。
 */
@Data
public class VipAdminPlanUpdateDto implements Serializable {

    @NotNull(message = "套餐ID不能为空")
    private Integer id;

    @NotBlank(message = "套餐名称不能为空")
    @Size(max = 50, message = "套餐名称长度不能超过50个字符")
    private String name;

    @NotNull(message = "套餐时长不能为空")
    @Min(value = 1, message = "套餐时长必须大于0")
    private Integer days;

    @NotNull(message = "套餐金额不能为空")
    @Min(value = 1, message = "套餐金额必须大于0")
    private Integer priceFen;

    @NotNull(message = "套餐启用状态不能为空")
    private Boolean enabled;

    @Size(max = 100, message = "套餐描述长度不能超过100个字符")
    private String description;
}
