package com.example.tag_dev.USER.Controller;

import com.example.tag_dev.USER.DTO.UserDTO;
import com.example.tag_dev.USER.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

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
        return ResponseEntity.ok(userService.checkLoginId(loginId));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO userDTO, HttpServletRequest request) {
        log.info("로그인 요청 : {}", userDTO.getLogin_id());
        return userService.login(userDTO, request);
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String jwtToken, HttpServletRequest request) {
        log.info("로그아웃 요청");
        String token = jwtToken.startsWith("Bearer ") ? jwtToken.substring(7) : jwtToken;
        return ResponseEntity.ok(userService.logout(token, request));
    }

    // 비밀번호 변경
    @PutMapping("/changePwd/{login_id}")
    public ResponseEntity<?> changePwd(@PathVariable String loginId, @RequestBody UserDTO userDTO, @RequestHeader("Authorization") String JwtToken) {
        log.info("비밀번호 변경 요청 : LoginId = {} , user_dto = {}", loginId, userDTO);
        String token = JwtToken.startsWith("Bearer ") ? JwtToken.substring(7) : JwtToken;
        return ResponseEntity.ok(userService.changePassword(userDTO, token));
    }

    // 비밀번호 찾기
    @PutMapping("/findPassword")
    public ResponseEntity<?> findPassword(@RequestBody UserDTO userDTO) {
        log.info("비밀번호 찾기 요청 : {}", userDTO.getLogin_id());
        return ResponseEntity.ok(userService.findPassword(userDTO));
    }
}