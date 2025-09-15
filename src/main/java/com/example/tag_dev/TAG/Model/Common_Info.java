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
@Table(name="TB_TAG_COMMON_INFO")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "seq")
public class Common_Info {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="seq")
    private Long common_info_seq;

    @Column(name="ORD_NO")
    private String ordNo;
    @Column(name="MAC_ADDR")
    private String macAddr; // MAC 주소
    @Column(name="FAC_CD")
    private String facCd; // 공장 시리얼 코드
    @Column(name="FAC_NO")
    private String facNo; // 공장 시리얼 번호

    private Date Create_dt; // 생성일
    private String Create_id; // 생성자(사번)
    private Date Update_dt; // 수정일
    private String Update_id; // 수정자(사번)
    private String Status; // 삭제여부
}
