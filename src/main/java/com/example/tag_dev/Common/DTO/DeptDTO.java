package com.example.tag_dev.Common.DTO;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class DeptDTO {

    private String deptCode; // 부서코드
    private String dept; // 부서명
    private String userName;
    private Date regDt;
    private Date updateDt;
    private String updateUser;
    private String Status;

    private String parentDeptCode; // 상위 부서 코드
    private List<String> childDeptCodes; // 하위 부서 코드 목록(선택)
}
