package com.example.tag_dev.SYSTEM.Service;

import com.example.tag_dev.Config.JwtTokenProvider;
import com.example.tag_dev.LOG.Model.ProjectLog;
import com.example.tag_dev.LOG.Repository.ProjectLogRepository;
import com.example.tag_dev.SYSTEM.DTO.ProjectDTO;
import com.example.tag_dev.SYSTEM.Model.Project_Info;
import com.example.tag_dev.SYSTEM.Repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.example.tag_dev.USER.Service.UserService.getStrings;

@RequiredArgsConstructor
@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectLogRepository projectLogRepository;
    private final JwtTokenProvider jwtTokenProvider;

    // 프로젝트 생성
    public ResponseEntity<?> createProject(String token, ProjectDTO projectDTO) {
        if (jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String yearSuffix = String.valueOf(Year.now().getValue()).substring(2);
        String maxCode = projectRepository.findMaxCodeByYear(yearSuffix);

        int nextSeq = 1;
        if (maxCode != null) {
            // 뒤 3자리만 추출 후 숫자로 변환
            int currentMax = Integer.parseInt(maxCode.substring(2));
            nextSeq = currentMax + 1;
        }
        String newCode = yearSuffix + String.format("%03d", nextSeq);

        Project_Info project_Info = new Project_Info();
        project_Info.setProjectCode(newCode);
        project_Info.setProject_name(projectDTO.getProject_name());
        project_Info.setProject_leader(projectDTO.getProject_leader());
        project_Info.setProject_status("Y");
        project_Info.setProject_ing(projectDTO.getProject_ing());
        project_Info.setCustomer(projectDTO.getCustomer());
        project_Info.setDeptCd(projectDTO.getDeptCd());
        project_Info.setStartDt(projectDTO.getStartDt());
        project_Info.setEndDt(projectDTO.getEndDt());
        project_Info.setRegion(projectDTO.getRegion());
        project_Info.setCreateDt(new Date());
        project_Info.setCreate_id(jwtTokenProvider.extractUserName(token));
        projectRepository.save(project_Info);

        ProjectLog projectLog = new ProjectLog();
        projectLog.setProject_leader(project_Info.getProject_leader());
        projectLog.setProject_status(project_Info.getProject_status());
        projectLog.setCustomer(project_Info.getCustomer());
        projectLog.setDeptCd(project_Info.getDeptCd());
        projectLog.setProject_ing(project_Info.getProject_ing());
        projectLog.setStartDt(project_Info.getStartDt());
        projectLog.setEndDt(project_Info.getEndDt());
        projectLog.setRegion(project_Info.getRegion());
        projectLog.setCreate_id(jwtTokenProvider.extractUserName(token));
        projectLog.setCreateDt(new Date());

        projectLogRepository.save(projectLog);

        return ResponseEntity.ok("프로젝트 생성");
    }

    // 프로젝트 조회
    public ResponseEntity<?> getAllProject(String token) {
        if (jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<Project_Info> projects = projectRepository.findAll();
        return ResponseEntity.ok(projects);
    }

    // 프로젝트 수정/삭제
    public ResponseEntity<?> updateProject(String projectCode, String token, ProjectDTO projectDTO) {
        if (jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<Project_Info> projectOpt = projectRepository.findByProjectCode(projectCode);
        if (projectOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("프로젝트 없음");
        }

        Project_Info projectInfo = projectOpt.get();

        // DTO의 null이 아닌 값들만 Project_Info 엔티티에 복사
        BeanUtils.copyProperties(projectDTO, projectInfo, getNullPropertyNames(projectDTO));

        // 업데이트 정보 설정
        projectInfo.setUpdateDt(new Date());
        projectInfo.setUpdate_id(jwtTokenProvider.extractUserName(token));
        projectRepository.save(projectInfo);

        // 프로젝트 로그 생성
        createProjectLog(projectInfo, token);

        return ResponseEntity.ok("프로젝트 정보 수정/삭제 완료");
    }

    // null이 아닌 프로퍼티만 가져오는 헬퍼 메서드
    private String[] getNullPropertyNames(Object source) {
        return getStrings(source);
    }

    // 프로젝트 로그 생성 헬퍼 메서드
    private void createProjectLog(Project_Info projectInfo, String token) {
        ProjectLog projectLog = new ProjectLog();
        projectLog.setProject_leader(projectInfo.getProject_leader());
        projectLog.setProject_status(projectInfo.getProject_status());
        projectLog.setCustomer(projectInfo.getCustomer());
        projectLog.setDeptCd(projectInfo.getDeptCd());
        projectLog.setStartDt(projectInfo.getStartDt());
        projectLog.setEndDt(projectInfo.getEndDt());
        projectLog.setRegion(projectInfo.getRegion());
        projectLog.setProject_ing(projectInfo.getProject_ing());
        projectLog.setCreateDt(new Date());
        projectLog.setCreate_id(jwtTokenProvider.extractUserName(token));

        projectLogRepository.save(projectLog);
    }
}
