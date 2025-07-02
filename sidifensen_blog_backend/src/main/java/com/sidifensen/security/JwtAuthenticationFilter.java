package com.sidifensen.security;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.sidifensen.domain.dto.UserDto;
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
import org.springframework.security.authentication.BadCredentialsException;
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
        // 如果是登录接口，则直接放行
        if ("/user/login".equals(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }
        // 从请求头中获取jwt
        String jwt = request.getHeader("Authorization");
        if (ObjectUtil.isEmpty(jwt)){
            WebUtil.Unauthorized(response, Result.unauthorized("请登录").toJson());
            return;
        }
        if (ObjectUtil.isNotEmpty(jwt)){
            // 解析并验证jwt
            String user = jwtUtil.parseToken(jwt);
            if (ObjectUtil.isEmpty(user)) {
                WebUtil.Unauthorized(response, Result.unauthorized("登录已过期，请重新登录").toJson());
                return;
            }
            // 将json字符串转换为UserDto对象
            UserDto userDto = JSONUtil.toBean(user, UserDto.class);
            // 根据id查询用户信息
            SysUser sysUser = sysUserMapper.selectById(userDto.getId());
            // 将SysUser转换为LoginUser
            LoginUser loginUser = new LoginUser(sysUser);
            // 判断是否为null
            if(ObjectUtil.isNotNull(loginUser)) {
                // 鉴权，跳转的时候需要访问 /index 页面
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
                // 将用户信息存储到SecurityContext中，SecurityContext存储到SecurityContextHolder中
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                filterChain.doFilter(request, response);
            }
        }
    }
}