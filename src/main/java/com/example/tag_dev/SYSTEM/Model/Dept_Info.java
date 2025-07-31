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
@Table(name="TB_BI_DEPT_INFO")
public class Dept_Info {

    @Id
    @Column(name="DEPT_CD")
    private String deptCode; // 부서코드
    private String dept; // 부서명
    @Column(name="USER_NAME")
    private String userName; // 등록자 명
    private String del_yn; // 삭제여부
    @Column(name="REG_DT")
    private Date regDt; // 등록일
    @Column(name="UPDATE_DT")
    private Date updateDt; // 수정일
    @Column(name="UPDATE_USER")
    private String updateUser; // 수정자 명
    @Column(name="DEPT_STATUS")
    private String deptStatus; // 이용 여부
}
