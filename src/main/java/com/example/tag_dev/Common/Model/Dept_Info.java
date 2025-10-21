package com.example.tag_dev.Common.Model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    @Column(name="STATUS")
    private String Status; // 사용여부
    @Column(name="REG_DT")
    private Date regDt; // 등록일
    @Column(name="UPDATE_DT")
    private Date updateDt; // 수정일
    @Column(name="UPDATE_USER")
    private String updateUser; // 수정자 명

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "PARENT_DEPT_CD") // 상위 부서 코드
    private Dept_Info parentDept;

    @OneToMany(mappedBy = "parentDept" , cascade = CascadeType.ALL , orphanRemoval = true)
    @JsonManagedReference
    private List<Dept_Info> childDepts = new ArrayList<>();
}
