package com.example.tag_dev.TAG.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class TagSettingDTO {
    @JsonProperty("ORD_NO")
    private String ORD_NO; // 스마트 태그 번호
    
    @JsonProperty("HW_VER")
    private String HW_VER; // 하드웨어 버전
    
    @JsonProperty("FW_VER")
    private String FW_VER; // 펌웨어 버전
    
    @JsonProperty("LED_SEC")
    private String LED_SEC; // LED ON 주기
    
    @JsonProperty("RI_MS")
    private String RI_MS; // 송신주기
    
    @JsonProperty("TX_POWER")
    private String TX_POWER; // 신호 강도
    
    @JsonProperty("RANDOM_DV")
    private String RANDOM_DV; // 송신 방식(비공정형, 고정형)
    
    @JsonProperty("RF_PROFILE")
    private String RF_PROFILE; // RF신호의 프로파일
    
    @JsonProperty("CHANNEL")
    private String CHANNEL; // 통신 채널
    
    @JsonProperty("SLEEP_MODE")
    private String SLEEP_MODE; // 슬립모드
    
    @JsonProperty("SLEEP_TH_HOLD")
    private String SLEEP_TH_HOLD; // 슬립모드 강도
    
    @JsonProperty("SLEEP_INTERVAL")
    private String SLEEP_INTERVAL; // RISM
    
    @JsonProperty("SLEEP_PERIOD")
    private String SLEEP_PERIOD; // sleep mode entry time
    
    @JsonProperty("BC_VER")
    private String BC_VER; // Back Channel 버전
    
    @JsonProperty("BC_PERIOD")
    private String BC_PERIOD; // Back Channel 주기
    
    @JsonProperty("BC_SLEEP")
    private String BC_SLEEP; // Back Channel Sleep 상태에서 주기
    
    @JsonProperty("DEVICE_IP")
    private String DEVICE_IP; // 디바이스 ip
    
    @JsonProperty("SERVER_IP")
    private String SERVER_IP; // 서버 ip
    
    @JsonProperty("GATEWAY")
    private String GATEWAY; // 게이트웨이
    
    @JsonProperty("SUB_MASK")
    private String SUB_MASK; // 서브넷 마스크
    
    @JsonProperty("TDMA")
    private String TDMA; // 시간 분할 다중 접속 방식
    
    @JsonProperty("PORT")
    private String PORT; // 포트 번호
    
    @JsonProperty("CREATE_DT")
    private Date CREATE_DT; // 생성일
    
    @JsonProperty("CREATE_ID")
    private String CREATE_ID; // 생성자(사번)
    
    @JsonProperty("UPDATE_DT")
    private Date UPDATE_DT; // 수정일
    
    @JsonProperty("UPDATE_ID")
    private String UPDATE_ID; // 수정자(사번)
    
    @JsonProperty("Status")
    private String Status; // 삭제여부
}
