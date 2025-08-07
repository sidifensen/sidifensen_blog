package com.sidifensen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.constants.RabbitMQConstants;
import com.sidifensen.domain.dto.*;
import com.sidifensen.domain.entity.Album;
import com.sidifensen.domain.entity.LoginUser;
import com.sidifensen.domain.entity.Photo;
import com.sidifensen.domain.entity.SysUser;
import com.sidifensen.domain.vo.SysUserVo;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.AlbumMapper;
import com.sidifensen.mapper.PhotoMapper;
import com.sidifensen.mapper.SysUserMapper;
import com.sidifensen.redis.RedisComponent;
import com.sidifensen.service.ISysUserService;
import com.sidifensen.utils.JwtUtils;
import com.sidifensen.utils.SecurityUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private JwtUtils jwtUtils;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private RabbitTemplate rabbitTemplate;

    // Spring Security的密码加密器
    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private AlbumMapper albumMapper;

    @Resource
    private PhotoMapper photoMapper;

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
            // 创建token,此处的token时由UUID编码而成JWT字符串
            String token = jwtUtils.createToken(loginUser.getSysUser().getId(), loginDto.getRememberMe());
            return token;
        } finally {
            redisComponent.cleanCheckCode(loginDto.getCheckCodeKey());
        }

    }

    @Override
    public void register(RegisterDto registerDto) {
        // 校验验证码
        if (!registerDto.getEmailCheckCode().equals(redisComponent.getEmailCheckCode(registerDto.getEmail(), "register"))) {
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
    public void sendEmailCheckCode(EmailDto emailDto) {
        String email = emailDto.getEmail();
        // 如果是重置密码, 则需要先校验邮箱是否存在
        if (emailDto.getType().equals("reset")) {
            SysUser sysUser = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getEmail, email));
            if (ObjectUtil.isNull(sysUser)) {
                throw new BlogException(BlogConstants.NotFoundEmail);// 邮箱不存在
            }
        }
        //生成六位数的验证码
        String checkCode = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
        //将验证码存入redis
        redisComponent.saveEmailCheckCode(email, emailDto.getType(), checkCode);
        // 发送邮件
        HashMap<String, Object> sendEmail = new HashMap<>();
        sendEmail.put("email", email);
        sendEmail.put("checkCode", checkCode);
        sendEmail.put("type", emailDto.getType());
        rabbitTemplate.convertAndSend(RabbitMQConstants.Email_Exchange, RabbitMQConstants.Email_Routing_Key, sendEmail);
        log.info("生产者向rabbitmq发送请求：{}", emailDto);
    }

    @Override
    public void verifyReset(VerifyResetDto verifyResetDto) {
        if (!verifyResetDto.getEmailCheckCode().equals(redisComponent.getEmailCheckCode(verifyResetDto.getEmail(), "reset"))) {
            throw new BlogException(BlogConstants.CheckCodeError); // 验证码错误
        }
    }

    @Override
    public void resetPassword(ResetPasswordDto resetPasswordDto) {
        // 如果新密码与老密码相同
        SysUser sysUser = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getEmail, resetPasswordDto.getEmail()));
        if (ObjectUtil.isNull(sysUser)) {
            throw new BlogException(BlogConstants.NotFoundEmail); // 邮箱不存在
        }
        if (passwordEncoder.matches(resetPasswordDto.getPassword(), sysUser.getPassword())) { // security的密码加密器对比密码用matches方法
            throw new BlogException(BlogConstants.NewPasswordSameAsOld); // 新密码不能与原密码相同
        }
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<SysUser>().eq(SysUser::getEmail, resetPasswordDto.getEmail());
        SysUser newSysUser = new SysUser().setPassword(passwordEncoder.encode(resetPasswordDto.getPassword()));
        sysUserMapper.update(newSysUser, queryWrapper);

        redisComponent.cleanEmailCheckCode(resetPasswordDto.getEmail(), "reset");
    }

    @Override
    public SysUserVo info() {
        Integer userId = SecurityUtils.getUserId();
        SysUser sysUser = sysUserMapper.selectById(userId);
        if (sysUser == null) {
            throw new BlogException(BlogConstants.NotFoundUser); // 用户不存在
        }
        SysUserVo sysUserVo = new SysUserVo();
        sysUserVo.setId(sysUser.getId());
        sysUserVo.setNickname(sysUser.getNickname());
        sysUserVo.setUsername(sysUser.getUsername());
        sysUserVo.setEmail(sysUser.getEmail());
        sysUserVo.setIntroduction(sysUser.getIntroduction());
        sysUserVo.setAvatar(sysUser.getAvatar());
        sysUserVo.setSex(sysUser.getSex());
        return sysUserVo;
    }


    // 删除用户
    @Override
    public void deleteUser(Long id) {
        sysUserMapper.deleteById(id);
        // TODO 异步删除用户相关信息
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        try {
            executor.submit(() -> {
                // 删除用户相册
                albumMapper.delete(new LambdaQueryWrapper<Album>().eq(Album::getUserId, id));
                // 删除用户照片
                photoMapper.delete(new LambdaQueryWrapper<Photo>().eq(Photo::getUserId, id));
            });
        } catch (Exception e) {
            log.error("异步删除用户相关数据失败，用户ID: {}", id, e);
        } finally {
            executor.shutdown();
        }

    }


    // 管理员登录
    @Override
    public String adminLogin(AdminLoginDto adminLoginDto) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(adminLoginDto.getUsername(), adminLoginDto.getPassword());
        // 调用loadUserByUsername方法
        Authentication authenticate = authenticationManager.authenticate(authentication);
        // 获取用户信息，返回的就是UserDetails
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        // 创建token,此处的token时由UUID编码而成JWT字符串
        String token = jwtUtils.createToken(loginUser.getSysUser().getId(), adminLoginDto.getRememberMe());
        return token;
    }

    // 管理端获取用户信息
    @Override
    public SysUserVo getAdminInfo() {
        SysUser user = SecurityUtils.getUser();
        if (ObjectUtil.isEmpty(user)) {
            return null;
        }

        SysUserVo sysUserVo = BeanUtil.copyProperties(user, SysUserVo.class);
        return sysUserVo;
    }


}
