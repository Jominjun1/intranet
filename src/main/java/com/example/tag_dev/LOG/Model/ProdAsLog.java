package com.example.tag_dev.LOG.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Setter
@Getter
@Table(name= "TB_LOG_PROD_AS")
public class ProdAsLog {

    @Id
    @Column(name="seq")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long AS_LOG_SEQ;

    @Column(name= "TAG_NO")
    private String tagNo; // 스마트 태그 번호
    private String LogType; // 로그 타입
    private Long AS_CNT; // AS 횟수
    private String AS_DOC; // AS 문서번호
    private Date OCCR_DT; // AS 발생일
    private String OCCR_RSN; // AS 발생사유
    private Date CLOSE_DT; // AS 종결일
    private String CLOSE_RSLT; // AS 처리 결과
    private Date DELIVERY_DT; // 납품일
    @Column(name="CREATE_DT")
    private Date createDt;// 생성일
    private String CREATE_ID; // 생성자(사번)
    private Date UPDATE_DT; // 수정일
    private String UPDATE_ID; // 수정자(사번)
}
