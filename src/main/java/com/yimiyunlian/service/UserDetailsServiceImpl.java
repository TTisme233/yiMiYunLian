package com.yimiyunlian.service;

import com.yimiyunlian.entity.User;
import com.yimiyunlian.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 查找用户
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));

        // 确定用户角色
        String role = "USER"; // 默认角色
        if (user.getClass().getName().contains("Patient")) {
            role = "PATIENT";
        } else if (user.getClass().getName().contains("Doctor")) {
            role = "DOCTOR";
        } else if (user.getClass().getName().contains("Administrator")) {
            role = "ADMIN";
        }

        // 创建权限列表
        List<SimpleGrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority(role));

        // 创建并返回UserDetails对象
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }

    /**
     * 加载完整的用户对象（包含所有属性）
     */
    public Object loadFullUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));
    }
}