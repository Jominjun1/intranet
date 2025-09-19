package com.example.tag_dev.LOG.Service;

import com.example.tag_dev.LOG.Model.*;
import com.example.tag_dev.LOG.Repository.*;
import com.example.tag_dev.TAG.Repository.SettingInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class LogService {

    private final BasicInfoLogRepository basicInfoLogRepository;
    private final CommonInfoLogRepository commonInfoLogRepository;
    private final DeptLogRepository deptLogRepository;
    private final ProjectLogRepository projectLogRepository;
    private final ProdAsLogRepository prodAsLogRepository;
    private final ProcStepLogRepository procStepLogRepository;
    private final SettingInfoRepository settingInfoRepository;
    private final UserLogRepository userLogRepository;
    private final VersionInfoLogRepository versionInfoLogRepository;
    private final SettingInfoLogRepository settingInfoLogRepository;
    private final DailyReportLogRepository dailyReportLogRepository;

    // 로그 조회
    public ResponseEntity<?> searchLog(String type, String startDate, String endDate) {
        try {
            List<Map<String, Object>> result = new ArrayList<>();

            // 날짜 파싱
            Date start = null;
            Date end = null;
            if (startDate != null && !startDate.isEmpty()) {
                start = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(startDate);
            }
            if (endDate != null && !endDate.isEmpty()) {
                end = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(endDate);
                // 종료일을 23:59:59로 설정
                Calendar cal = Calendar.getInstance();
                cal.setTime(end);
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                end = cal.getTime();
            }

            switch (type) {
                case "user":
                    List<UserLog> userLogs;
                    if (start != null && end != null) {
                        userLogs = userLogRepository.findByRegDtBetween(start, end);
                    } else {
                        userLogs = userLogRepository.findAll();
                    }
                    for (UserLog log : userLogs) {
                        Map<String, Object> logMap = new HashMap<>();
                        logMap.put("logId", log.getLogId());
                        logMap.put("loginId", log.getLoginId());
                        logMap.put("status", log.getStatus());
                        logMap.put("ip_addr", log.getIp_addr());
                        logMap.put("http_refr", log.getHttp_refr());
                        logMap.put("reg_dt", log.getRegDt());
                        logMap.put("update_dt", log.getUpdate_dt());
                        logMap.put("update_id", log.getUpdate_id());
                        result.add(logMap);
                    }
                    break;

                case "basic":
                    List<BasicInfoLog> basicLogs;
                    if (start != null && end != null) {
                        basicLogs = basicInfoLogRepository.findByCreateDtBetween(start, end);
                    } else {
                        basicLogs = basicInfoLogRepository.findAll();
                    }
                    for (BasicInfoLog log : basicLogs) {
                        Map<String, Object> logMap = new HashMap<>();
                        logMap.put("logId", log.getLog_seq());
                        logMap.put("ordNo", log.getOrdNo());
                        logMap.put("tagType", log.getTagType());
                        logMap.put("erpCd", log.getERP_CD());
                        logMap.put("mngCtg", log.getMNG_CTG());
                        logMap.put("lot", log.getLOT());
                        logMap.put("prodOdr", log.getPROD_ODR());
                        logMap.put("pjtCd", log.getPJT_CD());
                        logMap.put("pjtMngr", log.getPJT_MNGR());
                        logMap.put("macDupYn", log.getMAC_DUP_YN());
                        logMap.put("delRsn", log.getDEL_RSN());
                        logMap.put("createDt", log.getCreateDt());
                        logMap.put("createId", log.getCREATE_ID());
                        logMap.put("updateDt", log.getUPDATE_DT());
                        logMap.put("updateId", log.getUPDATE_ID());
                        result.add(logMap);
                    }
                    break;

                case "common":
                    List<CommonInfoLog> commonLogs;
                    if (start != null && end != null) {
                        commonLogs = commonInfoLogRepository.findByCreateDtBetween(start, end);
                    } else {
                        commonLogs = commonInfoLogRepository.findAll();
                    }
                    for (CommonInfoLog log : commonLogs) {
                        Map<String, Object> logMap = new HashMap<>();
                        logMap.put("logId", log.getLogseq());
                        logMap.put("macAddr", log.getMacAddr());
                        logMap.put("facCd", log.getFacCd());
                        logMap.put("facNo", log.getFacNo());
                        logMap.put("createDt", log.getCreateDt());
                        logMap.put("createId", log.getCREATE_ID());
                        logMap.put("updateDt", log.getUPDATE_DT());
                        logMap.put("updateId", log.getUPDATE_ID());
                        result.add(logMap);
                    }
                    break;

                case "setting":
                    List<SettingInfoLog> settingLogs;
                    if (start != null && end != null) {
                        settingLogs = settingInfoLogRepository.findByCreateDtBetween(start, end);
                    } else {
                        settingLogs = settingInfoLogRepository.findAll();
                    }
                    for (SettingInfoLog log : settingLogs) {
                        Map<String, Object> logMap = new HashMap<>();
                        logMap.put("logId", log.getLogseq());
                        logMap.put("ordNo", log.getOrdNo());
                        logMap.put("hwVer", log.getHW_VER());
                        logMap.put("fwVer", log.getFW_VER());
                        logMap.put("ledSec", log.getLED_SEC());
                        logMap.put("riMs", log.getRI_MS());
                        logMap.put("txPower", log.getTX_POWER());
                        logMap.put("randomDv", log.getRANDOM_DV());
                        logMap.put("rfProfile", log.getRF_PROFILE());
                        logMap.put("channel", log.getCHANNEL());
                        logMap.put("sleepMode", log.getSLEEP_MODE());
                        logMap.put("sleepThHold", log.getSLEEP_TH_HOLD());
                        logMap.put("sleepInterval", log.getSLEEP_INTERVAL());
                        logMap.put("sleepPeriod", log.getSLEEP_PERIOD());
                        logMap.put("bcVer", log.getBC_VER());
                        logMap.put("bcPeriod", log.getBC_PERIOD());
                        logMap.put("bcSleep", log.getBC_SLEEP());
                        logMap.put("deviceIp", log.getDEVICE_IP());
                        logMap.put("serverIp", log.getSERVER_IP());
                        logMap.put("gateway", log.getGATEWAY());
                        logMap.put("subMask", log.getSUB_MASK());
                        logMap.put("tdma", log.getTDMA());
                        logMap.put("port", log.getPORT());
                        logMap.put("createDt", log.getCreateDt());
                        logMap.put("createId", log.getCREATE_ID());
                        logMap.put("updateDt", log.getUPDATE_DT());
                        logMap.put("updateId", log.getUPDATE_ID());
                        result.add(logMap);
                    }
                    break;

                case "proc":
                    List<ProcStepLog> procLogs;
                    if (start != null && end != null) {
                        procLogs = procStepLogRepository.findByCreateDtBetween(start, end);
                    } else {
                        procLogs = procStepLogRepository.findAll();
                    }
                    for (ProcStepLog log : procLogs) {
                        Map<String, Object> logMap = new HashMap<>();
                        logMap.put("logId", log.getLogseq());
                        logMap.put("ordNo", log.getOrdNo());
                        logMap.put("receiptDt", log.getRECEIPT_DT());
                        logMap.put("deliveryDt", log.getDELIVERY_DT());
                        logMap.put("labInspDt", log.getLAB_INSP_DT());
                        logMap.put("labInspDesc", log.getLAB_INSP_DESC());
                        logMap.put("techInspDt", log.getTECH_INSP_DT());
                        logMap.put("techInspDesc", log.getTECH_INSP_DESC());
                        logMap.put("createDt", log.getCreateDt());
                        logMap.put("createId", log.getCREATE_ID());
                        logMap.put("updateDt", log.getUPDATE_DT());
                        logMap.put("updateId", log.getUPDATE_ID());
                        result.add(logMap);
                    }
                    break;

                case "prod":
                    List<ProdAsLog> prodLogs;
                    if (start != null && end != null) {
                        prodLogs = prodAsLogRepository.findByCreateDtBetween(start, end);
                    } else {
                        prodLogs = prodAsLogRepository.findAll();
                    }
                    for (ProdAsLog log : prodLogs) {
                        Map<String, Object> logMap = new HashMap<>();
                        logMap.put("logId", log.getAS_LOG_SEQ());
                        logMap.put("ordNo", log.getOrdNo());
                        logMap.put("asCnt", log.getAS_CNT());
                        logMap.put("asDoc", log.getAS_DOC());
                        logMap.put("occrDt", log.getOCCR_DT());
                        logMap.put("occrRsn", log.getOCCR_RSN());
                        logMap.put("closeDt", log.getCLOSE_DT());
                        logMap.put("closeRslt", log.getCLOSE_RSLT());
                        logMap.put("deliveryDt", log.getDELIVERY_DT());
                        logMap.put("createDt", log.getCreateDt());
                        logMap.put("createId", log.getCREATE_ID());
                        logMap.put("updateDt", log.getUPDATE_DT());
                        logMap.put("updateId", log.getUPDATE_ID());
                        result.add(logMap);
                    }
                    break;

                case "version":
                    List<VersionInfoLog> versionLogs;
                    if (start != null && end != null) {
                        versionLogs = versionInfoLogRepository.findByCreateDtBetween(start, end);
                    } else {
                        versionLogs = versionInfoLogRepository.findAll();
                    }
                    for (VersionInfoLog log : versionLogs) {
                        Map<String, Object> logMap = new HashMap<>();
                        logMap.put("logId", log.getLogseq());
                        logMap.put("ordNo", log.getOrdNo());
                        logMap.put("tagVer", log.getTAG_VER());
                        logMap.put("delRsn", log.getDEL_RSN());
                        logMap.put("createDt", log.getCreateDt());
                        logMap.put("createId", log.getCREATE_ID());
                        logMap.put("updateDt", log.getUPDATE_DT());
                        logMap.put("updateId", log.getUPDATE_ID());
                        result.add(logMap);
                    }
                    break;

                default:
                    return ResponseEntity.badRequest().body("지원하지 않는 로그 타입입니다: " + type);
            }

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("로그 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
