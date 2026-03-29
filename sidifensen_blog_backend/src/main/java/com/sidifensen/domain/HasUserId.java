package com.sidifensen.domain;

/**
 * 具有用户ID属性的接口
 * 用于批量填充用户信息
 *
 * @author sidifensen
 * @since 2026-03-29
 */
public interface HasUserId {

    /**
     * 获取用户ID
     *
     * @return 用户ID
     */
    Integer getUserId();
}
