package com.sidifensen;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.NumberWithFormat;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.signers.JWTSignerUtil;
import com.sidifensen.domain.dto.UserDto;
import com.sidifensen.exception.BlogException;
import com.sidifensen.utils.JwtUtil;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.Map;

@SpringBootTest
public class JwtUtilTest {

    @Resource
    private JwtUtil jwtUtil;

    @Test
    public void testCreateToken() {
        Integer id = 1;
        String userName = "sidifensen";
        String nickName = "斯蒂芬森";
        String email = "sidifensen@163.com";
        Integer sex = 0;
        String introduction = "斯蒂芬森的个人博客";
        String avatar = "https://www.sidifensen.com/avatar.jpg";
        UserDto userDto = new UserDto(id, userName, nickName, email, sex, introduction, avatar);
        String token = jwtUtil.createToken(userDto);
        System.out.println("token: " + token);
    }

    @Test
    public void testParseToken() {
        Integer id = 1;
        String userName = "sidifensen";
        String nickName = "斯蒂芬森";
        String email = "sidifensen@163.com";
        Integer sex = 0;
        String introduction = "斯蒂芬森的个人博客";
        String avatar = "https://www.sidifensen.com/avatar.jpg";
        UserDto userDto = new UserDto(id, userName, nickName, email, sex, introduction, avatar);
        String token = jwtUtil.createToken(userDto);

        String user = jwtUtil.parseToken(token);
        System.out.println("user: " + user);

    }

    @Test
    public void testJWTExpire(){
        Integer id = 1;
        String userName = "sidifensen";
        String nickName = "斯蒂芬森";
        String email = "sidifensen@163.com";
        Integer sex = 0;
        String introduction = "斯蒂芬森的个人博客";
        String avatar = "https://www.sidifensen.com/avatar.jpg";
        UserDto userDto = new UserDto(id, userName, nickName, email, sex, introduction, avatar);

        Map<String, Object> map = BeanUtil.beanToMap(userDto);

        String token = JWT.create()
                .setExpiresAt(new Date(System.currentTimeMillis() + 1000 * 1))
                .addPayloads(map)
                .setSigner(JWTSignerUtil.createSigner("HS256", "sidifensen".getBytes()))
                .sign();

        System.out.println(token);

        //线程休眠2秒钟
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (!JWTUtil.verify(token, "sidifensen".getBytes())) {
            throw new BlogException("token验证失败");
        }
        JWT jwt = JWTUtil.parseToken(token);
        // 判断是否过期
        long exp = Long.parseLong(jwt.getPayload().getClaim("exp").toString());
        if (System.currentTimeMillis() > exp ) {
            System.out.println("token已过期");
            throw new BlogException("token已过期");
        }

        boolean verify = JWTUtil.verify(token, "sidifensen".getBytes());
        System.out.println(verify );

    }

    @Test
    public void testJWT(){
        Integer id = 1;
        String userName = "sidifensen";
        String nickName = "斯蒂芬森";
        String email = "sidifensen@163.com";
        Integer sex = 0;
        String introduction = "斯蒂芬森的个人博客";
        String avatar = "https://www.sidifensen.com/avatar.jpg";
        UserDto userDto = new UserDto(id, userName, nickName, email, sex, introduction, avatar);

        Map<String, Object> map = BeanUtil.beanToMap(userDto);

        String token = JWT.create()
                .setExpiresAt(new Date(System.currentTimeMillis() + 1000 * 1))
                .addPayloads(map)
                .setSigner(JWTSignerUtil.createSigner("HS256", "sidifensen".getBytes()))
                .sign();

        System.out.println(token);

        //线程休眠2秒钟
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String string = jwtUtil.parseToken(token);
        System.out.println(string);

    }

}
