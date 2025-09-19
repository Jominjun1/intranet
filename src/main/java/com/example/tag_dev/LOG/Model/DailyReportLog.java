package com.example.tag_dev.LOG.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name="TB_LOG_DailyReport")
public class DailyReportLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="seq")
    private Long log_seq;

    private String hour; // 시간 ( 시 )
    private String minute; // 시간 ( 분 )

    @Column(name="CREATE_DT")
    private Date createDt; // 생성일
    private String CREATE_ID; // 생성자(사번)
    private Date UPDATE_DT; // 수정일
    private String UPDATE_ID; // 수정자(사번)

    @Column(name="DEPT_CD")
    private String deptCode; // 부서코드
    private String dept; // 부서명

    @Column(name="PROJECT_CODE")
    private String projectCode; // 프로젝트 코드
    private String project_name; // 프로젝트 명
    private String project_leader; // 프로젝트 PM

    private String DailyStatus; // 승인 여부
    private String Status; // 삭제 여부
}
