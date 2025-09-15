package com.example.tag_dev.USER.Controller;

import com.example.tag_dev.SYSTEM.DTO.DeptDTO;
import com.example.tag_dev.Config.JwtTokenProvider;
import com.example.tag_dev.USER.DTO.UserDTO;
import com.example.tag_dev.USER.Service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/Admin")
public class AdminController {

    @Autowired
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public AdminController(UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/all-user")
    public ResponseEntity<?> getAllUser(@RequestHeader("Authorization") String JwtToken) {
        log.info("모든 사용자 정보 조회 요청 : {}", jwtTokenProvider.extractUserName(JwtToken));
        String token = JwtToken.substring(7);
        return ResponseEntity.ok(userService.getAllUser(token));
    }

    // 부서 등록
    @PostMapping("/createDept")
    public ResponseEntity<?> createDept(@RequestHeader("Authorization") String JwtToken, @RequestBody DeptDTO deptDTO) {
        log.info("부서 등록 요청 : {}", jwtTokenProvider.extractUserName(JwtToken));
        String token = JwtToken.substring(7);
        return ResponseEntity.ok(userService.createDept(token, deptDTO));
    }

    // 사용자 추가
    @PostMapping("/createUser")
    public ResponseEntity<?> createUser(@RequestHeader("Authorization") String JwtToken, @RequestBody UserDTO userDTO) {
        log.info("사용자 추가 요청 : {}", jwtTokenProvider.extractUserName(JwtToken));
        String token = JwtToken.substring(7);
        return ResponseEntity.ok(userService.createUser(token, userDTO));
    }

    // 사용자 권한 수정
    @PutMapping("/ChangeAcl/{user_id}")
    public ResponseEntity<?> changeAcl(@RequestHeader("Authorization") String JwtToken, @PathVariable Long user_id, @RequestParam String userAcl) {
        log.info("권한 변경 요청 : {}", jwtTokenProvider.extractUserName(JwtToken));
        String token = JwtToken.substring(7);
        return userService.changeAcl(token, user_id, userAcl);
    }

    // 사용자 정보 수정/삭제
    @PutMapping("/update/{user_id}")
    public ResponseEntity<?> updateUserInfo(@PathVariable String user_id, @RequestBody UserDTO user_dto, @RequestHeader("Authorization") String JwtToken) {
        log.info("사용자 정보 수정 요청 : {}", jwtTokenProvider.extractUserName(JwtToken));
        String token = JwtToken.substring(7);
        return ResponseEntity.ok(userService.updateUserInfo(user_id, user_dto, token));
    }

    // 관리자가 사용자 비밀번호 변경
    @PutMapping("/changePassword/{login_id}")
    public ResponseEntity<?> changeUserPassword(@PathVariable String login_id, @RequestBody UserDTO userDTO, @RequestHeader("Authorization") String JwtToken) {
        log.info("관리자 비밀번호 변경 요청 : LoginId = {}", login_id);
        String token = JwtToken.substring(7);
        return ResponseEntity.ok(userService.changeUserPasswordByAdmin(login_id, userDTO, token));
    }

    // 부서 수정/삭제
    @PutMapping("/changeDept/{deptCode}")
    public ResponseEntity<?> updateDept(@PathVariable String deptCode, @RequestBody DeptDTO deptDTO, @RequestHeader("Authorization") String JwtToken) {
        log.info("부서 수정 요청 : {}", jwtTokenProvider.extractUserName(JwtToken));
        String token = JwtToken.substring(7);
        return ResponseEntity.ok(userService.updateDept(deptCode, token, deptDTO));
    }
}
