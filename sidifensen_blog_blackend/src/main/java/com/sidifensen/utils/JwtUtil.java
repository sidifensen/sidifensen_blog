package com.sidifensen.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import com.sidifensen.domain.dto.UserDto;
import org.springframework.stereotype.Component;


import java.util.Map;

@Component
public class JwtUtil {

    // 这里的secret可以自己设置，建议使用复杂的字符
    private static final String SECRET = "sidifensen";

    /**
     * 生成token
     * @param userDto
     * @return
     */
    public String createToken(UserDto userDto) {
        Map<String, Object> map = BeanUtil.beanToMap(userDto);
        JWTSigner jwtSigner = JWTSignerUtil.createSigner("HS256",SECRET.getBytes());
        String token = JWTUtil.createToken(map, jwtSigner);
        return token;
    }

    /**
     * 解析token
     * @param token
     * @return
     */
    public String parseToken(String token) {
        JWT jwt = JWTUtil.parseToken(token);
        String string = jwt.getPayload().toString();
        return string;
    }


}
