package com.example.tag_dev.SYSTEM.Service;

import com.example.tag_dev.SYSTEM.DTO.ProjectDTO;
import com.example.tag_dev.SYSTEM.Model.Project_Info;
import com.example.tag_dev.SYSTEM.Repository.ProjectRepository;
import com.example.tag_dev.Config.JwtTokenProvider;
import com.example.tag_dev.LOG.LogRepository.ProjectLogRepository;
import com.example.tag_dev.LOG.Model.ProjectLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    @Autowired
    private final ProjectRepository projectRepository;
    private final ProjectLogRepository projectLogRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public ProjectService(ProjectRepository projectRepository, ProjectLogRepository projectLogRepository, JwtTokenProvider jwtTokenProvider) {
        this.projectRepository = projectRepository;
        this.projectLogRepository = projectLogRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // 프로젝트 생성
    public ResponseEntity<?> createProject(String token , ProjectDTO projectDTO) {
        if(!jwtTokenProvider.validateToken(token)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try{
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
            project_Info.setProject_status(projectDTO.getProject_status());
            project_Info.setProject_leader(projectDTO.getProject_leader());
            project_Info.setStatus("N");
            project_Info.setProject_status(projectDTO.getProject_status());
            project_Info.setCustomer(projectDTO.getCustomer());
            project_Info.setDeptCd(projectDTO.getDeptCd());
            project_Info.setStartDt(projectDTO.getStartDt());
            project_Info.setEndDt(projectDTO.getEndDt());
            project_Info.setRegion(projectDTO.getRegion());
            project_Info.setRegDt(new Date());
            project_Info.setUserName(jwtTokenProvider.extractUserName(token));
            projectRepository.save(project_Info);

            ProjectLog projectLog = new ProjectLog();
            projectLog.setProject_leader(project_Info.getProject_leader());
            projectLog.setProject_status(project_Info.getProject_status());
            projectLog.setCustomer(project_Info.getCustomer());
            projectLog.setDeptCd(project_Info.getDeptCd());
            projectLog.setStatus(project_Info.getStatus());
            projectLog.setStartDt(project_Info.getStartDt());
            projectLog.setEndDt(project_Info.getEndDt());
            projectLog.setRegion(project_Info.getRegion());
            projectLog.setUserName(jwtTokenProvider.extractUserName(token));
            projectLog.setRegDt(new Date());

            projectLogRepository.save(projectLog);

            return ResponseEntity.ok("프로젝트 생성");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("사용자 생성 중 오류가 발생: " + e.getMessage());
        }
    }

    // 프로젝트 조회
    public ResponseEntity<?> getAllProject(String token){
        if(!jwtTokenProvider.validateToken(token)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<Project_Info> projects = projectRepository.findAll();
        return ResponseEntity.ok(projects);
    }

    // 프로젝트 수정/삭제
    public ResponseEntity<?> updateProject( String projectCode, String token, ProjectDTO projectDTO) {
        if(!jwtTokenProvider.validateToken(token)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Optional<Project_Info> projectOpt = projectRepository.findByProjectCode(projectCode);
        if(projectOpt.isPresent()){
            Project_Info projectInfo = projectOpt.get();
            boolean isUpdated = false;

            if(projectDTO.getProject_name() != null && !projectDTO.getProject_name().isEmpty()){
                projectInfo.setProject_name(projectDTO.getProject_name());
                isUpdated = true;
            }
            if(projectDTO.getProject_leader() != null && !projectDTO.getProject_leader().isEmpty()){
                projectInfo.setProject_leader(projectDTO.getProject_leader());
                isUpdated = true;
            }
            if(projectDTO.getProject_status() != null && !projectDTO.getProject_status().isEmpty()){
                projectInfo.setProject_status(projectDTO.getProject_status());
                isUpdated = true;
            }
            if(projectDTO.getStatus() != null && !projectDTO.getStatus().isEmpty()){
                projectInfo.setStatus(projectDTO.getStatus());
                isUpdated = true;
            }
            if(projectDTO.getCustomer() != null && !projectDTO.getCustomer().isEmpty()){
                projectInfo.setCustomer(projectDTO.getCustomer());
                isUpdated = true;
            }
            if(projectDTO.getDeptCd() != null && !projectDTO.getDeptCd().isEmpty()){
                projectInfo.setDeptCd(projectDTO.getDeptCd());
                isUpdated = true;
            }
            if(projectDTO.getProject_category() != null && !projectDTO.getProject_category().isEmpty()){
                projectInfo.setProject_category(projectDTO.getProject_category());
                isUpdated = true;
            }
            if(projectDTO.getStartDt() != null){
                projectInfo.setStartDt(projectDTO.getStartDt());
                isUpdated = true;
            }
            if(projectDTO.getEndDt() != null){
                projectInfo.setEndDt(projectDTO.getEndDt());
                isUpdated = true;
            }
            if(projectDTO.getRegion() != null && !projectDTO.getRegion().isEmpty()){
                projectInfo.setRegion(projectDTO.getRegion());
                isUpdated = true;
            }
            if(projectDTO.getProject_leader() != null && !projectDTO.getProject_leader().isEmpty()){
                projectInfo.setProject_leader(projectDTO.getProject_leader());
                isUpdated = true;
            }
            if(isUpdated){
                projectInfo.setRegDt(new Date());
                projectInfo.setUserName(jwtTokenProvider.extractUserName(token));
                projectRepository.save(projectInfo);

                ProjectLog projectLog = new ProjectLog();
                projectLog.setProject_leader(projectInfo.getProject_leader());
                projectLog.setProject_status(projectInfo.getProject_status());
                projectLog.setCustomer(projectInfo.getCustomer());
                projectLog.setDeptCd(projectInfo.getDeptCd());
                projectLog.setStartDt(projectInfo.getStartDt());
                projectLog.setEndDt(projectInfo.getEndDt());
                projectLog.setRegion(projectInfo.getRegion());
                if(projectInfo.getStatus().equals("Y")) {
                    projectLog.setStatus(projectInfo.getStatus());
                } else {
                    projectLog.setStatus(projectInfo.getStatus());
                }
                projectLog.setUserName(jwtTokenProvider.extractUserName(token));
                projectLog.setRegDt(new Date());
                projectLogRepository.save(projectLog);

                return ResponseEntity.ok("프로젝트 정보 수정/삭제 완료");
            } else{
                return ResponseEntity.ok("수정할 내용 없음");
            }
        }else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("프로젝트 없음");
        }
    }
}
