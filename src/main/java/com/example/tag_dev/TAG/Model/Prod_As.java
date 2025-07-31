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

    @Column(name="TAG_NO")
    private String tagNo; // 스마트 태그 번호
    private Long AS_CNT; // AS 횟수
    private String AS_DOC; // AS 문서번호
    private Date OCCR_DT; // AS 발생일
    private String OCCR_RSN; // AS 발생사유
    private Date CLOSE_DT; // AS 종결일
    private String CLOSE_RSLT; // AS 처리 결과
    private Date DELIVERY_DT; // 납품일
    private Date CREATE_DT; // 생성일
    private String CREATE_ID; // 생성자(사번)
    private Date UPDATE_DT; // 수정일
    private String UPDATE_ID; // 수정자(사번)
    private Date DEL_DT; // 삭제일

}
