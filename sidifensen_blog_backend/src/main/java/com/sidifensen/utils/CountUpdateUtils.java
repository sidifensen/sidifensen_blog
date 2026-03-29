package com.sidifensen.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import cn.hutool.core.util.ObjectUtil;

/**
 * 计数更新工具类
 * 用于生成计数更新的SQL片段，支持正负数增量更新
 *
 * @author sidifensen
 * @since 2026-03-29
 */
public class CountUpdateUtils {

    /**
     * 生成计数更新SQL片段
     * 正数：field = COALESCE(field, 0) + delta
     * 负数：field = GREATEST(COALESCE(field, 0) + delta, 0)
     *
     * @param wrapper 更新条件构造器
     * @param field   字段名
     * @param delta   增量值（正数或负数）
     */
    public static void incrementCount(LambdaUpdateWrapper<?> wrapper, String field, int delta) {
        if (delta >= 0) {
            // 正数增量：直接累加，NULL值视为0
            wrapper.setSql(field + " = COALESCE(" + field + ", 0) + " + delta);
        } else {
            // 负数增量：使用GREATEST确保结果不小于0
            wrapper.setSql(field + " = GREATEST(COALESCE(" + field + ", 0) + " + delta + ", 0)");
        }
    }

}
