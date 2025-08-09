package com.sidifensen;

import com.sidifensen.domain.entity.SysUser;
import com.sidifensen.mapper.SysUserMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

/**
 * @author sidifensen
 * @since 2025-07-08
 */
@SpringBootTest
public class UserTest {

    @Resource
    private SysUserMapper sysUserMapper;

    @Test
    public void test1() {
        SysUser sysUser = new SysUser();
        sysUser.setUsername("youyu");
        sysUser.setPassword("123456");
        sysUser.setNickname("Youyu");
        sysUser.setEmail("123456@gmail.com");
        sysUser.setLoginIp("127.0.0.1");
        sysUser.setStatus(0);

        sysUserMapper.insert(sysUser);
    }

    @Test
    public void test2() {
        String uuid = UUID.randomUUID().toString();
        System.out.println(uuid);
    }
}
