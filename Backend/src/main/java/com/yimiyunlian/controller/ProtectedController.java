package com.yimiyunlian.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/protected")
public class ProtectedController {

    /**
     * 受保护的端点，只有携带有效JWT令牌的请求才能访问
     */
    @GetMapping("/profile")
    public Map<String, Object> getUserProfile() {
        // 从安全上下文中获取当前认证用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        // 构建响应
        Map<String, Object> response = new HashMap<>();
        response.put("message", "成功访问受保护的资源");
        response.put("username", username);
        response.put("status", "已认证");
        
        return response;
    }
    
    /**
     * 另一个受保护的端点示例
     */
    @GetMapping("/data")
    public Map<String, String> getProtectedData() {
        Map<String, String> data = new HashMap<>();
        data.put("key1", "value1");
        data.put("key2", "value2");
        data.put("message", "这是只有认证用户才能访问的数据");
        return data;
    }
}