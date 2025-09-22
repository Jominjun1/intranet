package com.example.tag_dev.LOG.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name="TB_LOG_PROJECT")
public class ProjectLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="seq")
    private Long logseq;

    private String project_name; // 프로젝트 명
    private String project_leader; // 프로젝트 PM
    private String project_category; // 프로젝트 유형
    private String customer; // 고객사
    private String region; // 지역
    private String deptCd; // 담당 부서
    private String Status;
    private String project_status; // 프로젝트 진행 상황
    private Date startDt; // 계약 날짜
    private Date endDt; // 종료 날짜
    private Date createDt; // 생성일
    private String create_id; // 생성자

}
