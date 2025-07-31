package com.example.tag_dev.SYSTEM.DTO;

import lombok.Data;

import java.util.Date;

@Data
public class DeptDTO {

    private String deptCode; // 부서코드
    private String dept; // 부서명
    private String userName;
    private Date regDt;
    private Date updateDt;
    private String updateUser;
    private String deptStatus;
    private String delYn; // 삭제여부
}
