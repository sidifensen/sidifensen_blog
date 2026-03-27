package com.sidifensen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.constants.RabbitMQConstants;
import com.sidifensen.domain.dto.*;
import com.sidifensen.domain.entity.*;
import com.sidifensen.domain.enums.*;
import com.sidifensen.domain.vo.*;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.*;
import com.sidifensen.redis.RedisComponent;
import com.sidifensen.security.SysUserDetailsService;
import com.sidifensen.service.IpService;
import com.sidifensen.service.SysUserRoleService;
import com.sidifensen.service.SysUserService;
import com.sidifensen.service.UserSettingsService;
import com.sidifensen.service.VipService;
import com.sidifensen.utils.IpUtils;
import com.sidifensen.utils.JwtUtils;
import com.sidifensen.utils.SecurityUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

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

    @Resource
    private SysUserDetailsService sysUserDetailsService;

    @Resource
    private IpUtils ipUtils;

    @Resource
    private IpService ipService;

    @Resource
    private SysUserRoleService sysUserRoleService;

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private ColumnMapper columnMapper;

    @Resource
    private com.sidifensen.service.SysLoginLogService sysLoginLogService;

    @Resource
    private VipService vipService;

    @Resource
    private UserSettingsService userSettingsService;

    @Override
    public String login(LoginDto loginDto) {
        try {
            // 校验验证码
            if (!loginDto.getCheckCode().equals(redisComponent.getCheckCode(loginDto.getCheckCodeKey()))) {
                throw new BlogException(BlogConstants.CheckCodeError); // 验证码错误
            }
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    loginDto.getUsername(), loginDto.getPassword());
            // 调用loadUserByUsername方法
            Authentication authenticate = authenticationManager.authenticate(authentication);
            // 获取用户信息，返回的就是UserDetails
            LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
            // 创建token,此处的token时由UUID编码而成JWT字符串
            String token = jwtUtils.createToken(loginUser.getSysUser().getId(), loginDto.getRememberMe());
            return token;
        } catch (Exception e) {
            // 记录登录失败日志
            String ip = ipUtils.getIp();
            sysLoginLogService.recordLoginLog(
                    null, // 登录失败时可能无法获取用户ID
                    loginDto.getUsername(),
                    RegisterOrLoginTypeEnum.EMAIL.getCode(), // 0-用户名/邮箱登录
                    ip,
                    LoginStatusEnum.FAIL.getCode() // 1-失败
            );
            throw e; // 重新抛出异常，让全局异常处理器处理
        } finally {
            redisComponent.cleanCheckCode(loginDto.getCheckCodeKey());
        }

    }

    @Override
    public void register(RegisterDto registerDto) {
        // 必须校验验证码，不能为空
        if (ObjectUtil.isEmpty(registerDto.getEmailCheckCode())) {
            throw new BlogException(BlogConstants.CheckCodeError);
        }

        // 校验验证码是否正确
        if (!registerDto.getEmailCheckCode()
            .equals(redisComponent.getEmailCheckCode(registerDto.getEmail(), MailEnum.REGISTER.getType()))) {
            throw new BlogException(BlogConstants.CheckCodeError);
        }

        // 分别检查用户名和邮箱是否存在，避免or条件导致的逻辑错误
        LambdaQueryWrapper<SysUser> usernameQuery = new LambdaQueryWrapper<>();
        usernameQuery.eq(SysUser::getUsername, registerDto.getUsername());
        SysUser existUsername = sysUserMapper.selectOne(usernameQuery);
        if (ObjectUtil.isNotNull(existUsername)) {
            throw new BlogException(BlogConstants.ExistUserName);// 用户名已存在
        }

        LambdaQueryWrapper<SysUser> emailQuery = new LambdaQueryWrapper<>();
        emailQuery.eq(SysUser::getEmail, registerDto.getEmail());
        SysUser existEmail = sysUserMapper.selectOne(emailQuery);
        if (ObjectUtil.isNotNull(existEmail)) {
            throw new BlogException(BlogConstants.ExistEmail);// 邮箱已存在
        }

        // 注册用户
        SysUser user = new SysUser();
        user.setUsername(registerDto.getUsername());
        user.setNickname(registerDto.getUsername());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setEmail(registerDto.getEmail());
        user.setRegisterIp(ipUtils.getIp());
        user.setRegisterAddress(ipUtils.getAddress());
        user.setStatus(StatusEnum.NORMAL.getStatus());
        String ip = ipUtils.getIp();

        int insert = sysUserMapper.insert(user);
        if (insert == 1) {
            Integer userId = user.getId();

            // 异步创建用户设置
            ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
            try {
                executor.submit(() -> {
                    try {
                        userSettingsService.createDefaultSettings(userId);
                    } catch (Exception e) {
                        log.error("异步创建用户设置失败，userId={}", userId, e);
                    }
                });
            } finally {
                executor.shutdown();
            }

            ipService.setRegisterIp(user.getId(), ip);
            sysUserRoleService.setRegisterRole(user.getId());
            redisComponent.cleanEmailCheckCode(registerDto.getEmail(), MailEnum.REGISTER.getType());
        }

    }

    @Override
    public void sendEmailCheckCode(EmailDto emailDto) {
        String email = emailDto.getEmail();
        // 如果是重置密码, 则需要先校验邮箱是否存在
        if (emailDto.getType().equals(MailEnum.RESET_PASSWORD.getType())
                || emailDto.getType().equals(MailEnum.RESET_EMAIL.getType())) {
            SysUser sysUser = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getEmail, email));
            if (ObjectUtil.isNull(sysUser)) {
                throw new BlogException(BlogConstants.NotFoundEmail);// 邮箱不存在
            }
        }
        // 生成六位数的验证码
        String checkCode = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
        // 将验证码存入redis
        redisComponent.saveEmailCheckCode(email, emailDto.getType(), checkCode);
        // 发送邮件
        HashMap<String, Object> sendEmail = new HashMap<>();
        sendEmail.put("email", email);
        sendEmail.put("checkCode", checkCode);
        sendEmail.put("type", emailDto.getType());
        rabbitTemplate.convertAndSend(RabbitMQConstants.Email_Exchange, RabbitMQConstants.Email_Routing_Key, sendEmail);
    }

    @Override
    public void verifyResetPassword(VerifyResetDto verifyResetDto) {
        if (!verifyResetDto.getEmailCheckCode()
                .equals(redisComponent.getEmailCheckCode(verifyResetDto.getEmail(),
                        MailEnum.RESET_PASSWORD.getType()))) {
            throw new BlogException(BlogConstants.CheckCodeError); // 验证码错误
        }
    }

    @Override
    public void resetPassword(ResetPasswordDto resetPasswordDto) {
        // 校验邮箱验证码
        if (!resetPasswordDto.getEmailCheckCode()
                .equals(redisComponent.getEmailCheckCode(resetPasswordDto.getEmail(),
                        MailEnum.RESET_PASSWORD.getType()))) {
            throw new BlogException(BlogConstants.CheckCodeError);
        }

        // 查询用户
        SysUser sysUser = sysUserMapper
                .selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getEmail, resetPasswordDto.getEmail()));
        if (ObjectUtil.isNull(sysUser)) {
            throw new BlogException(BlogConstants.NotFoundEmail); // 邮箱不存在
        }

        // 检查新密码是否与原密码相同
        if (passwordEncoder.matches(resetPasswordDto.getPassword(), sysUser.getPassword())) {
            throw new BlogException(BlogConstants.NewPasswordSameAsOld); // 新密码不能与原密码相同
        }

        // 更新密码
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<SysUser>().eq(SysUser::getEmail,
                resetPasswordDto.getEmail());
        SysUser newSysUser = new SysUser().setPassword(passwordEncoder.encode(resetPasswordDto.getPassword()));
        sysUserMapper.update(newSysUser, queryWrapper);

        // 清除验证码
        redisComponent.cleanEmailCheckCode(resetPasswordDto.getEmail(), MailEnum.RESET_PASSWORD.getType());
    }

    @Override
    public void verifyResetEmail(VerifyEmailDto verifyEmailDto) {
        // 校验原邮箱的验证码
        if (!verifyEmailDto.getEmailCheckCode()
                .equals(redisComponent.getEmailCheckCode(verifyEmailDto.getEmail(), MailEnum.RESET_EMAIL.getType()))) {
            throw new BlogException(BlogConstants.CheckCodeError);
        }
    }

    @Override
    public void resetEmail(UpdateEmailDto updateEmailDto) {
        // 获取当前登录用户ID
        Integer userId = SecurityUtils.getUserId();

        // 查询当前用户
        SysUser currentUser = sysUserMapper.selectById(userId);
        if (currentUser == null) {
            throw new BlogException(BlogConstants.NotFoundUser);
        }

        // 校验原邮箱是否是当前用户的邮箱
        if (!currentUser.getEmail().equals(updateEmailDto.getEmail())) {
            throw new BlogException(BlogConstants.EmailNotMatch);
        }

        // 校验原邮箱的验证码
        if (!updateEmailDto.getEmailCheckCode()
                .equals(redisComponent.getEmailCheckCode(updateEmailDto.getEmail(), MailEnum.RESET_EMAIL.getType()))) {
            throw new BlogException(BlogConstants.CheckCodeError);
        }

        // 检查新邮箱是否已被其他用户使用
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getEmail, updateEmailDto.getNewEmail());
        SysUser existUser = sysUserMapper.selectOne(queryWrapper);
        if (existUser != null) {
            throw new BlogException(BlogConstants.ExistEmail);
        }

        // 更新为新邮箱
        LambdaUpdateWrapper<SysUser> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SysUser::getId, userId)
                .set(SysUser::getEmail, updateEmailDto.getNewEmail());

        int result = sysUserMapper.update(null, updateWrapper);
        if (result != 1) {
            throw new BlogException(BlogConstants.UpdateUserInfoError);
        }

        // 清除原邮箱的验证码
        redisComponent.cleanEmailCheckCode(updateEmailDto.getEmail(), MailEnum.RESET_EMAIL.getType());
    }

    @Override
    public SysUserVo info() {
        Integer userId = SecurityUtils.getUserId();
        log.info("【OAuth调试】info() - SecurityUtils.getUserId()={}, SecurityContext={}", userId, SecurityContextHolder.getContext().getAuthentication());
        SysUser sysUser = sysUserMapper.selectById(userId);
        if (sysUser == null) {
            log.error("【OAuth调试】info() - 用户查询为空, userId={}", userId);
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
        sysUserVo.setFansCount(sysUser.getFansCount());
        sysUserVo.setFollowCount(sysUser.getFollowCount());
        // 从 user_settings 表读取邮件通知设置
        sysUserVo.setIsReceivePrivateMessageEmail(userSettingsService.getReceivePrivateMessageEmail(userId));
        sysUserVo.setIsReceiveCommentEmail(userSettingsService.getReceiveCommentEmail(userId));
        sysUserVo.setIsReceiveSystemEmail(userSettingsService.getReceiveSystemEmail(userId));
        fillVipInfo(sysUserVo, sysUser.getId());
        return sysUserVo;
    }

    @Override
    public SysUserVo getUserInfoById(Integer userId) {
        SysUser sysUser = sysUserMapper.selectById(userId);
        if (sysUser == null) {
            throw new BlogException(BlogConstants.NotFoundUser); // 用户不存在
        }
        SysUserVo sysUserVo = new SysUserVo();
        sysUserVo.setId(sysUser.getId());
        sysUserVo.setNickname(sysUser.getNickname());
        sysUserVo.setUsername(sysUser.getUsername());
        sysUserVo.setIntroduction(sysUser.getIntroduction());
        sysUserVo.setAvatar(sysUser.getAvatar());
        sysUserVo.setSex(sysUser.getSex());
        sysUserVo.setCreateTime(sysUser.getCreateTime());
        String loginAddress = processLoginAddress(sysUser.getLoginAddress());
        sysUserVo.setLoginAddress(loginAddress);
        sysUserVo.setFansCount(sysUser.getFansCount());
        sysUserVo.setFollowCount(sysUser.getFollowCount());

        // 实时查询已发布且审核通过的文章数量
        LambdaQueryWrapper<Article> articleQuery = new LambdaQueryWrapper<>();
        articleQuery.eq(Article::getUserId, userId)
                .eq(Article::getEditStatus, EditStatusEnum.PUBLISHED.getCode()) // 已发布
                .eq(Article::getExamineStatus, ExamineStatusEnum.PASS.getCode()); // 审核通过
        Integer articleCount = Math.toIntExact(articleMapper.selectCount(articleQuery));
        sysUserVo.setArticleCount(articleCount);
        fillVipInfo(sysUserVo, sysUser.getId());

        return sysUserVo;
    }

    @Override
    public void updateUserInfo(UpdateUserInfoDto updateUserInfoDto) {
        // 获取当前登录用户ID
        Integer userId = SecurityUtils.getUserId();

        // 查询当前用户
        SysUser currentUser = sysUserMapper.selectById(userId);
        if (currentUser == null) {
            throw new BlogException(BlogConstants.NotFoundUser);
        }

        LambdaUpdateWrapper<SysUser> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SysUser::getId, userId)
                .set(ObjectUtil.isNotEmpty(updateUserInfoDto.getNickname()), SysUser::getNickname,
                        updateUserInfoDto.getNickname())
                .set(ObjectUtil.isNotNull(updateUserInfoDto.getSex()), SysUser::getSex, updateUserInfoDto.getSex())
                .set(SysUser::getIntroduction, updateUserInfoDto.getIntroduction())
                .set(ObjectUtil.isNotEmpty(updateUserInfoDto.getAvatar()), SysUser::getAvatar,
                        updateUserInfoDto.getAvatar());

        int result = sysUserMapper.update(null, updateWrapper);
        if (result != 1) {
            throw new BlogException(BlogConstants.UpdateUserInfoError);
        }

        // 更新私信邮件通知设置到 user_settings 表
        if (ObjectUtil.isNotNull(updateUserInfoDto.getIsReceivePrivateMessageEmail())) {
            userSettingsService.setReceivePrivateMessageEmail(userId, updateUserInfoDto.getIsReceivePrivateMessageEmail());
        }

    }

    /**
     * 处理登录地址显示
     * 如果不是中国开头的，截取第一个-之前的内容
     * 如果是中国开头的，截取第二段文字（江西、上海等省份）
     *
     * @param loginAddress 原始登录地址，格式如：中国-广东-广州 或 美国-纽约
     * @return 处理后的地址
     */
    private String processLoginAddress(String loginAddress) {
        if (ObjectUtil.isEmpty(loginAddress)) {
            return loginAddress;
        }

        // 按-分割地址
        String[] addressParts = loginAddress.split("-");

        if (addressParts.length == 0) {
            return loginAddress;
        }

        // 如果是中国开头，返回第二段（省份）
        if ("中国".equals(addressParts[0]) && addressParts.length > 1) {
            return addressParts[1];
        }

        // 否则返回第一段（国家）
        return addressParts[0];
    }

    /**
     * 用户态统一补齐 VIP 信息，避免多个接口重复拼装。
     */
    private void fillVipInfo(SysUserVo sysUserVo, Integer userId) {
        sysUserVo.setIsVip(vipService.hasVipAccess(userId));
        sysUserVo.setVipStatus(vipService.getVipStatus(userId));
        sysUserVo.setVipExpireTime(vipService.getVipExpireTime(userId));
    }

    // 管理员登录
    @Override
    public String adminLogin(AdminLoginDto adminLoginDto) {
        try {
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    adminLoginDto.getUsername(), adminLoginDto.getPassword());
            // 调用loadUserByUsername方法
            Authentication authenticate = authenticationManager.authenticate(authentication);
            // 获取用户信息
            LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
            List<SysRole> sysRoles = loginUser.getSysUser().getSysRoles();

            if (sysRoles.stream().noneMatch(r -> r.getRole().equals("admin") || r.getRole().equals("viewer"))) {
                throw new BlogException(BlogConstants.NotAdminAccount); // 不是管理后台账户
            }
            // 创建token,此处的token时由UUID编码而成JWT字符串
            String token = jwtUtils.createToken(loginUser.getSysUser().getId(), adminLoginDto.getRememberMe());
            return token;
        } catch (Exception e) {
            // 记录管理员登录失败日志
            String ip = ipUtils.getIp();
            sysLoginLogService.recordLoginLog(
                    null, // 登录失败时可能无法获取用户ID
                    adminLoginDto.getUsername(),
                    RegisterOrLoginTypeEnum.EMAIL.getCode(), // 0-用户名/邮箱登录
                    ip,
                    LoginStatusEnum.FAIL.getCode() // 1-失败
            );
            throw e; // 重新抛出异常，让全局异常处理器处理
        }
    }

    // 管理端获取用户信息
    @Override
    public SysUserVo getAdminInfo() {
        // 获取用户信息
        SysUser user = SecurityUtils.getUser();
        if (ObjectUtil.isEmpty(user)) {
            return null;
        }

        List<SysRole> sysRoles = user.getSysRoles();

        if (sysRoles.stream().noneMatch(r -> r.getRole().equals("admin") || r.getRole().equals("viewer"))) {
            throw new BlogException(BlogConstants.NotAdminAccount); // 不是管理员账户
        }

        SysUserVo sysUserVo = BeanUtil.copyProperties(user, SysUserVo.class);
        return sysUserVo;
    }

    // 管理端获取用户列表
    @Override
    public List<SysUserVo> listUser() {
        List<SysUser> sysUsers = sysUserMapper.selectList(new LambdaQueryWrapper<SysUser>().orderByAsc(SysUser::getId));
        List<SysUserVo> sysUserVos = BeanUtil.copyToList(sysUsers, SysUserVo.class);
        maskEmailsForNonAdmin(sysUserVos);
        return sysUserVos;
    }

    @Override
    public PageVo<List<SysUserVo>> pageUser(Integer pageNum, Integer pageSize) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<SysUser>().orderByAsc(SysUser::getId);
        Page<SysUser> userPage = sysUserMapper.selectPage(page, queryWrapper);
        List<SysUserVo> sysUserVos = BeanUtil.copyToList(userPage.getRecords(), SysUserVo.class);
        maskEmailsForNonAdmin(sysUserVos);
        return new PageVo<>(sysUserVos, userPage.getTotal());
    }

    // 管理端获取用户列表（包含文章数量）
    @Override
    public List<SysUserWithArticleCountVo> listUserWithArticleCount() {
        List<SysUser> sysUsers = sysUserMapper.selectList(null);
        List<SysUserWithArticleCountVo> sysUserVos = BeanUtil.copyToList(sysUsers, SysUserWithArticleCountVo.class);

        // 为每个用户设置文章数量
        for (SysUserWithArticleCountVo userVo : sysUserVos) {
            LambdaQueryWrapper<Article> articleQuery = new LambdaQueryWrapper<Article>()
                    .eq(Article::getUserId, userVo.getId());
            Integer articleCount = Math.toIntExact(articleMapper.selectCount(articleQuery));
            userVo.setArticleCount(articleCount);
        }

        return sysUserVos;
    }

    // 管理端获取用户列表（包含评论数量）
    @Override
    public List<SysUserWithCommentCountVo> listUserWithCommentCount() {
        List<SysUser> sysUsers = sysUserMapper.selectList(null);
        List<SysUserWithCommentCountVo> sysUserVos = BeanUtil.copyToList(sysUsers, SysUserWithCommentCountVo.class);

        // 为每个用户设置评论数量
        for (SysUserWithCommentCountVo userVo : sysUserVos) {
            LambdaQueryWrapper<Comment> commentQuery = new LambdaQueryWrapper<Comment>()
                    .eq(Comment::getUserId, userVo.getId());
            Integer commentCount = Math.toIntExact(commentMapper.selectCount(commentQuery));
            userVo.setCommentCount(commentCount);
        }

        return sysUserVos;
    }

    @Override
    public List<SysUserWithColumnCountVo> listUserWithColumnCount() {
        List<SysUser> sysUsers = sysUserMapper.selectList(null);
        List<SysUserWithColumnCountVo> sysUserVos = BeanUtil.copyToList(sysUsers, SysUserWithColumnCountVo.class);

        // 为每个用户设置专栏数量
        for (SysUserWithColumnCountVo userVo : sysUserVos) {
            LambdaQueryWrapper<Column> columnQuery = new LambdaQueryWrapper<Column>()
                    .eq(Column::getUserId, userVo.getId());
            Integer columnCount = Math.toIntExact(columnMapper.selectCount(columnQuery));
            userVo.setColumnCount(columnCount);
        }

        return sysUserVos;
    }

    // 管理端更新用户信息
    @Override
    public void updateUser(SysUserDto sysUserDto) {
        SysUser sysUser = BeanUtil.copyProperties(sysUserDto, SysUser.class);
        sysUserMapper.updateById(sysUser);
    }

    // 管理端删除用户
    @Override
    public void deleteUser(Integer id) {
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

    // 管理端搜索角色
    @Override
    public List<SysUserVo> searchUser(SysUserSearchDTO sysUserSearchDTO) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(ObjectUtil.isNotEmpty(sysUserSearchDTO.getUsername()), SysUser::getUsername,
                sysUserSearchDTO.getUsername());
        queryWrapper.like(ObjectUtil.isNotEmpty(sysUserSearchDTO.getEmail()), SysUser::getEmail,
                sysUserSearchDTO.getEmail());
        queryWrapper.eq(ObjectUtil.isNotEmpty(sysUserSearchDTO.getStatus()), SysUser::getStatus,
                sysUserSearchDTO.getStatus());
        queryWrapper.ge(ObjectUtil.isNotEmpty(sysUserSearchDTO.getCreateTimeStart()), SysUser::getCreateTime,
                sysUserSearchDTO.getCreateTimeStart());
        queryWrapper.le(ObjectUtil.isNotEmpty(sysUserSearchDTO.getCreateTimeEnd()), SysUser::getCreateTime,
                sysUserSearchDTO.getCreateTimeEnd());
        queryWrapper.orderByAsc(SysUser::getId);

        List<SysUser> sysUsers = sysUserMapper.selectList(queryWrapper);
        List<SysUserVo> sysUserVos = BeanUtil.copyToList(sysUsers, SysUserVo.class);
        maskEmailsForNonAdmin(sysUserVos);
        return sysUserVos;
    }

    @Override
    public PageVo<List<SysUserVo>> searchUserPage(SysUserSearchDTO sysUserSearchDTO) {
        Page<SysUser> page = new Page<>(sysUserSearchDTO.getPageNum(), sysUserSearchDTO.getPageSize());
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(ObjectUtil.isNotEmpty(sysUserSearchDTO.getUsername()), SysUser::getUsername,
                sysUserSearchDTO.getUsername());
        queryWrapper.like(ObjectUtil.isNotEmpty(sysUserSearchDTO.getEmail()), SysUser::getEmail,
                sysUserSearchDTO.getEmail());
        queryWrapper.eq(ObjectUtil.isNotEmpty(sysUserSearchDTO.getStatus()), SysUser::getStatus,
                sysUserSearchDTO.getStatus());
        queryWrapper.ge(ObjectUtil.isNotEmpty(sysUserSearchDTO.getCreateTimeStart()), SysUser::getCreateTime,
                sysUserSearchDTO.getCreateTimeStart());
        queryWrapper.le(ObjectUtil.isNotEmpty(sysUserSearchDTO.getCreateTimeEnd()), SysUser::getCreateTime,
                sysUserSearchDTO.getCreateTimeEnd());
        queryWrapper.orderByAsc(SysUser::getId);

        Page<SysUser> userPage = sysUserMapper.selectPage(page, queryWrapper);
        List<SysUserVo> sysUserVos = BeanUtil.copyToList(userPage.getRecords(), SysUserVo.class);
        maskEmailsForNonAdmin(sysUserVos);
        return new PageVo<>(sysUserVos, userPage.getTotal());
    }

    // 管理端获取用户详情
    @Override
    public SysUserDetailVo getUserInfo(Integer userId) {
        SysUser sysUser = sysUserMapper.selectById(userId);
        SysUser sysUserDetail = sysUserDetailsService.setUserDetail(sysUser);
        SysUserDetailVo sysUserDetailVo = BeanUtil.copyProperties(sysUserDetail, SysUserDetailVo.class);
        return sysUserDetailVo;
    }

    // 管理端获取用户总数统计
    @Override
    public Long getUserTotalCount() {
        return sysUserMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getIsDeleted, 0)); // 只统计未删除的用户
    }

    @Override
    public List<SysUserVo> getRecommendedAuthors(Integer limit) {
        // 按文章数量排序，获取活跃作者（已发布且审核通过的文章数量）
        // 先获取所有用户，然后按文章数量排序
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getIsDeleted, 0) // 只统计未删除的用户
                .orderByDesc(SysUser::getFansCount) // 按粉丝数降序
                .last("LIMIT " + limit);

        List<SysUser> users = this.list(queryWrapper);
        List<SysUserVo> result = BeanUtil.copyToList(users, SysUserVo.class);

        // 为每个用户设置文章数量
        for (SysUserVo userVo : result) {
            LambdaQueryWrapper<Article> articleQuery = new LambdaQueryWrapper<Article>()
                    .eq(Article::getUserId, userVo.getId())
                    .eq(Article::getEditStatus, EditStatusEnum.PUBLISHED.getCode())
                    .eq(Article::getExamineStatus, ExamineStatusEnum.PASS.getCode());
            Integer articleCount = Math.toIntExact(articleMapper.selectCount(articleQuery));
            userVo.setArticleCount(articleCount);
        }

        return result;
    }

    private void maskEmailsForNonAdmin(List<SysUserVo> sysUserVos) {
        SysUser currentUser = SecurityUtils.getUser();
        boolean isAdmin = currentUser.getSysRoles().stream()
                .anyMatch(role -> "admin".equals(role.getRole()));
        if (!isAdmin) {
            sysUserVos.forEach(userVo -> userVo.setEmail(null));
        }
    }

    @Override
    public Map<String, Object> getCommunityStats() {
        Map<String, Object> stats = new HashMap<>();

        // 获取文章总数（已发布且审核通过）
        LambdaQueryWrapper<Article> articleQuery = new LambdaQueryWrapper<Article>()
                .eq(Article::getEditStatus, EditStatusEnum.PUBLISHED.getCode())
                .eq(Article::getExamineStatus, ExamineStatusEnum.PASS.getCode());
        Long articleCount = articleMapper.selectCount(articleQuery);

        // 获取用户总数（未删除）
        LambdaQueryWrapper<SysUser> userQuery = new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getIsDeleted, 0);
        Long userCount = this.count(userQuery);

        // 获取总阅读量（所有文章的阅读数之和）
        // 注意：MyBatis-Plus 的 selectCount 不支持 SUM 聚合函数
        // 临时使用估算值：文章数 * 平均阅读量（100）
        // 后续可通过自定义 SQL 或从 visitor_log 表统计获得精确值
        Long totalViews = articleCount * 100;

        // 获取活跃作者数（有已发布文章的用户数）
        // 使用 distinct 查询不同的 userId 数量
        List<Integer> userIds = articleMapper.selectList(
            new LambdaQueryWrapper<Article>()
                .eq(Article::getEditStatus, EditStatusEnum.PUBLISHED.getCode())
                .eq(Article::getExamineStatus, ExamineStatusEnum.PASS.getCode())
                .select(Article::getUserId)
        ).stream()
            .map(Article::getUserId)
            .distinct()
            .toList();
        Long authorCount = (long) userIds.size();

        stats.put("articleCount", articleCount);
        stats.put("userCount", userCount);
        stats.put("viewCount", totalViews);
        stats.put("authorCount", authorCount);

        return stats;
    }

    @Override
    public List<Map<String, Object>> getHotSearches(Integer limit) {
        // 从 Redis 中获取热门搜索记录
        // Redis ZSet 格式：key = "hot_searches", member = 关键词，score = 搜索次数
        return redisComponent.getHotSearches(limit);
    }

}
