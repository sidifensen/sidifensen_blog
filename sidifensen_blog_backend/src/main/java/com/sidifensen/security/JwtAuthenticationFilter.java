package com.sidifensen.security;

import cn.hutool.core.util.ObjectUtil;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.constants.SecurityConstants;
import com.sidifensen.domain.entity.LoginUser;
import com.sidifensen.domain.entity.SysUser;
import com.sidifensen.domain.enums.StatusEnum;
import com.sidifensen.domain.result.Result;
import com.sidifensen.mapper.SysUserMapper;
import com.sidifensen.utils.JwtUtils;
import com.sidifensen.utils.WebUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @author sdifensen
 * @date 2023/12/14
 * 该接口在请求前执行一次，获取request中的数据，其中token就在请求头里
 * 获取token，根据token从redis中获取用户信息
 */
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Resource
    private JwtUtils jwtUtils;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private SysUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 如果是不需要登录的接口，则直接放行
        for (String url : SecurityConstants.No_Auth_Urls) {
            AntPathRequestMatcher matcher = new AntPathRequestMatcher(url);
            if (matcher.matches(request)) {
                filterChain.doFilter(request, response);
                return;
            }
        }
        // 从请求头中获取jwt
        String jwt = request.getHeader("Authorization");
        if (ObjectUtil.isEmpty(jwt)) {
            //请先登录
            log.error("用户访问接口{},提示:请先登录", request.getRequestURI());
            WebUtils.Unauthorized(response, Result.unauthorized(BlogConstants.LoginRequired).toJson());
            return;
        }
        if (ObjectUtil.isNotEmpty(jwt)) {
            // 解析并验证jwt
            Integer id = jwtUtils.parseToken(jwt);
            if (ObjectUtil.isEmpty(id)) {
                //登录过期
                log.error("用户访问接口{},提示:登录过期", request.getRequestURI());
                WebUtils.Unauthorized(response, Result.unauthorized(BlogConstants.LoginExpired).toJson());
                return;
            }
            try {
                // 根据id查询用户信息
                SysUser sysUser = sysUserMapper.selectById(id);
                if (ObjectUtil.isEmpty(sysUser)){
                    // 用户不存在
                    log.error("用户访问接口{},提示:用户不存在", request.getRequestURI());
                    WebUtils.Unauthorized(response, Result.unauthorized(BlogConstants.NotFoundUser).toJson());
                    return;
                }
                if (sysUser.getStatus() == StatusEnum.DISABLE.getStatus()){
                    // 用户已被禁用
                    log.error("用户访问接口{},提示:用户已被禁用", request.getRequestURI());
                    WebUtils.Unauthorized(response, Result.unauthorized(BlogConstants.UserDisabled).toJson());
                    return;
                }
                // 将SysUser转换为LoginUser
                LoginUser loginUser = userDetailsService.handleLogin(sysUser);
                if (ObjectUtil.isNotEmpty(loginUser)) {
                    // 鉴权，跳转的时候需要访问 /index 页面
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
                    // 将用户信息存储到SecurityContext中，SecurityContext存储到SecurityContextHolder中
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            } catch (Exception e) {
                log.error("查询用户信息时发生异常，用户ID: {}，错误信息: {}", id, e.getMessage(), e);
                WebUtils.Unauthorized(response, Result.unauthorized("系统内部错误").toJson());
                return;
            }

        }
        filterChain.doFilter(request, response);
    }
}