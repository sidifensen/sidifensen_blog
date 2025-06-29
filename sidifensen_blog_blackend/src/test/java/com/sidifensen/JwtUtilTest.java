package com.sidifensen;

import com.sidifensen.domain.dto.UserDto;
import com.sidifensen.utils.JwtUtil;
import org.junit.jupiter.api.Test;


public class JwtUtilTest {

    JwtUtil jwtUtil = new JwtUtil();

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

}
