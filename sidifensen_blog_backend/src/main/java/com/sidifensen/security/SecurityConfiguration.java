package com.sidifensen.security;

import com.sidifensen.config.SidifensenConfig;
import com.sidifensen.domain.constants.SecurityConstants;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableMethodSecurity
public class SecurityConfiguration {

    @Resource
    private SysUserDetailsService sysUserDetailsService;

    @Resource
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Resource
    private SidifensenConfig sidifensenConfig; // 现在导入正确

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 设置认证管理器
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(SecurityConstants.No_Need_Auth_Urls).permitAll()
                .requestMatchers(SecurityConstants.Optional_Auth_Urls).permitAll() // 可选认证接口也允许访问
                .anyRequest().authenticated());
        http.csrf(AbstractHttpConfigurer::disable);
        // 配置跨域
        http.cors(cors -> cors.configurationSource(configurationSource()));
        // 添加JWT过滤器
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // 创建AuthenticationManager
    @Bean
    public AuthenticationManager sysUserAuthenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(sysUserDetailsService);
        return new ProviderManager(provider);
    }

    // 配置密码编码器
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 配置跨域，允许跨域 配置CorsConfigurationSource
    public CorsConfigurationSource configurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedMethods(Collections.singletonList("*"));
        corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
        // corsConfiguration.setAllowedOrigins(sidifensenConfig.getAllowOrigins());
        corsConfiguration.setAllowedOrigins(Collections.singletonList("*"));

        // 创建 CorsConfigurationSource对象
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

}
