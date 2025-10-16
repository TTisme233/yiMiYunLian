package com.yimiyunlian.util;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    /**
     * 生成JWT令牌
     */
    public String generateJwtToken(String username, String userType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", username);
        claims.put("role", userType);
        claims.put("iat", new Date());
        claims.put("exp", new Date((new Date()).getTime() + jwtExpirationMs));

        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }
    
    /**
     * 从JWT令牌中提取用户角色
     */
    public String getUserRoleFromJwtToken(String token) {
        return (String) Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token)
                .getBody().get("role");
    }

    /**
     * 从JWT令牌中提取用户名
     */
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token)
                .getBody().getSubject();
    }

    /**
     * 验证JWT令牌
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            System.err.println("无效的JWT签名: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.err.println("无效的JWT令牌: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.err.println("JWT令牌已过期: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.err.println("不支持的JWT令牌: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("JWT声明字符串为空: " + e.getMessage());
        }

        return false;
    }
}