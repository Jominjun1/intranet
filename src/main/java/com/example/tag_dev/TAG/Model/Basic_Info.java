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
@Table(name="TB_TAG_BASIC_INFO")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "seq")
public class Basic_Info {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="seq")
    private Long basic_info_seq;

    @Column(name="ORD_NO")
    private String ordNo;  // 스마트태그 번호


    @Column(name="TAG_TYPE")
    private String tagType; // 스마트 태그 타입
    @Column(name="ERP_CODE")
    private String erpCode; // ERP 코드
    @Column(name="MNG_CATEGORY")
    private String MngCategory; // 관리 카테고리(태그 ,앵커 , SC)
    private String Lot; // LOT 번호
    private String Prod_order; // 생산 지시
    private String Project_code; // 프로젝트 코드
    private String Project_manager; // 프로젝트 매니저
    private String Mac_duple_yn; // MAC 중복 여부
    private Date Create_dt; // 생성일
    private String Create_id; // 생성자(사번)
    private Date Update_dt; // 수정일
    private String Update_id; // 수정자(사번)
    private String Status; // 삭제여부

}
