package com.example.tag_dev.SYSTEM.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name="TB_BI_PROJECT_INFO")
public class Project_Info {

    @Id
    @Column(name="PROJECT_CODE")
    private String projectCode;

    private String project_name; // 프로젝트 명
    private String project_leader; // 프로젝트 PM
    private String project_category; // 프로젝트 유형
    private String Status; // 삭제여부
    private String customer; // 고객사
    private String region; // 지역
    private String deptCd; // 담당 부서
    private String project_status; // 프로젝트 진행 상황
    private Date startDt; // 계약 날짜
    private Date endDt; // 종료 날짜

    @Column(name="USER_NAME")
    private String userName; // 등록한 사람
    @Column(name="REG_DT")
    private Date regDt; // 등록일
}
