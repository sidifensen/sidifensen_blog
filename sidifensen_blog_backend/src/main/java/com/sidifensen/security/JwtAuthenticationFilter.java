package com.sidifensen.security;

import cn.hutool.core.util.ObjectUtil;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.constants.SecurityConstants;
import com.sidifensen.domain.entity.LoginUser;
import com.sidifensen.domain.entity.SysUser;
import com.sidifensen.domain.result.Result;
import com.sidifensen.mapper.SysUserMapper;
import com.sidifensen.utils.JwtUtil;
import com.sidifensen.utils.WebUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private JwtUtil jwtUtil;

    @Resource
    private SysUserMapper sysUserMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 如果是不需要登录的接口，则直接放行
        for (String url : SecurityConstants.No_Auth_Urls) {
            if (request.getRequestURI().equals(url)) {
                filterChain.doFilter(request, response);
                return;
            }
        }
        // 从请求头中获取jwt
        String jwt = request.getHeader("Authorization");
        if (ObjectUtil.isEmpty(jwt)){
            //请先登录
            WebUtil.Unauthorized(response, Result.unauthorized(BlogConstants.LoginRequired).toJson());
            return;
        }
        if (ObjectUtil.isNotEmpty(jwt)){
            // 解析并验证jwt
            Long id = jwtUtil.parseToken(jwt);
            if (ObjectUtil.isEmpty(id)) {
                //登录过期
                WebUtil.Unauthorized(response, Result.unauthorized(BlogConstants.LoginExpired).toJson());
                return;
            }
            // 根据id查询用户信息
            SysUser sysUser = sysUserMapper.selectById(id);
            // 将SysUser转换为LoginUser
            LoginUser loginUser = new LoginUser(sysUser);
            // 判断是否为null
            if(ObjectUtil.isNotNull(loginUser)) {
                // 鉴权，跳转的时候需要访问 /index 页面
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
                // 将用户信息存储到SecurityContext中，SecurityContext存储到SecurityContextHolder中
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}