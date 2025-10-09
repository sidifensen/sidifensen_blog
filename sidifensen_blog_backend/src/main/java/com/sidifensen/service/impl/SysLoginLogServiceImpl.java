package com.sidifensen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.dto.SysLoginLogQueryDto;
import com.sidifensen.domain.entity.SysLoginLog;
import com.sidifensen.domain.enums.LoginStatusEnum;
import com.sidifensen.domain.enums.RegisterOrLoginTypeEnum;
import com.sidifensen.domain.vo.SysLoginLogVo;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.SysLoginLogMapper;
import com.sidifensen.service.SysLoginLogService;
import com.sidifensen.utils.IpUtils;
import com.sidifensen.utils.MyThreadFactory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 登录日志服务实现类
 * </p>
 *
 * @author sidifensen
 * @since 2025-10-06
 */
@Service
@Slf4j
public class SysLoginLogServiceImpl extends ServiceImpl<SysLoginLogMapper, SysLoginLog> implements SysLoginLogService {

    @Resource
    private SysLoginLogMapper sysLoginLogMapper;

    @Resource
    private IpUtils ipUtils;

    // 创建线程池
    private final ExecutorService executorService = new ThreadPoolExecutor(
            2,
            4,
            0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(500),
            new MyThreadFactory("SysLoginLogServiceImpl")
    );

    /**
     * 异步记录登录日志
     */
    @Override
    public void recordLoginLog(Integer userId, String username, Integer loginType, String loginIp, String loginAddress, Integer status) {
        // 使用线程池异步执行
        executorService.execute(() -> {
            try {
                SysLoginLog loginLog = new SysLoginLog();
                loginLog.setUserId(userId);
                loginLog.setUsername(username);
                loginLog.setLoginType(loginType);
                loginLog.setLoginIp(loginIp);
                loginLog.setLoginAddress(loginAddress);
                loginLog.setStatus(status);
                loginLog.setLoginTime(new Date());

                sysLoginLogMapper.insert(loginLog);
            } catch (Exception e) {
                log.error("记录登录日志失败：用户ID={}, 用户名={}, 登录IP={}, 错误信息={}", userId, username, loginIp, e.getMessage(), e);
            }
        });
    }

        /**
     * 异步记录登录日志
     */
    @Override
    public void recordLoginLog(Integer userId, String username, Integer loginType, String loginIp, Integer status) {
        // 使用线程池异步执行
        executorService.execute(() -> {
            try {
                SysLoginLog loginLog = new SysLoginLog();
                loginLog.setUserId(userId);
                loginLog.setUsername(username);
                loginLog.setLoginType(loginType);
                loginLog.setLoginIp(loginIp);
                loginLog.setLoginAddress(ipUtils.getAddress(loginIp));
                loginLog.setStatus(status);
                loginLog.setLoginTime(new Date());

                sysLoginLogMapper.insert(loginLog);
            } catch (Exception e) {
                log.error("记录登录日志失败：用户ID={}, 用户名={}, 登录IP={}, 错误信息={}", userId, username, loginIp, e.getMessage(), e);
            }
        });
    }

    /**
     * 查询所有登录日志（按时间倒序）
     */
    @Override
    public List<SysLoginLogVo> getLoginLogList() {
        LambdaQueryWrapper<SysLoginLog> qw = new LambdaQueryWrapper<SysLoginLog>()
                .orderByDesc(SysLoginLog::getLoginTime);

        List<SysLoginLog> loginLogs = sysLoginLogMapper.selectList(qw);
        return convertToVo(loginLogs);
    }

    /**
     * 搜索登录日志
     */
    @Override
    public List<SysLoginLogVo> searchLoginLog(SysLoginLogQueryDto queryDto) {
        LambdaQueryWrapper<SysLoginLog> qw = new LambdaQueryWrapper<SysLoginLog>()
                .eq(ObjectUtil.isNotEmpty(queryDto.getUserId()), SysLoginLog::getUserId, queryDto.getUserId())
                .eq(ObjectUtil.isNotEmpty(queryDto.getLoginType()), SysLoginLog::getLoginType, queryDto.getLoginType())
                .eq(ObjectUtil.isNotEmpty(queryDto.getStatus()), SysLoginLog::getStatus, queryDto.getStatus())
                .ge(ObjectUtil.isNotEmpty(queryDto.getLoginTimeStart()), SysLoginLog::getLoginTime, queryDto.getLoginTimeStart())
                .le(ObjectUtil.isNotEmpty(queryDto.getLoginTimeEnd()), SysLoginLog::getLoginTime, queryDto.getLoginTimeEnd())
                .orderByDesc(SysLoginLog::getLoginTime);

        List<SysLoginLog> loginLogs = sysLoginLogMapper.selectList(qw);
        return convertToVo(loginLogs);
    }

    /**
     * 批量删除登录日志
     */
    @Override
    public void deleteLoginLogs(List<Integer> ids) {
        if (ObjectUtil.isEmpty(ids)) {
            throw new BlogException(BlogConstants.LoginLogIdsRequired);
        }

        int result = sysLoginLogMapper.deleteByIds(ids);
        if (result == 0) {
            throw new BlogException(BlogConstants.DeleteLoginLogError);
        }

        log.info("批量删除登录日志成功，删除数量：{}", result);
    }

    /**
     * 转换为VO
     */
    private List<SysLoginLogVo> convertToVo(List<SysLoginLog> loginLogs) {
        return loginLogs.stream().map(loginLog -> {
            SysLoginLogVo vo = BeanUtil.copyProperties(loginLog, SysLoginLogVo.class);

            // 设置登录方式描述
            vo.setLoginTypeDesc(RegisterOrLoginTypeEnum.getType(loginLog.getLoginType()));

            // 设置登录状态描述
            if (loginLog.getStatus().equals(LoginStatusEnum.SUCCESS.getCode())) {
                vo.setStatusDesc(LoginStatusEnum.SUCCESS.getDescription());
            } else {
                vo.setStatusDesc(LoginStatusEnum.FAIL.getDescription());
            }

            return vo;
        }).collect(Collectors.toList());
    }
}
