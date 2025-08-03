package com.sidifensen.domain.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
    private Integer id;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 相册名称
     */
    private String name;

    /**
     * 相册封面
     */
    private String coverUrl;

    /**
     * 展示状态 0-公开 1-私密
     */
    //展示状态只能为0或1
    @Min(value = 0, message = "展示状态错误")
    @Max(value = 1, message = "展示状态错误")
    private Integer showStatus;

    /**
     * 创建时间
     */
    private Date createTime;


    /**
     * 相册照片列表
     */
    private List<PhotoDto> photos;

    /**
     * 用户名
     */
    private String userName;

}
