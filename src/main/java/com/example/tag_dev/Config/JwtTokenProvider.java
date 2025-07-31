package com.example.tag_dev.Config;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {

    private final JwtConfig jwtConfig;
    private final Key secretKey;

    @Autowired
    public JwtTokenProvider(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
        this.secretKey = Keys.hmacShaKeyFor(jwtConfig.getSecretKey().getBytes());
    }

    // 토큰 생성
    public String generateToken(String userName , String userAcl , Long userId) {
        Map<String , Object> map = new HashMap<>();
        map.put("user_name" , userName);
        map.put("user_acl" , userAcl);
        map.put("user_id" , userId);

        Long validityInMilliseconds = jwtConfig.getValidityInMilliseconds();
        try {
            return Jwts.builder()
                    .setClaims(map)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + validityInMilliseconds))
                    .signWith(secretKey, SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            throw new RuntimeException("토큰 생성 실패", e);
        }
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.err.println("토큰 만료: " + e.getMessage());
            return false;
        } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e) {
            System.err.println("잘못된 토큰: " + e.getMessage());
            return false;
        }
    }
    // 토큰에서 권한 추출
    public String extractUserAcl(String token) {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
            return (String) claims.get("user_acl");
        } catch (Exception e) {
            return null;
        }
    }
    public Long extractUserId(String token) {
        try{
            Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
            return (Long) claims.get("user_id");
        } catch(Exception e){
            return null;
        }
    }
    public String extractUserName(String token) {
        try{
            Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
            return (String) claims.get("user_name");
        } catch (Exception e) {
            return null;
        }
    }
}
