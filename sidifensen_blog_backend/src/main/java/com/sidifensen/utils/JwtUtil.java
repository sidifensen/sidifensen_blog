package com.sidifensen.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.JWTValidator;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import com.sidifensen.domain.constants.RedisConstants;
import com.sidifensen.domain.dto.UserDto;
import com.sidifensen.domain.entity.LoginUser;
import com.sidifensen.domain.entity.SysUser;
import com.sidifensen.exception.BlogException;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.security.SignatureException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtil {

    @Value("${spring.security.jwt.secret}")
    private String secret;

    /**
     * 生成token
     * @param userDto
     * @return
     */
    public String createToken(UserDto userDto,boolean isRememberMe) {
        String token = JWT.create()
                // 设置过期时间 rememberMe为true时，token有效期为7天，否则为1天
                .setExpiresAt(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(isRememberMe? 7 : 1)))
                .addPayloads(BeanUtil.beanToMap(userDto))
                .setSigner(JWTSignerUtil.createSigner("HS256", secret.getBytes()))
                .sign();
        return token;
    }

    /**
     * 解析并验证token
     * @param token
     * @return
     */
    public String parseToken(String token) {
        // 验证token
        if (!JWTUtil.verify(token, secret.getBytes())) {
            return null;
        }
        // 解析token
        JWT jwt = JWTUtil.parseToken(token);
        // 判断是否过期
        long exp = Long.parseLong(jwt.getPayload().getClaim("exp").toString());
        if (System.currentTimeMillis() > exp * 1000) {
            return null;
        }
        String user = jwt.getPayload().toString();
        return user;
    }


}
