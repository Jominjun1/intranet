package com.example.tag_dev.LOG.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
@Entity
@Table(name="TB_LOG_SETTING_INFO")
public class SettingInfoLog {

    @Id
    @Column(name="seq")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logseq;

    @Column(name="ORD_NO")
    private String ordNo; // 스마트 태그 번호
    private String HW_VER; // 하드웨어 버전
    private String FW_VER; // 펌웨어 버전
    private String LED_SEC; // LED ON 주기
    private String RI_MS; // 송신주기
    private String TX_POWER; // 신호 강도
    private String RANDOM_DV; // 송신 방식(비공정형, 고정형)
    private String RF_PROFILE; // RF신호의 프로파일
    private String CHANNEL; // 통신 채널
    private String SLEEP_MODE; // 슬립모드
    private String SLEEP_TH_HOLD; // 슬립모드 강도
    private String SLEEP_INTERVAL; // RISM
    private String SLEEP_PERIOD; // sleep mode entry time
    private String BC_VER; // Back Channel 버전
    private String BC_PERIOD; // Back Channel 주기
    private String BC_SLEEP; // Back Channel Sleep 상태에서 주기
    private String DEVICE_IP; // 디바이스 ip
    private String SERVER_IP; // 서버 ip
    private String GATEWAY; // 게이트웨이
    private String SUB_MASK; // 서브넷 마스크
    private String TDMA; // 시간 분할 다중 접속 방식
    private String PORT; // 포트 번호
    @Column(name="CREATE_DT")
    private Date createDt;// 생성일
    private String CREATE_ID; // 생성자(사번)
    private Date UPDATE_DT; // 수정일
    private String UPDATE_ID; // 수정자(사번)
    private String Status;
}
