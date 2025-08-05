package com.example.tag_dev.TAG.Model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name="TB_TAG_PROC_STEP")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "proc_step_seq")
public class Proc_Step {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="seq")
    private Long proc_step_seq;

    @Column(name ="ORD_NO")
    private String ordNo;  // 스마트태그 번호
    private Date Receipt_dt; // 입고일
    private Date Delivery_dt; // 납품일
    private Date Lab_insp_dt; // 연구소 검수일
    private String Lab_insp_desc; // 연구소 검수소견
    private Date Tech_inst_dt; // 융합기술팀 검수일
    private String Tech_inst_desc; // 융합기술팀 검수소견
    private Date Create_dt; // 생성일
    private String Create_id; // 생성자(사번)
    private Date Update_dt; // 수정일
    private String Update_id; // 수정자(사번)
    private String Status;

}
