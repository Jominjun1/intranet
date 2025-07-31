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

    @Column(name="TAG_NO")
    private String tagNo;  // 스마트태그 번호
    @Column(name="tagType")
    private String tagType; // 스마트 태그 타입
    private String ERP_CD; // ERP 코드
    private String MNG_CTG; // 관리 카테고리(태그 ,앵커 , SC)
    private String LOT; // LOT 번호
    private String PROD_ODR; // 생산 지시
    private String PJT_CD; // 프로젝트 코드
    private String PJT_MNGR; // 프로젝트 매니저
    private String MAC_DUP_YN; // MAC 중복 여부
    private Date CREATE_DT; // 생성일
    private String CREATE_ID; // 생성자(사번)
    private Date UPDATE_DT; // 수정일
    private String UPDATE_ID; // 수정자(사번)

}
