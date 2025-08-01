package com.example.tag_dev.USER.Controller;

import com.example.tag_dev.USER.DTO.UserDTO;
import com.example.tag_dev.USER.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ID 찾기
    @GetMapping("/findID/{login_id}")
    public ResponseEntity<?> findID(@RequestBody UserDTO userDTO) {
        try{
            log.info("ID 찾기 요청 : {}", userDTO.getLogin_id());
            return ResponseEntity.ok(userService.findLoginId(userDTO));
        } catch (Exception e) {
            log.info("에러 발생 : {}" , e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ID 중복체크
    @GetMapping("/checkID")
    public ResponseEntity<?> checkID(@RequestParam String loginId){
        try{
            log.info("로그인 ID 중복 체크 : {}" , loginId);
            return ResponseEntity.ok(userService.checkLoginId(loginId));
        } catch (Exception e) {
            log.info("에러 발생 : {}" , e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO userDTO , HttpServletRequest request){
        try{
            log.info("로그인 요청 : {}", userDTO.getLogin_id());
            return userService.login(userDTO , request);
        } catch (Exception e) {
            log.info("에러 발생 : {}" , e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String jwtToken ,  HttpServletRequest request){
        try{
            log.info("로그아웃 요청");
            String token = jwtToken.startsWith("Bearer ") ? jwtToken.substring(7) : jwtToken;
            return ResponseEntity.ok(userService.logout(token , request));
        } catch (Exception e) {
            log.info("에러 발생 : {}" , e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    // 비밀번호 변경
    @PutMapping("/changePwd/{login_id}")
    public ResponseEntity<?> changePwd(@PathVariable String loginId, @RequestBody UserDTO userDTO , @RequestHeader("Authorization") String JwtToken){
        try{
            log.info("비밀번호 변경 요청 : LoginId = {} , user_dto = {}",loginId, userDTO);
            String token = JwtToken.startsWith("Bearer ") ? JwtToken.substring(7) : JwtToken;
            return ResponseEntity.ok(userService.changePassword(userDTO , token));
        } catch (Exception e) {
            log.info("에러 발생 : {}" , e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 회원 삭제
    @DeleteMapping("/deleteUser{login_id}")
    public ResponseEntity<?> deleteUser(@PathVariable String loginId , @RequestHeader("Authorization") String JwtToken){
        try{
            log.info("유저 삭제 요청 : LoginId = {}",loginId);
            String token = JwtToken.startsWith("Bearer ") ? JwtToken.substring(7) : JwtToken;
            return ResponseEntity.ok(userService.deleteUser(loginId , token));
        } catch (Exception e) {
            log.info("에러 발생 : {}" , e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
