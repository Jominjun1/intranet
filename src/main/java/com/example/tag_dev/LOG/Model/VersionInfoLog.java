package com.example.tag_dev.LOG.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name="TB_LOG_VERSION_INFO")
public class VersionInfoLog {

    @Id
    @Column(name= "seq")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logseq;

    @Column(name="TAG_NO")
    private String tagNo; // 스마트태그 번호
    private String LogType; // 로그 타입
    private String TAG_VER; // 제품 번호
    private String DEL_RSN; // 스마트 태그 삭제 사유
    @Column(name="CREATE_DT")
    private Date createDt; // 생성일
    private String CREATE_ID; // 생성자(사번)
    private Date UPDATE_DT; // 수정일
    private String UPDATE_ID; // 수정자(사번)
    private String Status;

}
