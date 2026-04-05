package com.sidifensen.domain.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

/**
 * <p>
 * 更新用户信息DTO
 * </p>
 *
 * @author sidifensen
 * @since 2025-10-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class UpdateUserInfoDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 昵称
     */
    @Length(min = 4, max = 20, message = "昵称长度必须在4-20个字符之间")
    private String nickname;

    /**
     * 性别 0-男 1-女
     */
    private Integer sex;

    /**
     * 简介
     */
    @Size(max = 200, message = "简介不能超过200个字符")
    private String introduction;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 是否接收私信邮件通知 0-关闭 1-开启
     */
    private Integer isReceivePrivateMessageEmail;
}

