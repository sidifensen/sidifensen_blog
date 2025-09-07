package com.sidifensen.utils;

import cn.hutool.core.convert.NumberWithFormat;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.signers.JWTSignerUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtils {

    @Value("${sidifensen.jwt}")
    private String secret;

    /**
     * 生成token
     * 
     * @param id           用户id
     * @param isRememberMe 是否记住我
     * @return
     */
    public String createToken(Integer id, boolean isRememberMe) {
        String token = JWT.create()
                // 设置过期时间 rememberMe为true时，token有效期为7天，否则为1天
                .setExpiresAt(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(isRememberMe ? 7 : 1)))
                .addPayloads(Map.of("id", id))
                .setSigner(JWTSignerUtil.createSigner("HS256", secret.getBytes()))
                .sign();
        return token;
    }

    /**
     * 解析并验证token
     * 
     * @param token
     * @return
     */
    public Integer parseToken(String token) {
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

        Object claim = jwt.getPayload().getClaim("id");
        if (claim instanceof NumberWithFormat) {
            Number number = (Number) ((NumberWithFormat) claim).getNumber();
            return number.intValue(); // 转换为Integer类型
        }
        return null;
    }

}
