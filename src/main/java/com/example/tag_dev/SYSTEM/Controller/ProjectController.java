package com.example.tag_dev.SYSTEM.Controller;


import com.example.tag_dev.SYSTEM.DTO.ProjectDTO;
import com.example.tag_dev.SYSTEM.Service.ProjectService;
import com.example.tag_dev.Config.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/project")
public class ProjectController {

    @Autowired
    private final ProjectService projectService;
    private final JwtTokenProvider jwtTokenProvider;

    public ProjectController(ProjectService projectService, JwtTokenProvider jwtTokenProvider) {
        this.projectService = projectService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // 프로젝트 조회
    @GetMapping("/searchAll")
    public ResponseEntity<?> searchAllProject(@RequestHeader("Authorization") String JwtToken) {
        log.info("프로젝트 조회 요청 : {}", jwtTokenProvider.extractUserName(JwtToken));
        String token = JwtToken.substring(7);
        return ResponseEntity.ok(projectService.getAllProject(token));
    }

    // 프로젝트 생성
    @PostMapping("/create")
    public ResponseEntity<?> createProject(@RequestHeader("Authorization") String JwtToken, @RequestBody ProjectDTO projectDTO) {
        log.info("프로젝트 생성 요청 : {}", jwtTokenProvider.extractUserName(JwtToken));
        String token = JwtToken.substring(7);
        return ResponseEntity.ok(projectService.createProject(token, projectDTO));
    }

    // 프로젝트 수정/삭제
    @PostMapping("/update{projectCode}")
    public ResponseEntity<?> updateProject(@PathVariable String projectCode, @RequestHeader("Authorization") String JwtToken, @RequestBody ProjectDTO projectDTO) {
        log.info("프로젝트 수정 요청 : {}", jwtTokenProvider.extractUserName(JwtToken));
        String token = JwtToken.substring(7);
        return ResponseEntity.ok(projectService.updateProject(projectCode, token, projectDTO));
    }
}
