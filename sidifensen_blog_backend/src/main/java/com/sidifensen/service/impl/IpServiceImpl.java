package com.sidifensen.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.sidifensen.domain.entity.SysUser;
import com.sidifensen.mapper.SysUserMapper;
import com.sidifensen.service.IpService;
import com.sidifensen.utils.IpUtils;
import com.sidifensen.utils.MyThreadFactory;
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

    ExecutorService executorService = new ThreadPoolExecutor(
            2, 4, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(500),
            new MyThreadFactory("IpServiceImpl")
    );


    public void setRegisterIp(Integer id, String ip) {
        executorService.execute(() -> {
            SysUser sysUser = sysUserMapper.selectById(id);
            if (ObjectUtil.isNotEmpty(sysUser)){
                sysUser.setRegisterAddress(ipUtils.getAddress(ip));
                sysUserMapper.updateById(sysUser);
            }
        });
    }

    public void setLoginIp(Integer id, String ip) {
        executorService.execute(() -> {
            SysUser sysUser = sysUserMapper.selectById(id);
            if (ObjectUtil.isNotEmpty(sysUser)){
                sysUser.setLoginAddress(ipUtils.getAddress(ip));
                sysUserMapper.updateById(sysUser);
            }
        });
    }
}
