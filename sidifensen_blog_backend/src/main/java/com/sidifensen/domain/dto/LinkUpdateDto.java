package com.sidifensen.domain.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 管理员更新友链DTO
 *
 * @author sidifensen
 * @since 2025-09-28
 */
@Data
public class LinkUpdateDto {

    /**
     * 友链ID
     */
    @NotNull(message = "友链ID不能为空")
    private Integer id;

    /**
     * 网站名称
     */
    @Size(max = 20, message = "网站名称不能超过20个字")
    private String name;

    /**
     * 网站地址
     */
    @Size(max = 200, message = "网站地址不能超过200个字")
    private String url;

    /**
     * 网站封面
     */
    private String coverUrl;

    /**
     * 网站描述
     */
    @Size(max = 50, message = "网站描述不能超过50个字")
    private String description;

    /**
     * 网站邮箱
     */
    private String email;
}
