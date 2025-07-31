package com.example.tag_dev.SYSTEM.DTO;

import lombok.Data;

import java.util.Date;

@Data
public class ProjectDTO {

    private String projectCode;
    private String project_name; // 프로젝트 명
    private String project_leader; // 프로젝트 PM
    private String project_category; // 프로젝트 유형
    private String customer; // 고객사
    private String region; // 지역
    private String deptCd; // 담당 부서
    private String project_status; // 프로젝트 진행 상황
    private Date startDt; // 계약 날짜
    private Date endDt; // 종료 날짜

    private String userName; // 등록한 사람
    private Date regDt; // 등록일
}
