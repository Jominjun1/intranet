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

    @Column(name ="TAG_NO")
    private String tagNo;  // 스마트태그 번호
    private Date RECEIPT_DT; // 입고일
    private Date DELIVERY_DT; // 납품일
    private Date LAB_INSP_DT; // 연구소 검수일
    private String LAB_INSP_DESC; // 연구소 검수소견
    private Date TECH_INSP_DT; // 융합기술팀 검수일
    private String TECH_INSP_DESC; // 융합기술팀 검수소견
    private Date CREATE_DT; // 생성일
    private String CREATE_ID; // 생성자(사번)
    private Date UPDATE_DT; // 수정일
    private String UPDATE_ID; // 수정자(사번)

}
