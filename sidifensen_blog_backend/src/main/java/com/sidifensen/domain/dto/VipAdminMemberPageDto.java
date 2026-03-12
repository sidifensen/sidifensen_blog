package com.sidifensen.domain.dto;

import org.springframework.format.annotation.DateTimeFormat;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 管理端会员列表分页查询参数。
 */
@Data
public class VipAdminMemberPageDto implements Serializable {

    @Min(value = 1, message = "页码不能为空")
    private Integer pageNum = 1;

    @Min(value = 1, message = "每页大小不能为空")
    private Integer pageSize = 10;

    /**
     * 关键词，支持用户ID、用户名、昵称、邮箱。
     */
    private String keyword;

    /**
     * 会员状态 NONE/ACTIVE/EXPIRED。
     */
    private String status;

    /**
     * 到期时间开始。
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expireTimeStart;

    /**
     * 到期时间结束。
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expireTimeEnd;
}
