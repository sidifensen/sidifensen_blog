package com.sidifensen.utils;

import cn.hutool.core.util.ObjectUtil;
import com.sidifensen.exception.BlogException;

/**
 * 实体校验工具类
 *
 * @author sidifensen
 * @since 2026-03-29
 */
public class EntityCheckUtils {

    /**
     * 获取实体，若为null则抛出异常
     *
     * @param entity       实体对象
     * @param errorMessage 错误消息
     * @param <T>          实体类型
     * @return 非空的实体
     */
    public static <T> T getOrThrow(T entity, String errorMessage) {
        if (entity == null) {
            throw new BlogException(errorMessage);
        }
        return entity;
    }

    /**
     * 获取实体，使用ObjectUtil.isEmpty检查，支持各种空类型
     *
     * @param entity       实体对象
     * @param errorMessage 错误消息
     * @param <T>          实体类型
     * @return 非空的实体
     */
    public static <T> T getOrThrowNotEmpty(T entity, String errorMessage) {
        if (ObjectUtil.isEmpty(entity)) {
            throw new BlogException(errorMessage);
        }
        return entity;
    }

    /**
     * 获取实体，检查是否被删除（isDeleted == 1）
     *
     * @param entity       实体对象，需包含isDeleted字段
     * @param errorMessage 错误消息
     * @param <T>          实体类型
     * @return 未删除的实体
     */
    public static <T> T getOrThrowNotDeleted(T entity, String errorMessage) {
        if (entity == null) {
            throw new BlogException(errorMessage);
        }
        // 通过反射获取isDeleted字段值
        try {
            var isDeletedField = entity.getClass().getDeclaredField("isDeleted");
            isDeletedField.setAccessible(true);
            Integer isDeleted = (Integer) isDeletedField.get(entity);
            if (isDeleted != null && isDeleted == 1) {
                throw new BlogException(errorMessage);
            }
        } catch (NoSuchFieldException e) {
            // 实体没有isDeleted字段，忽略检查
        } catch (IllegalAccessException e) {
            throw new BlogException(errorMessage);
        }
        return entity;
    }

    /**
     * 验证非空，可用于参数校验
     *
     * @param entity       实体对象
     * @param errorMessage 错误消息
     * @param <T>          实体类型
     * @return 非空的实体
     */
    public static <T> T requireNonNull(T entity, String errorMessage) {
        if (entity == null) {
            throw new BlogException(errorMessage);
        }
        return entity;
    }
}
