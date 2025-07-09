package com.sidifensen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.constants.RabbitMQConstants;
import com.sidifensen.domain.dto.EmailDto;
import com.sidifensen.domain.dto.LoginDto;
import com.sidifensen.domain.dto.RegisterDto;
import com.sidifensen.domain.dto.UserDto;
import com.sidifensen.domain.entity.*;
import com.sidifensen.domain.enums.RegisterOrLoginTypeEnum;
import com.sidifensen.domain.enums.RoleEnum;
import com.sidifensen.domain.result.Result;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.*;
import com.sidifensen.redis.RedisComponent;
import com.sidifensen.service.ISysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.utils.JwtUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author sidifensen
 * @since 2025-06-29
 */
@Service
@Slf4j
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private RabbitTemplate rabbitTemplate;

    // Spring Security的密码加密器
    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public String login(LoginDto loginDto) {
        try {
            // 校验验证码
            if (!loginDto.getCheckCode().equals(redisComponent.getCheckCode(loginDto.getCheckCodeKey()))) {
                throw new BlogException(BlogConstants.CheckCodeError); //验证码错误
            }
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());
            // 调用loadUserByUsername方法
            Authentication authenticate = authenticationManager.authenticate(authentication);
            // 获取用户信息，返回的就是UserDetails
            LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
            // 将用户信息转换为UserDto
            UserDto userDto = BeanUtil.copyProperties(loginUser.getSysUser(), UserDto.class);
            // 创建token,此处的token时由UUID编码而成JWT字符串
            String token = jwtUtil.createToken(userDto, loginDto.getRememberMe());
            return token;
        } finally {
            redisComponent.cleanCheckCode(loginDto.getCheckCodeKey());
        }

    }

    @Override
    public void register(RegisterDto registerDto) {
        // 校验验证码
        if (!registerDto.getEmailCheckCode().equals(redisComponent.getEmailCheckCode(registerDto.getEmail()))) {
            throw new BlogException(BlogConstants.CheckCodeError);
        }

        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUsername, registerDto.getUsername())
                .or().eq(SysUser::getEmail, registerDto.getEmail());
        SysUser sysUser = sysUserMapper.selectOne(queryWrapper);
        if (ObjectUtil.isNotNull(sysUser)) {
            if (sysUser.getUsername().equals(registerDto.getUsername())) {
                throw new BlogException(BlogConstants.ExistUserName);// 用户名已存在
            } else if (sysUser.getEmail().equals(registerDto.getEmail())) {
                throw new BlogException(BlogConstants.ExistEmail);// 邮箱已存在
            }
        }

        // 注册用户
        SysUser user = new SysUser();
        user.setUsername(registerDto.getUsername());
        user.setNickname(registerDto.getUsername());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setEmail(registerDto.getEmail());

        int insert = sysUserMapper.insert(user);
        if (insert == 1) {
            log.info("用户{}注册成功", user.getUsername());
        }

    }


    @Override
    public void registerEmailCheckCode(EmailDto emailDto) {
        //生成六位数的验证码
        String checkCode = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
        //将验证码存入redis
        redisComponent.saveEmailCheckCode(emailDto.getEmail(), checkCode);
        // 发送邮件
        HashMap<String, Object> sendEmail = new HashMap<>();
        sendEmail.put("email", emailDto.getEmail());
        sendEmail.put("checkCode", checkCode);
        sendEmail.put("type", emailDto.getType());
        rabbitTemplate.convertAndSend(RabbitMQConstants.Email_Exchange, RabbitMQConstants.Email_Routing_Key, sendEmail);
    }


}
