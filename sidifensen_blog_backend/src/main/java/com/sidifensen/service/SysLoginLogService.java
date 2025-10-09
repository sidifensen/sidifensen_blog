package com.sidifensen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sidifensen.domain.dto.SysLoginLogQueryDto;
import com.sidifensen.domain.entity.SysLoginLog;
import com.sidifensen.domain.vo.SysLoginLogVo;

import java.util.List;

/**
 * <p>
 * 登录日志服务类
 * </p>
 *
 * @author sidifensen
 * @since 2025-10-06
 */
public interface SysLoginLogService extends IService<SysLoginLog> {

    /**
     * 异步记录登录日志
     *
     * @param userId       用户ID
     * @param username     用户名
     * @param loginType    登录方式
     * @param loginIp      登录IP
     * @param loginAddress 登录地址
     * @param status       登录状态 0-成功 1-失败
     */
    void recordLoginLog(Integer userId, String username, Integer loginType, String loginIp, String loginAddress, Integer status);

    /**
     * 查询所有登录日志（按时间倒序）
     *
     * @return 登录日志列表
     */
    List<SysLoginLogVo> getLoginLogList();

    /**
     * 搜索登录日志
     *
     * @param queryDto 查询条件
     * @return 登录日志列表
     */
    List<SysLoginLogVo> searchLoginLog(SysLoginLogQueryDto queryDto);

    /**
     * 批量删除登录日志
     *
     * @param ids 登录日志ID列表
     */
    void deleteLoginLogs(List<Integer> ids);
}
