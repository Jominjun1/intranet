package com.example.tag_dev.LOG.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name="TB_LOG_COMMON_INFO")
public class CommonInfoLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="seq")
    private Long logseq;

    @Column(name="MAC_ADDR")
    private String macAddr; // MAC 주소
    @Column(name="FAC_CD")
    private String facCd; // 공장 시리얼 코드
    @Column(name="FAC_NO")
    private String facNo; // 공장 시리얼 번호

    private String LogType; // 로그 타입

    @Column(name="CREATE_DT")
    private Date createDt; // 생성일
    private String CREATE_ID; // 생성자(사번)
    private Date UPDATE_DT; // 수정일
    private String UPDATE_ID; // 수정자(사번)
}
