package com.sidifensen.utils;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

import java.util.function.Consumer;

/**
 * MyBatis-Plus 查询条件构建工具类
 * 用于动态构建 LambdaQueryWrapper 查询条件，支持条件拼接
 *
 * @author sidifensen
 * @since 2025-07-11
 */
public class QueryBuilder {

    /**
     * 构建动态查询条件
     * 通过可变数量的条件构建器动态添加查询条件
     *
     * @param dto              包含查询条件的 DTO 对象
     * @param conditionBuilders 条件构建器数组，可以为 null
     * @param <T>              DTO 类型
     * @return LambdaQueryWrapper 查询包装器
     */
    @SafeVarargs
    public static <T> LambdaQueryWrapper<T> buildSearchWrapper(T dto, Consumer<LambdaQueryWrapper<T>>... conditionBuilders) {
        LambdaQueryWrapper<T> wrapper = new LambdaQueryWrapper<>();
        if (conditionBuilders != null) {
            for (Consumer<LambdaQueryWrapper<T>> builder : conditionBuilders) {
                if (builder != null) {
                    builder.accept(wrapper);
                }
            }
        }
        return wrapper;
    }

    /**
     * 条件 LIKE 查询
     * 当 condition 为 true 时，才执行 LIKE 查询
     *
     * @param wrapper  查询包装器
     * @param condition 是否执行查询的条件
     * @param column   字段引用
     * @param value    查询值
     * @param <T>      实体类型
     */
    public static <T> void likeWhen(LambdaQueryWrapper<T> wrapper, boolean condition, SFunction<T, ?> column, String value) {
        if (condition && ObjectUtil.isNotEmpty(value)) {
            wrapper.like(column, value);
        }
    }

    /**
     * 条件 EQ 查询
     * 当 condition 为 true 时，才执行 EQ 查询
     *
     * @param wrapper  查询包装器
     * @param condition 是否执行查询的条件
     * @param column   字段引用
     * @param value    查询值
     * @param <T>      实体类型
     */
    public static <T> void eqWhen(LambdaQueryWrapper<T> wrapper, boolean condition, SFunction<T, ?> column, Object value) {
        if (condition && ObjectUtil.isNotEmpty(value)) {
            wrapper.eq(column, value);
        }
    }

    /**
     * 条件 GE 查询（大于等于）
     * 当 condition 为 true 时，才执行 GE 查询
     *
     * @param wrapper  查询包装器
     * @param condition 是否执行查询的条件
     * @param column   字段引用
     * @param value    查询值
     * @param <T>      实体类型
     */
    public static <T> void geWhen(LambdaQueryWrapper<T> wrapper, boolean condition, SFunction<T, ?> column, Comparable<?> value) {
        if (condition && ObjectUtil.isNotEmpty(value)) {
            wrapper.ge(column, value);
        }
    }

    /**
     * 条件 LE 查询（小于等于）
     * 当 condition 为 true 时，才执行 LE 查询
     *
     * @param wrapper  查询包装器
     * @param condition 是否执行查询的条件
     * @param column   字段引用
     * @param value    查询值
     * @param <T>      实体类型
     */
    public static <T> void leWhen(LambdaQueryWrapper<T> wrapper, boolean condition, SFunction<T, ?> column, Comparable<?> value) {
        if (condition && ObjectUtil.isNotEmpty(value)) {
            wrapper.le(column, value);
        }
    }

}
