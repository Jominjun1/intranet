package com.example.tag_dev.SYSTEM.Model;


import com.example.tag_dev.USER.Model.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
@Entity
@Table(name="TB_BI_DAILY_REPORT_INFO")
public class DailyReport_info {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name ="USER_NAME")
    private String userName; // 작성자
    private String loginId; // 작성자 ID
    @Column(name = "APPROVAL_NAME")
    private String approvalNm; // 승인자
    @Column(name = "APPROVAL_DATE")
    private Date approvalDate; // 승인 일자

    private String txt; // 업무 내용 작성
    private String hour; // 시간 ( 시 )
    private String minute; // 시간 ( 분 )
    @Column(name ="CREATE_DT")
    private Date createDt; // 업무 작성 일자

    @Column(name="DEPT_CD")
    private String deptCode; // 부서코드
    private String dept; // 부서명

    private String project_name; // 프로젝트 명
    private String project_leader; // 프로젝트 PM

    private String dailyStatus; // 승인 여부
    private String status; // 삭제 여부

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "USER_ID" , referencedColumnName = "seq")
    private User user;

    @OneToMany(mappedBy = "dailyReportInfo", orphanRemoval = true)
    private List<Daily_ProJect_R> reportProjects = new ArrayList<>();

}
