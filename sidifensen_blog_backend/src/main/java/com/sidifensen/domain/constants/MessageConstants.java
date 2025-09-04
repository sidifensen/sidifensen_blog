package com.sidifensen.domain.constants;

import cn.hutool.json.JSONUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sidifensen
 * @since 2025-08-18
 */
public class MessageConstants {


    public static final String IMAGE_NEED_REVIEW = "图片 %d 需要人工审核";
    public static final String ARTICLE_NEED_REVIEW = "文章 %d 需要人工审核, 原因: %s";
    public static final String ARTICLE_AUDIT_NOT_PASS = "文章id %d 标题 %s 内容审核不通过, 原因: %s";
    public static final String ARTICLE_AUDIT_PASS = "文章id %d 标题 %s 审核通过";

    /**
     * 图片需要审核的消息
     *
     * @param photoId 图片id
     * @return 格式化后的消息
     */
    public static String ImageNeedReview(Integer photoId) {
        String text = String.format(IMAGE_NEED_REVIEW, photoId);
        Map<String, Object> map = new HashMap<>();
        map.put("text", text);
        return toJson(map);
    }

    /**
     * 文章需要审核的消息
     *
     * @param articleId 文章id
     * @return 格式化后的消息
     */
    public static String ArticleNeedReview(Integer articleId, String articleTitle, String reason) {
        String text = String.format(ARTICLE_NEED_REVIEW, articleId, articleTitle, reason);
        Map<String, Object> map = new HashMap<>();
        map.put("text", text);
        return toJson(map);
    }

    /**
     * 文章审核通过的消息
     *
     * @param articleId 文章id
     * @param title 文章标题
     * @return 格式化后的消息
     */
    public static String ArticleAuditPass(Integer articleId, String title) {
        String text = String.format(ARTICLE_AUDIT_PASS, articleId, title);
        Map<String, Object> map = new HashMap<>();
        map.put("text", text);
        return toJson(map);
    }

    /**
     * 文章审核不通过的消息
     *
     * @param message 审核不通过的消息
     * @return 格式化后的消息
     */
    public static String ArticleAuditNotPass(Integer articleId, String title, String message) {
        String text = String.format(ARTICLE_AUDIT_NOT_PASS, articleId, title, message);
        Map<String, Object> map = new HashMap<>();
        map.put("text", text);
        return toJson(map);
    }

    /**
     * 将传入的Map参数转换为JSON格式
     *
     * @param map 包含数据的Map
     * @return JSON字符串
     */
    public static String toJson(Map<String, Object> map) {
        return JSONUtil.toJsonStr(map);
    }

}