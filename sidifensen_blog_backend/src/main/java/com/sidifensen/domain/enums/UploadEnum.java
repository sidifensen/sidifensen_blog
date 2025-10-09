package com.sidifensen.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * @author sidifensen
 * @since 2025-07-12
 */
@Getter
@AllArgsConstructor
public enum UploadEnum {

    // 相册图片
    ALBUM("album/", "相册图片", List.of("jpg", "jpeg", "png", "gif", "webp"), 5.0),

    // 文章封面
    ARTICLE("article/", "文章封面", List.of("jpg", "jpeg", "png", "webp"), 5.0),

    // 用户头像
    USER_AVATAR("user/avatar/", "用户头像", List.of("jpg", "jpeg", "png", "webp"), 1.0),

    // 专栏封面
    COLUMN("column/", "专栏封面", List.of("jpg", "jpeg", "png", "webp"), 5.0),

    // 前台首页图片
    BANNERS("banners/", "前台首页图片", List.of("jpg", "jpeg", "png", "webp"), 1.0);


    // 上传目录
    private final String dir;

    // 描述
    private final String description;

    // 支持的格式
    private final List<String> format;

    // 文件最大大小 单位：MB
    private final Double limitSize;


}
