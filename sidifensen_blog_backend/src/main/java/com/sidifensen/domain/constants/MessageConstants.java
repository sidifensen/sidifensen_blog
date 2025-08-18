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

    /**
     * 图片需要审核的消息
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
     * 将传入的Map参数转换为JSON格式
     * @param map 包含数据的Map
     * @return JSON字符串
     */
    public static String toJson(Map<String, Object> map) {
        return JSONUtil.toJsonStr(map);
    }

}