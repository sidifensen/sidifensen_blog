package com.sidifensen.domain.dto;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 相册 DTO
 * </p>
 *
 * @author sidifensen
 * @since 2025-07-30
 */
@Data
public class AlbumDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 相册id
     */
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 相册名称
     */
    private String name;

    /**
     * 相册描述
     */
    private String description;

    /**
     * 相册封面
     */
    private String coverUrl;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 展示状态 0-公开 1-私有
     */
    //展示状态只能为0或1
    @Min(value = 0, message = "展示状态错误")
    @Max(value = 1, message = "展示状态错误")
    private Integer showStatus;
}
