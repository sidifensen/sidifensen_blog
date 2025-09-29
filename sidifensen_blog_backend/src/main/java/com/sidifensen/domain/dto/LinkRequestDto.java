package com.sidifensen.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 申请友链DTO
 *
 * @author sidifensen
 * @since 2025-09-28
 */
@Data
public class LinkRequestDto {

    /**
     * 网站名称
     */
    @NotBlank(message = "网站名称不能为空")
    @Size(max = 20, message = "网站名称不能超过20个字")
    private String name;

    /**
     * 网站地址
     */
    @NotBlank(message = "网站地址不能为空")
    @Size(max = 200, message = "网站地址不能超过200个字")
    private String url;

    /**
     * 网站封面
     */
    private String coverUrl;

    /**
     * 网站描述
     */
    @NotBlank(message = "网站描述不能为空")
    @Size(max = 200, message = "网站描述不能超过200个字")
    private String description;

    /**
     * 网站邮箱
     */
    @NotBlank(message = "网站邮箱不能为空")
    private String email;
}
