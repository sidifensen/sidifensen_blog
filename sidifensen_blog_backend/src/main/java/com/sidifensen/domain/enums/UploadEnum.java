package com.sidifensen.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.List;

/**
 * @author sidifensen
 * @since 2025-07-12
 */
@Getter
@AllArgsConstructor
public enum UploadEnum {

    // 文章封面
    ARTICLE_COVER("article/articleCover/","文章封面",List.of("jpg", "jpeg", "png", "webp"), 0.3),

    // 文章图片
    ARTICLE_IMAGE("article/articleImage/","文章图片",List.of("jpg", "jpeg", "png", "gif", "webp"), 3.0),

    // 用户头像
    USER_AVATAR("user/avatar/","用户头像",List.of("jpg", "jpeg", "png", "webp"), 0.3),

    // 前台首页Banners图片
    BANNERS("banners/","前台首页Banners图片",List.of("jpg", "jpeg", "png", "webp"), 0.3);


    // 上传目录
    private final String dir;

    // 描述
    private final String description;

    // 支持的格式
    private final List<String> format;

    // 文件最大大小 单位：MB
    private final Double limitSize;


}
