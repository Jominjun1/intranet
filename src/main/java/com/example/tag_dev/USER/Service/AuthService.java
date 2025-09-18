package com.example.tag_dev.USER.Service;

import com.example.tag_dev.Config.JwtTokenProvider;
import com.example.tag_dev.LOG.Model.UserLog;
import com.example.tag_dev.LOG.Repository.UserLogRepository;
import com.example.tag_dev.USER.DTO.UserDTO;
import com.example.tag_dev.USER.Model.RefreshToken;
import com.example.tag_dev.USER.Model.User;
import com.example.tag_dev.USER.Repository.RefreshTokenRepository;
import com.example.tag_dev.USER.Repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserLogRepository userLogRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    // 리프레시 토큰 재발금
    @Transactional
    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(cookies == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("refreshToken not found");
        }
        String refreshToken = null;
        for (Cookie cookie : cookies) {
            if("refresh_token".equals(cookie.getName())) {
                refreshToken = cookie.getValue();
                break;
            }
        }
        if(refreshToken == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("refreshToken not found");
        }
        Optional<RefreshToken> tokenOpt = refreshTokenRepository.findByRefreshToken(refreshToken);
        if(tokenOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refreshToken");
        }
        RefreshToken token = tokenOpt.get();
        if(token.getExpiresAt().before(new Date())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("refreshToken expired.");
        }
        Long userId = token.getUserId();
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }
        User user = userOpt.get();
        String userName = user.getUserName();
        String userAcl = user.getUser_acl();

        String newAccessToken = jwtTokenProvider.generateToken(userName , userAcl , userId);
        Map<String , Object> map = new HashMap<>();
        map.put("accessToken", newAccessToken);

        return ResponseEntity.status(HttpStatus.OK).body(map);

    }
    // ======================== 로그인 관련 ==============================//
    // 로그인
    @Transactional
    public ResponseEntity<?> login(UserDTO userDTO, HttpServletRequest request, HttpServletResponse response) {
        Optional<User> userOpt = userRepository.findByLoginId(userDTO.getLogin_id());

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            UserLog userLog = new UserLog();
            boolean match = passwordEncoder.matches(userDTO.getPassword(), user.getPassword());
            if (match) {
                user.setFail_login_cnt(0L);
                String accessToken = jwtTokenProvider.generateToken(user.getUserName(), user.getUser_acl(), user.getUserId());

                String refreshTokenStr = UUID.randomUUID().toString();
                Date expiry = new Date(System.currentTimeMillis() + Duration.ofDays(7).toMillis());

                RefreshToken refreshToken = new RefreshToken();
                refreshToken.setUserId(user.getUserId());
                refreshToken.setRefreshToken(refreshTokenStr);
                refreshToken.setExpiresAt(expiry);
                refreshTokenRepository.deleteByUserId(user.getUserId());
                refreshTokenRepository.save(refreshToken);

                // Refresh Token 쿠키 설정
                ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshTokenStr)
                        .httpOnly(true)
                        .secure(true)
                        .path("/")
                        .maxAge(Duration.ofDays(7))
                        .sameSite("Strict")
                        .build();
                response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

                // Access Token 쿠키 설정
                ResponseCookie accessCookie = ResponseCookie.from("access_token", accessToken)
                        .httpOnly(true)
                        .secure(true)
                        .path("/")
                        .maxAge(Duration.ofHours(1)) // 1시간 유효
                        .sameSite("Strict")
                        .build();
                response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());

                userRepository.save(user);

                userLog.setLoginId(user.getLoginId());
                userLog.setStatus("성공");
                userLog.setIp_addr(getClientIP(request));
                userLog.setHttp_refr(request.getHeader("referer"));
                userLog.setRegDt(new Date());

                userLogRepository.save(userLog);

                return ResponseEntity.ok(Map.of(
                        "accessToken", accessToken,
                        "user_id", user.getUserId(),
                        "user_name", user.getUserName(),
                        "login_id", user.getLoginId(),
                        "user_acl", user.getUser_acl(),
                        "user_email", user.getUserEmail()
                ));
            } else {
                long failCount = user.getFail_login_cnt() != null ? user.getFail_login_cnt() : 0L;
                failCount++;
                user.setFail_login_cnt(failCount);
                if (failCount >= 5) {
                    user.setUser_stat("LOCK");
                    user.setUser_acl("0");
                    userRepository.save(user);

                    userLog.setLoginId(user.getLoginId());
                    userLog.setStatus("잠김");
                    userLog.setIp_addr(getClientIP(request));
                    userLog.setHttp_refr(request.getHeader("referer"));
                    userLog.setRegDt(new Date());

                    userLogRepository.save(userLog);

                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
                userRepository.save(user);

                userLog.setLoginId(user.getLoginId());
                userLog.setStatus("실패");
                userLog.setIp_addr(getClientIP(request));
                userLog.setHttp_refr(request.getHeader("referer"));
                userLog.setRegDt(new Date());

                userLogRepository.save(userLog);

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호 일치 하지 않음");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않는 사용자");
        }
    }

    // 로그아웃
    @Transactional
    public ResponseEntity<?> logout(String jwtToken, HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getCookieValue(request);
        if (refreshToken != null) {
            refreshTokenRepository.deleteByRefreshToken(refreshToken);
        }
        ResponseCookie cookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        Long userId = jwtTokenProvider.extractUserId(jwtToken);
        if (userId != null) {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                userRepository.save(user);

                UserLog userLog = new UserLog();

                userLog.setLoginId(user.getLoginId());
                userLog.setStatus("로그아웃");
                userLog.setIp_addr(getClientIP(request));
                userLog.setHttp_refr(request.getHeader("referer"));
                userLog.setRegDt(new Date());

                userLogRepository.save(userLog);

            }
        }
        return ResponseEntity.ok("로그아웃 성공");
    }

    // ID 중복 확인
    public Optional<User> checkLoginId(String loginId) {
        return userRepository.findByLoginId(loginId);
    }

    // 쿠키 가져오기
    private String getCookieValue(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("refresh_token")) return cookie.getValue();
            }
        }
        return null;
    }

    // IP 변환 메소드
    public static String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip.split(",")[0].trim();
        }

        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        ip = request.getHeader("Proxy-Client-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        ip = request.getHeader("WL-Proxy-Client-IP"); // WebLogic
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }

}
