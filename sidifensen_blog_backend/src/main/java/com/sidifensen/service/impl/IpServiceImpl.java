package com.sidifensen.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.sidifensen.domain.entity.SysUser;
import com.sidifensen.mapper.SysUserMapper;
import com.sidifensen.service.IpService;
import com.sidifensen.utils.IpUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author sidifensen
 * @since 2025-08-13
 */
@Slf4j
@Service
public class IpServiceImpl implements IpService {

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private IpUtils ipUtils;

    ExecutorService executor = new ThreadPoolExecutor(2, 4, 0l, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(500));


    public void setRegisterIp(Integer id, String ip) {
        executor.execute(() -> {
            SysUser sysUser = sysUserMapper.selectById(id);
            if (ObjectUtil.isNotEmpty(sysUser)){
                sysUser.setRegisterAddress(ipUtils.getAddress(ip));
                sysUserMapper.updateById(sysUser);
            }
        });
    }

    public void setLoginIp(Integer id, String ip) {
        executor.execute(() -> {
            SysUser sysUser = sysUserMapper.selectById(id);
            if (ObjectUtil.isNotEmpty(sysUser)){
                sysUser.setLoginAddress(ipUtils.getAddress(ip));
                sysUserMapper.updateById(sysUser);
            }
        });
    }
}
