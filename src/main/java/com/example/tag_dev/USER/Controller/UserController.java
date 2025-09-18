package com.example.tag_dev.USER.Controller;

import com.example.tag_dev.USER.DTO.UserDTO;
import com.example.tag_dev.USER.Service.AuthService;
import com.example.tag_dev.USER.Service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    // ID 찾기
    @GetMapping("/findID")
    public ResponseEntity<?> findID(@RequestParam String user_name, @RequestParam String user_email, @RequestParam String user_phone_num) {
        log.info("ID 찾기 요청 : 이름={}, 이메일={}, 전화번호={}", user_name, user_email, user_phone_num);
        UserDTO userDTO = new UserDTO();
        userDTO.setUser_name(user_name);
        userDTO.setUser_email(user_email);
        userDTO.setUser_phone_num(user_phone_num);
        return ResponseEntity.ok(userService.findLoginId(userDTO));
    }

    // ID 중복체크
    @GetMapping("/checkID")
    public ResponseEntity<?> checkID(@RequestParam String loginId) {
        log.info("로그인 ID 중복 체크 : {}", loginId);
        return ResponseEntity.ok(authService.checkLoginId(loginId));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO userDTO, HttpServletRequest request , HttpServletResponse response) {
        log.info("로그인 요청 : {}", userDTO.getLogin_id());
        return authService.login(userDTO, request, response);
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        log.info("로그아웃 요청");
        String token = extractTokenFromCookie(request);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰이 없습니다.");
        }
        return ResponseEntity.ok(authService.logout(token, request, response));
    }
    
    // 쿠키에서 토큰을 추출하는 헬퍼 메서드
    private String extractTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("access_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    // refreshToken 설정
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request){
        log.info("reFreshToken : {}", request.getHeader("Authorization"));
        return ResponseEntity.ok(authService.refreshAccessToken(request));
    }

    // 비밀번호 변경
    @PutMapping("/changePwd/{login_id}")
    public ResponseEntity<?> changePwd(@PathVariable String loginId, @RequestBody UserDTO userDTO, HttpServletRequest request) {
        log.info("비밀번호 변경 요청 : LoginId = {} , user_dto = {}", loginId, userDTO);
        String token = extractTokenFromCookie(request);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰이 없습니다.");
        }
        return ResponseEntity.ok(userService.changePassword(userDTO, token));
    }

    // 비밀번호 찾기
    @PutMapping("/findPassword")
    public ResponseEntity<?> findPassword(@RequestBody UserDTO userDTO) {
        log.info("비밀번호 찾기 요청 : {}", userDTO.getLogin_id());
        return ResponseEntity.ok(userService.findPassword(userDTO));
    }

    // 부서 목록 조회
    @GetMapping("/getDeptList")
    public ResponseEntity<?> getDeptList() {
        log.info("부서 목록 조회 요청");
        return ResponseEntity.ok(userService.getDeptList());
    }
}