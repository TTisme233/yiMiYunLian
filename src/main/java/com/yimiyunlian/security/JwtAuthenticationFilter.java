package com.yimiyunlian.security;

import com.yimiyunlian.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // 从请求头中获取令牌
            String jwt = parseJwt(request);
            
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                // 从令牌中提取用户名
                String username = jwtUtils.getUserNameFromJwtToken(jwt);
                // 从令牌中提取用户角色
                String role = jwtUtils.getUserRoleFromJwtToken(jwt);
                
                // 创建权限列表
                List<SimpleGrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority(role));
                
                // 创建UserDetails对象
                UserDetails userDetails = new User(username, "", authorities);
                
                // 创建认证对象
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                
                // 设置认证详情
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // 设置认证信息到上下文
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("无法设置用户认证: {}", e);
        }
        
        filterChain.doFilter(request, response);
    }

    /**
     * 从请求头中解析JWT令牌
     */
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}