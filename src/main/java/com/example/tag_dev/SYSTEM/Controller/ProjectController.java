package com.example.tag_dev.SYSTEM.Controller;


import com.example.tag_dev.Config.JwtTokenProvider;
import com.example.tag_dev.SYSTEM.DTO.ProjectDTO;
import com.example.tag_dev.SYSTEM.Service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/project")
public class ProjectController {

    private final ProjectService projectService;
    private final JwtTokenProvider jwtTokenProvider;

    // 프로젝트 조회
    @GetMapping("/searchAll")
    public ResponseEntity<?> searchAllProject(HttpServletRequest request) {
        String token = extractTokenFromCookie(request);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰이 없습니다.");
        }
        log.info("프로젝트 조회 요청 : {}", jwtTokenProvider.extractUserName(token));
        return ResponseEntity.ok(projectService.getAllProject(token));
    }

    // 프로젝트 생성
    @PostMapping("/create")
    public ResponseEntity<?> createProject(HttpServletRequest request, @RequestBody ProjectDTO projectDTO) {
        String token = extractTokenFromCookie(request);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰이 없습니다.");
        }
        log.info("프로젝트 생성 요청 : {}", jwtTokenProvider.extractUserName(token));
        return ResponseEntity.ok(projectService.createProject(token, projectDTO));
    }

    // 프로젝트 수정/삭제
    @PostMapping("/update{projectCode}")
    public ResponseEntity<?> updateProject(@PathVariable String projectCode, HttpServletRequest request, @RequestBody ProjectDTO projectDTO) {
        String token = extractTokenFromCookie(request);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰이 없습니다.");
        }
        log.info("프로젝트 수정 요청 : {}", jwtTokenProvider.extractUserName(token));
        return ResponseEntity.ok(projectService.updateProject(projectCode, token, projectDTO));
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
}
