package com.sidifensen.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sidifensen.domain.entity.SysUser;
import com.sidifensen.mapper.SysUserMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户工具类
 * 提供批量获取用户Map等功能
 *
 * @author sidifensen
 * @since 2026-03-29
 */
@Slf4j
public class UserUtils {

    /**
     * 批量获取用户Map
     * 以用户ID为key，用户实体为value
     *
     * @param userIds    用户ID列表
     * @param userMapper 用户Mapper
     * @return 用户ID与用户实体的映射Map
     */
    public static Map<Integer, SysUser> getUserMap(List<Integer> userIds, SysUserMapper userMapper) {
        if (userIds == null || userIds.isEmpty() || userMapper == null) {
            return Collections.emptyMap();
        }

        // 过滤有效的用户ID
        List<Integer> validUserIds = userIds.stream()
                .filter(id -> id != null && id > 0)
                .distinct()
                .collect(Collectors.toList());

        if (validUserIds.isEmpty()) {
            return Collections.emptyMap();
        }

        // 批量查询用户信息
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<SysUser>()
                .in(SysUser::getId, validUserIds)
                .select(SysUser::getId, SysUser::getNickname);

        List<SysUser> users = userMapper.selectList(queryWrapper);

        // 转换为Map
        return users.stream().collect(Collectors.toMap(SysUser::getId, user -> user));
    }
}
