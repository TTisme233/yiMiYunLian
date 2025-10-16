package com.yimiyunlian.security;

import com.yimiyunlian.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtUtils jwtUtils;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF保护，因为我们使用JWT
            .csrf().disable()
            // 设置会话管理为无状态
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            // 设置请求授权
            .authorizeRequests()
            // 允许访问登录和注册接口
            .antMatchers("/api/auth/login", "/api/auth/register").permitAll()
            // 允许访问健康检查接口
            .antMatchers("/actuator/health").permitAll()
            // 个人信息管理API - 所有认证用户可访问
            .antMatchers("/api/profile/**").authenticated()
            // 权限管理API只能被管理员访问
            .antMatchers("/api/admin/permissions/**").hasAuthority("ADMINISTRATOR")
            // 病史管理API只能被医生访问（更精细的权限在服务层控制）
            .antMatchers("/api/medical-history/**").hasAuthority("DOCTOR")
            // 其他所有请求需要认证
            .anyRequest().authenticated();

        // 添加JWT过滤器
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}