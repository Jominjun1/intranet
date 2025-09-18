package com.example.tag_dev.USER.Controller;

import com.example.tag_dev.SYSTEM.DTO.DeptDTO;
import com.example.tag_dev.USER.DTO.UserDTO;
import com.example.tag_dev.USER.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/Admin")
public class AdminController {

    private final UserService userService;

    @GetMapping("/all-user")
    public ResponseEntity<?> getAllUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증이 필요합니다.");
        }
        log.info("모든 사용자 정보 조회 요청 : {}", authentication.getName());
        return ResponseEntity.ok(userService.getAllUser());
    }
    // 부서 등록
    @PostMapping("/createDept")
    public ResponseEntity<?> createDept(Authentication authentication, @RequestBody DeptDTO deptDTO) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증이 필요합니다.");
        }
        log.info("부서 등록 요청 : {}", authentication.getName());
        return ResponseEntity.ok(userService.createDept(deptDTO));
    }

    // 사용자 추가
    @PostMapping("/createUser")
    public ResponseEntity<?> createUser(Authentication authentication, @RequestBody UserDTO userDTO) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증이 필요합니다.");
        }
        log.info("사용자 추가 요청 : {}", authentication.getName());
        return ResponseEntity.ok(userService.createUser(userDTO));
    }

    // 사용자 권한 수정
    @PutMapping("/ChangeAcl/{user_id}")
    public ResponseEntity<?> changeAcl(Authentication authentication, @PathVariable Long user_id, @RequestParam String userAcl) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증이 필요합니다.");
        }
        log.info("권한 변경 요청 : {}", authentication.getName());
        return userService.changeAcl(user_id, userAcl);
    }

    // 사용자 정보 수정/삭제
    @PutMapping("/update/{user_id}")
    public ResponseEntity<?> updateUserInfo(@PathVariable String user_id, @RequestBody UserDTO user_dto, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증이 필요합니다.");
        }
        log.info("사용자 정보 수정 요청 : {}", authentication.getName());
        return ResponseEntity.ok(userService.updateUserInfo(user_id, user_dto));
    }

    // 관리자가 사용자 비밀번호 변경
    @PutMapping("/changePassword/{login_id}")
    public ResponseEntity<?> changeUserPassword(@PathVariable String login_id, @RequestBody UserDTO userDTO, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증이 필요합니다.");
        }
        log.info("관리자 비밀번호 변경 요청 : LoginId = {}", login_id);
        return ResponseEntity.ok(userService.changeUserPasswordByAdmin(login_id, userDTO));
    }

    // 부서 수정/삭제
    @PutMapping("/changeDept/{deptCode}")
    public ResponseEntity<?> updateDept(@PathVariable String deptCode, @RequestBody DeptDTO deptDTO, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증이 필요합니다.");
        }
        log.info("부서 수정 요청 : {}", authentication.getName());
        return ResponseEntity.ok(userService.updateDept(deptCode, deptDTO));
    }
}
