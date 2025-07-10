package com.sidifensen.redis;


import com.sidifensen.domain.constants.RedisConstants;
import com.sidifensen.utils.RedisUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;


import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class RedisComponent {

    @Resource
    private RedisUtils redisUtils;

    // 保存登录验证码
    public String saveCheckCode(String checkCode) {
        String checkCodeKey = UUID.randomUUID().toString().replace("-", "");
        redisUtils.set(RedisConstants.CheckCode + checkCodeKey, checkCode, 5, TimeUnit.MINUTES);
        return checkCodeKey;
    }

    // 获取登录验证码
    public String getCheckCode(String checkCodeKey) {
        return (String) redisUtils.get(RedisConstants.CheckCode + checkCodeKey);
    }

    // 清除登录验证码
    public void cleanCheckCode(String checkCodeKey) {
        redisUtils.del(RedisConstants.CheckCode + checkCodeKey);
    }

    // 保存邮箱验证码
    public void saveEmailCheckCode(String email, String type, String checkCode) {
        redisUtils.set(RedisConstants.EmailCheckCode + type + ":" + email, checkCode, 5, TimeUnit.MINUTES);
    }

    // 获取邮箱验证码
    public String getEmailCheckCode(String email,String type) {
        return (String) redisUtils.get(RedisConstants.EmailCheckCode + type + ":" + email);
    }

    // 清除邮箱验证码
    public void cleanEmailCheckCode(String email,String type) {
        redisUtils.del(RedisConstants.EmailCheckCode + type + ":" + email);
    }
}
