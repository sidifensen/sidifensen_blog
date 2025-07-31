package com.sidifensen;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.signers.JWTSignerUtil;
import com.sidifensen.exception.BlogException;
import com.sidifensen.utils.JwtUtils;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class JwtUtilsTest {

    @Resource
    private JwtUtils jwtUtils;

    Integer id = 1;
    String userName = "sidifensen";
//    String nickName = "斯蒂芬森";
//    String email = "123456@qq.com";
//    Integer sex = 0;
//    String introduction = "斯蒂芬森的个人博客";
//    String avatar = "https://www.sidifensen.com/avatar.jpg";
//    UserDto userDto = new UserDto(id, userName, nickName, email, sex, introduction, avatar);
//    UserDto userDto = new UserDto(id,userName);

    @Test
    public void testCreateToken() {

        String token = jwtUtils.createToken(id,false);
        System.out.println("token: " + token);
    }

    @Test
    public void testParseToken() {
        String token = jwtUtils.createToken(id,false);

        Long id = jwtUtils.parseToken(token);
        System.out.println("userId: " + id);

    }

    @Test
    public void testJWTExpire(){

//        Map<String, Object> map = BeanUtil.beanToMap(userDto);

        String token = JWT.create()
                .setExpiresAt(new Date(System.currentTimeMillis() + 1000 * 1))
                .addPayloads(Map.of("id",id))
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
//        Map<String, Object> map = BeanUtil.beanToMap(userDto);

        String token = JWT.create()
                .setExpiresAt(new Date(System.currentTimeMillis() + 1000 * 1))
                .addPayloads(Map.of("id",id))
                .setSigner(JWTSignerUtil.createSigner("HS256", "sidifensen".getBytes()))
                .sign();

        System.out.println(token);

        //线程休眠2秒钟
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Long id = jwtUtils.parseToken(token);
        System.out.println(id);

    }


    @Test
    public void testJWTValidate() {
//        Map<String, Object> map = BeanUtil.beanToMap(userDto);

        String token = JWT.create()
                .setExpiresAt(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7)))
                .addPayloads(Map.of("id",id))
                .setSigner(JWTSignerUtil.createSigner("HS256", "sidifensen".getBytes()))
                .sign();

        System.out.println(token);
        Date date = new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7));
        System.out.println(date.getTime());
        LocalDateTime localDateTime = LocalDateTime.now();
        System.out.println(localDateTime);
        boolean verify = JWTUtil.verify(token, "sidifensen".getBytes());
        System.out.println("verify: " + verify);
        Long id = jwtUtils.parseToken(token);
        System.out.println(id);
    }

    @Test
    public void testNewJWT() {
//        Map<String, Object> map = BeanUtil.beanToMap(userDto);

        String token = JWT.create()
                .setExpiresAt(new Date(System.currentTimeMillis() + 1000 * 1))
                .addPayloads(Map.of("id",id))
                .setSigner(JWTSignerUtil.createSigner("HS256", "sidifensen".getBytes()))
                .sign();

        System.out.println(token);

    }

}
