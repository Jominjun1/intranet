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
@Table(name="TB_TAG_PROD_AS")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "prod_as_id")
public class Prod_As {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="seq")
    private Long prod_as_id;

    @Column(name="ORD_NO")
    private String ordNo; // 스마트 태그 번호
    @Column(name="AS_CNT")
    private Long asCnt; // AS 횟수
    @Column(name="AS_DOC")
    private String asDoc; // AS 문서번호
    private Date Occr_dt; // AS 발생일
    private String Occ_rrsn; // AS 발생사유
    private Date Close_dt; // AS 종결일
    private String Close_rslt; // AS 처리 결과
    private Date Delivery_dt; // 납품일
    private Date Create_dt; // 생성일
    private String Create_id; // 생성자(사번)
    private Date Update_dt; // 수정일
    private String Update_id; // 수정자(사번)
    private String Status;

}
