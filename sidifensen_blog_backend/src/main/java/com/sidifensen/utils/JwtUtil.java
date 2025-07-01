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

    @Value("${spring.security.jwt.secret:sidifensen}")
    private String secret;

    @Value("${spring.security.jwt.expire:1}")
    private int expire;

    /**
     * 生成token
     * @param userDto
     * @return
     */
    public String createToken(UserDto userDto) {
        String token = JWT.create()
                .setExpiresAt(new Date(System.currentTimeMillis() + expire * 24 * 60 * 60 * 1000))
                .addPayloads(BeanUtil.beanToMap(userDto))
                .setSigner(JWTSignerUtil.createSigner("HS256", secret.getBytes()))
                .sign();
        return token;
    }

    /**
     * 解析token
     * @param token
     * @return
     */
    public String parseToken(String token) {
        if (!JWTUtil.verify(token, secret.getBytes())) {
            throw new BlogException("token验证失败");
        }
        JWT jwt = JWTUtil.parseToken(token);
        // 判断是否过期
        long exp = Long.parseLong(jwt.getPayload().getClaim("exp").toString());
        if (System.currentTimeMillis() > exp * 1000) {
            throw new BlogException("token已过期");
        }
        String user = jwt.getPayload().toString();
        return user;
    }


}
