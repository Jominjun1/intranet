package com.example.tag_dev.LOG.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name="TB_LOG_BASIC_INFO")
public class BasicInfoLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="seq")
    private Long log_seq;

    @Column(name="ORD_NO")
    private String ordNo;  // 스마트태그 번호
    @Column(name="tagType")
    private String tagType; // 스마트 태그 타입
    private String Status;
    private String ERP_CD; // ERP 코드
    private String MNG_CTG; // 관리 카테고리(태그 ,앵커 , SC)
    private String LOT; // LOT 번호
    private String PROD_ODR; // 생산 지시
    private String PJT_CD; // 프로젝트 코드
    private String PJT_MNGR; // 프로젝트 매니저
    private String MAC_DUP_YN; // MAC 중복 여부
    private String DEL_RSN; // 스마트 태그 삭제 사유
    @Column(name="CREATE_DT")
    private Date createDt; // 생성일
    private String CREATE_ID; // 생성자(사번)
    private Date UPDATE_DT; // 수정일
    private String UPDATE_ID; // 수정자(사번)

}
