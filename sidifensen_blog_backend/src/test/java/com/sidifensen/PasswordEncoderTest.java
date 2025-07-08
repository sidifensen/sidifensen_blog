package com.sidifensen;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

public class PasswordEncoderTest {

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    public void encodePasswordTest() {
        String password = "123456";
        String encodedPassword = passwordEncoder.encode(password);

        System.out.println("密码: " + encodedPassword);
        LocalDateTime now = LocalDateTime.now();
        System.out.println(now);
    }
}
