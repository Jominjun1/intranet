package com.example.tag_dev.LOG.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name="TB_LOG_DEPT")
public class DeptLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="seq")
    private Long logseq;

    @Column(name="DEPT_CD")
    private String deptCode; // 부서코드
    private String dept; // 부서명
    @Column(name="USER_NAME")
    private String userName; // 등록자 명
    @Column(name="REG_DT")
    private Date regDt;
    @Column(name="UPDATE_DT")
    private Date updateDt;
    @Column(name="UPDATE_USER")
    private String updateUser;
    @Column(name="STATUS")
    private String Status;
}
