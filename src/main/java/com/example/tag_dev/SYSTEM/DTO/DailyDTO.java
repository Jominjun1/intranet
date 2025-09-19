package com.example.tag_dev.SYSTEM.DTO;

import lombok.Data;

import java.util.Date;

@Data
public class DailyDTO {

    private String userName; // 작성자
    private String approvalNm; // 승인자

    private String txt; // 업무 내용 작성
    private String hour; // 시간 ( 시 )
    private String minute; // 시간 ( 분 )
    private Date createDt; // 업무 작성 일자

    private String deptCode; // 부서코드
    private String dept; // 부서명

    private String projectCode; // 프로젝트 코드
    private String project_name; // 프로젝트 명
    private String project_leader; // 프로젝트 PM

    private String dailyStatus; // 승인 여부
    private String status; // 삭제 여부
}
