package com.example.tag_dev.TAG.Service;

import com.example.tag_dev.LOG.LogRepository.*;
import com.example.tag_dev.TAG.DTO.TagSettingDTO;
import com.example.tag_dev.LOG.Model.CommonInfoLog;
import com.example.tag_dev.LOG.Model.ProdAsLog;
import com.example.tag_dev.LOG.Model.SettingInfoLog;
import com.example.tag_dev.LOG.Model.VersionInfoLog;
import com.example.tag_dev.TAG.Model.*;
import com.example.tag_dev.TAG.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class TagService {

    @Autowired
    private final BasicInfoRepository basicRepo;
    private final CommonInfoRepository commonRepo;
    private final VersionInfoRepository versionRepo;
    private final ProcStepRepository procStepRepo;
    private final SettingInfoRepository settingRepo;
    private final ProdAsRepository prodAsRepo;
    private final ProdAsLogRepository prodAsLogRepo;
    private final ProcStepLogRepository procStepLogRepo;
    private final SettingInfoLogRepository settingLogRepo;
    private final BasicInfoLogRepository basicInfoLog;
    private final VersionInfoLogRepository versionInfoLog;
    private final CommonInfoLogRepository commonLogRepo;



    public TagService(BasicInfoRepository basicRepo, CommonInfoRepository commonRepo, VersionInfoRepository versionRepo, ProcStepRepository procStepRepo,
                      SettingInfoRepository settingRepo, ProdAsRepository prodAsRepo, ProdAsLogRepository prodAsLogRepo, ProcStepLogRepository procStepLogRepo, SettingInfoLogRepository settingLogRepo, BasicInfoLogRepository basicInfoLog, VersionInfoLogRepository versionInfoLog, CommonInfoLogRepository commonLogRepo) {
        this.basicRepo = basicRepo;
        this.commonRepo = commonRepo;
        this.versionRepo = versionRepo;
        this.procStepRepo = procStepRepo;
        this.settingRepo = settingRepo;
        this.prodAsRepo = prodAsRepo;

        this.prodAsLogRepo = prodAsLogRepo;
        this.procStepLogRepo = procStepLogRepo;
        this.settingLogRepo = settingLogRepo;
        this.basicInfoLog = basicInfoLog;
        this.versionInfoLog = versionInfoLog;
        this.commonLogRepo = commonLogRepo;
    }

    public ResponseEntity<?> getTagInventoryList(String macAddr, String facCd, String facNo, String delFilter) {
        try {
            // 검색 조건이 없으면 빈 배열 반환
            boolean hasSearchCondition = (macAddr != null && !macAddr.trim().isEmpty()) || 
                                       (facCd != null && !facCd.trim().isEmpty()) || 

                                       (facNo != null && !facNo.trim().isEmpty());
            
            if (!hasSearchCondition) {
                return ResponseEntity.ok(new ArrayList<>());
            }
            List<Basic_Info> basics = basicRepo.findAll();
            List<Common_Info> commons = commonRepo.findAll();
            List<Map<String, Object>> result = new ArrayList<>();
            for (Basic_Info basic : basics) {
                Common_Info common = commons.stream()
                    .filter(c -> basic.getTagNo().equals(
                        (c.getMacAddr() != null ? c.getMacAddr().replace(":", "") : "") +
                        (c.getFacCd() != null ? c.getFacCd() : "") +
                        (c.getFacNo() != null ? c.getFacNo() : "")
                    ))
                    .findFirst().orElse(null);
                
                // 검색 조건에 맞는지 확인
                if (common != null) {
                    if ((macAddr != null && !macAddr.trim().isEmpty() && !common.getMacAddr().contains(macAddr)) ||
                        (facCd != null && !facCd.trim().isEmpty() && !common.getFacCd().contains(facCd)) ||
                        (facNo != null && !facNo.trim().isEmpty() && !common.getFacNo().contains(facNo))) {
                        continue;
                    }
                } else {
                    // common이 null이어도 검색 조건이 없으면 포함
                    if (hasSearchCondition) {
                        continue;
                    }
                }
                Map<String, Object> row = new HashMap<>();
                row.put("tagNo", basic.getTagNo());
                row.put("tagType", basic.getTagType());
                row.put("erpCd", basic.getERP_CD());
                row.put("mngCtg", basic.getMNG_CTG());
                row.put("lot", basic.getLOT());
                row.put("prodOdr", basic.getPROD_ODR());
                row.put("pjtCd", basic.getPJT_CD());
                row.put("pjtMngr", basic.getPJT_MNGR());
                row.put("macDupYn", basic.getMAC_DUP_YN());
                
                // common 정보가 있으면 추가
                if (common != null) {
                    row.put("macAddr", common.getMacAddr());
                    row.put("facCd", common.getFacCd());
                    row.put("facNo", common.getFacNo());
                } else {
                    row.put("macAddr", null);
                    row.put("facCd", null);
                    row.put("facNo", null);
                }
                Optional<Version_Info> ver = versionRepo.findAll().stream().filter(v -> v.getTagNo().equals(basic.getTagNo())).max(Comparator.comparing(Version_Info::getTAG_VER));
                row.put("tagVer", ver.map(Version_Info::getTAG_VER).orElse("1.0"));
                long asCount = prodAsRepo.findAll().stream()
                    .filter(a -> a.getTagNo().equals(basic.getTagNo()))
                    .filter(a -> a.getAS_CNT() != null && a.getAS_CNT() > 0)
                    .count();
                row.put("asCnt", asCount);
                result.add(row);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    public ResponseEntity<?> getProcStep(String tagNo) {
        try {
            Proc_Step result = procStepRepo.findAll().stream().filter(p -> tagNo.equals(p.getTagNo())).findFirst().orElse(null);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    public ResponseEntity<?> getSettingInfo(String tagNo) {
        try {
            Setting_Info result = settingRepo.findAll().stream().filter(s -> tagNo.equals(s.getTagNo())).findFirst().orElse(null);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    public ResponseEntity<?> updateSettingInfo(String tagNo, TagSettingDTO dto) {
        try {
            Setting_Info setting = settingRepo.findAll().stream().filter(s -> tagNo.equals(s.getTagNo())).findFirst().orElse(null);
            if (setting != null) {
                setting.setHW_VER(dto.getHW_VER());
                setting.setFW_VER(dto.getFW_VER());
                setting.setUPDATE_DT(new Date());
                setting.setUPDATE_ID(dto.getUPDATE_ID());
                settingRepo.save(setting);

                SettingInfoLog settingInfoLog = new SettingInfoLog();
                settingInfoLog.setTagNo(tagNo);
                settingInfoLog.setHW_VER(dto.getHW_VER());
                settingInfoLog.setFW_VER(dto.getFW_VER());
                settingInfoLog.setUPDATE_ID(dto.getUPDATE_ID());
                settingInfoLog.setUPDATE_DT(new Date());
                settingInfoLog.setUPDATE_ID(dto.getUPDATE_ID());
                settingInfoLog.setLogType("UPDATE");
                settingLogRepo.save(settingInfoLog);

                Optional<Version_Info> verOpt = versionRepo.findAll().stream().filter(v -> v.getTagNo().equals(tagNo)).max(Comparator.comparing(Version_Info::getTAG_VER));
                String newVer = verOpt.map(v -> {
                    try {
                        double vNum = Double.parseDouble(v.getTAG_VER());
                        return String.format("%.1f", vNum + 0.1);
                    } catch (Exception e) { return "1.1"; }
                }).orElse("1.1");
                Version_Info newVersion = new Version_Info();
                newVersion.setTagNo(tagNo);
                newVersion.setTAG_VER(newVer);
                newVersion.setCREATE_DT(new Date());
                newVersion.setCREATE_ID(dto.getUPDATE_ID());
                versionRepo.save(newVersion);

                VersionInfoLog versionInfoLog = new VersionInfoLog();
                versionInfoLog.setTagNo(newVersion.getTagNo());
                versionInfoLog.setTAG_VER(newVersion.getTAG_VER());
                versionInfoLog.setCREATE_ID(newVersion.getCREATE_ID());
                versionInfoLog.setCreateDt(newVersion.getCREATE_DT());
                versionInfoLog.setUPDATE_ID(newVersion.getUPDATE_ID());
                versionInfoLog.setUPDATE_DT(new Date());
                versionInfoLog.setLogType("UPDATE");
            }
            return ResponseEntity.ok(setting);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    public ResponseEntity<?> getProdAsList(String tagNo, String filter) {
        try {
            List<Prod_As> all = prodAsRepo.findAll();
            List<Common_Info> commons = commonRepo.findAll();
            List<Map<String, Object>> result = new ArrayList<>();
            for (Prod_As as : all) {
                if (tagNo.equals(as.getTagNo())) {
                    Map<String, Object> asMap = new HashMap<>();
                    asMap.put("id", as.getProd_as_id());
                    asMap.put("tag_NO", as.getTagNo());
                    asMap.put("as_CNT", as.getAS_CNT());
                    asMap.put("as_DOC", as.getAS_DOC());
                    asMap.put("occr_DT", as.getOCCR_DT());
                    asMap.put("occr_RSN", as.getOCCR_RSN());
                    asMap.put("close_DT", as.getCLOSE_DT());
                    asMap.put("close_RSLT", as.getCLOSE_RSLT());
                    asMap.put("delivery_DT", as.getDELIVERY_DT());
                    asMap.put("create_DT", as.getCREATE_DT());
                    asMap.put("create_ID", as.getCREATE_ID());
                    asMap.put("update_DT", as.getUPDATE_DT());
                    asMap.put("update_ID", as.getUPDATE_ID());
                    Common_Info common = commons.stream()
                        .filter(c -> tagNo.equals(
                            (c.getMacAddr() != null ? c.getMacAddr().replace(":", "") : "") +
                            (c.getFacCd() != null ? c.getFacCd() : "") +
                            (c.getFacNo() != null ? c.getFacNo() : "")
                        ))
                        .findFirst().orElse(null);
                    asMap.put("mac_ADDR", common != null ? common.getMacAddr() : "");
                    result.add(asMap);
                }
            }
            result.sort(Comparator.comparing(m -> (Date) m.get("create_DT")));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    public ResponseEntity<?> createProdAs(String tagNo, Map<String, Object> dto) {
        try {
            Prod_As newAs = new Prod_As();
            newAs.setTagNo(tagNo);
            newAs.setAS_DOC((String) dto.get("asDoc"));
            newAs.setOCCR_DT(parseDate((String) dto.get("occrDt")));
            newAs.setOCCR_RSN((String) dto.get("occrRsn"));
            newAs.setCLOSE_DT(parseDate((String) dto.get("closeDt")));
            newAs.setCLOSE_RSLT((String) dto.get("closeRslt"));
            newAs.setDELIVERY_DT(parseDate((String) dto.get("deliveryDt")));
            newAs.setCREATE_DT(new Date());
            newAs.setCREATE_ID((String) dto.get("updateId"));
            newAs.setUPDATE_DT(new Date());
            newAs.setUPDATE_ID((String) dto.get("updateId"));
            List<Prod_As> existingAs = prodAsRepo.findAll().stream()
                .filter(as -> tagNo.equals(as.getTagNo()))
                .toList();
            newAs.setAS_CNT((long) (existingAs.size() + 1));
            Prod_As saved = prodAsRepo.save(newAs);

            ProdAsLog prodAsLog = new ProdAsLog();
            prodAsLog.setAS_CNT(saved.getAS_CNT());
            prodAsLog.setAS_DOC(saved.getAS_DOC());
            prodAsLog.setOCCR_DT(saved.getOCCR_DT());
            prodAsLog.setOCCR_RSN(saved.getOCCR_RSN());
            prodAsLog.setCLOSE_DT(saved.getCLOSE_DT());
            prodAsLog.setCLOSE_RSLT(saved.getCLOSE_RSLT());
            prodAsLog.setDELIVERY_DT(saved.getDELIVERY_DT());
            prodAsLog.setCreateDt(new Date());
            prodAsLog.setCREATE_ID(saved.getCREATE_ID());
            prodAsLog.setUPDATE_DT(new Date());
            prodAsLog.setUPDATE_ID(saved.getUPDATE_ID());
            prodAsLog.setUPDATE_ID(saved.getUPDATE_ID());
            prodAsLog.setLogType("CREATE");
            prodAsLog.setTagNo(saved.getTagNo());

            prodAsLogRepo.save(prodAsLog);

            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    public ResponseEntity<?> updateProdAs(String tagNo, Map<String, Object> dto) {
        try {
            final Long asId = dto.get("id") != null ? Long.valueOf(dto.get("id").toString()) : null;
            if (asId != null) {
                Optional<Prod_As> existingAs = prodAsRepo.findAll().stream()
                    .filter(as -> asId.equals(as.getProd_as_id()))
                    .findFirst();
                if (existingAs.isPresent()) {
                    Prod_As as = existingAs.get();
                    as.setAS_DOC((String) dto.get("asDoc"));
                    as.setOCCR_DT(parseDate((String) dto.get("occrDt")));
                    as.setOCCR_RSN((String) dto.get("occrRsn"));
                    as.setCLOSE_DT(parseDate((String) dto.get("closeDt")));
                    as.setCLOSE_RSLT((String) dto.get("closeRslt"));
                    as.setDELIVERY_DT(parseDate((String) dto.get("deliveryDt")));
                    as.setUPDATE_DT(new Date());
                    as.setUPDATE_ID((String) dto.get("updateId"));
                    Prod_As saved = prodAsRepo.save(as);

                    ProdAsLog prodAsLog = new ProdAsLog();
                    prodAsLog.setAS_CNT(saved.getAS_CNT());
                    prodAsLog.setAS_DOC(saved.getAS_DOC());
                    prodAsLog.setOCCR_DT(saved.getOCCR_DT());
                    prodAsLog.setOCCR_RSN(saved.getOCCR_RSN());
                    prodAsLog.setCLOSE_DT(saved.getCLOSE_DT());
                    prodAsLog.setCLOSE_RSLT(saved.getCLOSE_RSLT());
                    prodAsLog.setDELIVERY_DT(saved.getDELIVERY_DT());
                    prodAsLog.setCreateDt(new Date());
                    prodAsLog.setCREATE_ID(saved.getCREATE_ID());
                    prodAsLog.setUPDATE_DT(new Date());
                    prodAsLog.setUPDATE_ID(saved.getUPDATE_ID());
                    prodAsLog.setUPDATE_ID(saved.getUPDATE_ID());
                    prodAsLog.setLogType("UPDATE");
                    prodAsLog.setTagNo(saved.getTagNo());

                    prodAsLogRepo.save(prodAsLog);

                    return ResponseEntity.ok(saved);
                }
            }
            return ResponseEntity.ok(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    public ResponseEntity<?> deleteProdAs(Long asId) {
        try {
            Optional<Prod_As> existingAs = prodAsRepo.findAll().stream()
                    .filter(as -> asId.equals(as.getProd_as_id()))
                    .findFirst();
            if (existingAs.isPresent()) {
                ProdAsLog log = new ProdAsLog();
                log.setTagNo(existingAs.get().getTagNo());
                log.setLogType("DELETE");
                log.setAS_CNT(existingAs.get().getAS_CNT());
                log.setAS_DOC(existingAs.get().getAS_DOC());
                log.setOCCR_DT(existingAs.get().getOCCR_DT());
                log.setOCCR_RSN(existingAs.get().getOCCR_RSN());
                log.setCLOSE_DT(existingAs.get().getCLOSE_DT());
                log.setCLOSE_RSLT(existingAs.get().getCLOSE_RSLT());
                log.setDELIVERY_DT(existingAs.get().getDELIVERY_DT());
                log.setUPDATE_DT(new Date());
                log.setUPDATE_ID(existingAs.get().getUPDATE_ID());

                prodAsLogRepo.save(log);

                prodAsRepo.delete(existingAs.get());
                return ResponseEntity.ok("삭제 완료");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 AS 기록 없음");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    private Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return null;
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateStr);
        } catch (ParseException e) {
            return new Date();
        }
    }

    public ResponseEntity<?> getVersionHistory(String tagNo) {
        try {
            List<Version_Info> versions = versionRepo.findAll().stream()
                .filter(v -> tagNo.equals(v.getTagNo()))
                .toList();
            List<Setting_Info> settings = settingRepo.findAll().stream()
                .filter(s -> tagNo.equals(s.getTagNo()))
                .toList();
            List<Map<String, Object>> result = new ArrayList<>();
            for (Version_Info version : versions) {
                Map<String, Object> versionData = new HashMap<>();
                versionData.put("seq", version.getVersion_info_seq());
                versionData.put("TAG_NO", version.getTagNo());
                versionData.put("TAG_VER", version.getTAG_VER());
                versionData.put("CREATE_DT", version.getCREATE_DT());
                versionData.put("CREATE_ID", version.getCREATE_ID());
                versionData.put("UPDATE_DT", version.getUPDATE_DT());
                versionData.put("UPDATE_ID", version.getUPDATE_ID());
                Setting_Info setting = settings.stream()
                    .filter(s -> s.getTagNo().equals(tagNo))
                    .findFirst().orElse(null);
                if (setting != null) {
                    versionData.put("HW_VER", setting.getHW_VER());
                    versionData.put("FW_VER", setting.getFW_VER());
                    versionData.put("LED_SEC", setting.getLED_SEC());
                    versionData.put("RI_MS", setting.getRI_MS());
                    versionData.put("TX_POWER", setting.getTX_POWER());
                    versionData.put("RANDOM_DV", setting.getRANDOM_DV());
                    versionData.put("RF_PROFILE", setting.getRF_PROFILE());
                    versionData.put("CHANNEL", setting.getCHANNEL());
                    versionData.put("SLEEP_MODE", setting.getSLEEP_MODE());
                    versionData.put("SLEEP_TH_HOLD", setting.getSLEEP_TH_HOLD());
                    versionData.put("SLEEP_INTERVAL", setting.getSLEEP_INTERVAL());
                    versionData.put("SLEEP_PERIOD", setting.getSLEEP_PERIOD());
                    versionData.put("BC_VER", setting.getBC_VER());
                    versionData.put("BC_PERIOD", setting.getBC_PERIOD());
                    versionData.put("BC_SLEEP", setting.getBC_SLEEP());
                    versionData.put("DEVICE_IP", setting.getDEVICE_IP());
                    versionData.put("SERVER_IP", setting.getSERVER_IP());
                    versionData.put("GATEWAY", setting.getGATEWAY());
                    versionData.put("SUB_MASK", setting.getSUB_MASK());
                    versionData.put("TDMA", setting.getTDMA());
                    versionData.put("PORT", setting.getPORT());
                }
                result.add(versionData);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    public ResponseEntity<?> getCommonHistory(String tagNo) {
        try {
            List<Common_Info> all = commonRepo.findAll();
            List<Map<String, Object>> result = new ArrayList<>();
            for (Common_Info c : all) {
                String composedTagNo = (c.getMacAddr() != null ? c.getMacAddr().replace(":", "") : "") +
                                       (c.getFacCd() != null ? c.getFacCd() : "") +
                                       (c.getFacNo() != null ? c.getFacNo() : "");
                if (tagNo.equals(composedTagNo)) {
                    Map<String, Object> commonMap = new HashMap<>();
                    commonMap.put("mac_ADDR", c.getMacAddr());
                    commonMap.put("fac_CD", c.getFacCd());
                    commonMap.put("fac_NO", c.getFacNo());
                    commonMap.put("create_DT", c.getCREATE_DT());
                    commonMap.put("create_ID", c.getCREATE_ID());
                    commonMap.put("update_DT", c.getUPDATE_DT());
                    commonMap.put("update_ID", c.getUPDATE_ID());
                    result.add(commonMap);
                }
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    public ResponseEntity<?> createCommonInfo(Map<String, Object> dto) {
        try {
            Common_Info newCommon = new Common_Info();
            newCommon.setMacAddr((String) dto.get("macAddr"));
            newCommon.setFacCd((String) dto.get("facCd"));
            newCommon.setFacNo((String) dto.get("facNo"));
            newCommon.setCREATE_DT(new Date());
            newCommon.setCREATE_ID((String) dto.get("updateId"));
            newCommon.setUPDATE_DT(new Date());
            newCommon.setUPDATE_ID((String) dto.get("updateId"));
            Common_Info saved = commonRepo.save(newCommon);

            CommonInfoLog commonInfoLog = new CommonInfoLog();
            commonInfoLog.setLogType("CREATE");
            commonInfoLog.setMacAddr(saved.getMacAddr());
            commonInfoLog.setFacCd(saved.getFacCd());
            commonInfoLog.setFacNo(saved.getFacNo());
            commonInfoLog.setCreateDt(saved.getCREATE_DT());
            commonInfoLog.setCREATE_ID(saved.getCREATE_ID());
            commonInfoLog.setUPDATE_DT(saved.getUPDATE_DT());
            commonInfoLog.setUPDATE_ID(saved.getUPDATE_ID());
            commonLogRepo.save(commonInfoLog);

            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    public ResponseEntity<?> updateCommonInfo(String tagNo, Map<String, Object> dto) {
        try {
            List<Common_Info> all = commonRepo.findAll();
            Common_Info existing = null;
            for (Common_Info c : all) {
                String composedTagNo = (c.getMacAddr() != null ? c.getMacAddr().replace(":", "") : "") +
                                       (c.getFacCd() != null ? c.getFacCd() : "") +
                                       (c.getFacNo() != null ? c.getFacNo() : "");
                if (tagNo.equals(composedTagNo)) {
                    existing = c;
                    break;
                }
            }
            if (existing != null) {
                existing.setMacAddr((String) dto.get("macAddr"));
                existing.setFacCd((String) dto.get("facCd"));
                existing.setFacNo((String) dto.get("facNo"));
                existing.setUPDATE_DT(new Date());
                existing.setUPDATE_ID((String) dto.get("updateId"));
                Common_Info saved = commonRepo.save(existing);

                CommonInfoLog commonInfoLog = new CommonInfoLog();
                commonInfoLog.setLogType("UPDATE");
                commonInfoLog.setMacAddr(saved.getMacAddr());
                commonInfoLog.setFacCd(saved.getFacCd());
                commonInfoLog.setFacNo(saved.getFacNo());
                commonInfoLog.setCreateDt(saved.getCREATE_DT());
                commonInfoLog.setCREATE_ID(saved.getCREATE_ID());
                commonInfoLog.setUPDATE_DT(saved.getUPDATE_DT());
                commonInfoLog.setUPDATE_ID(saved.getUPDATE_ID());
                commonLogRepo.save(commonInfoLog);

                return ResponseEntity.ok(saved);
            }
            return ResponseEntity.ok(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    public ResponseEntity<?> createVersionInfo(String tagNo, Map<String, Object> dto) {
        try {
            Version_Info newVersion = new Version_Info();
            newVersion.setTagNo(tagNo);
            newVersion.setTAG_VER((String) dto.get("tagVer"));
            newVersion.setCREATE_DT(new Date());
            newVersion.setCREATE_ID((String) dto.get("updateId"));
            newVersion.setUPDATE_DT(new Date());
            newVersion.setUPDATE_ID((String) dto.get("updateId"));
            versionRepo.save(newVersion);

            Setting_Info setting = settingRepo.findAll().stream()
                .filter(s -> tagNo.equals(s.getTagNo()))
                .findFirst().orElse(new Setting_Info());
            setting.setTagNo(tagNo);
            setting.setHW_VER((String) dto.get("hwVer"));
            setting.setFW_VER((String) dto.get("fwVer"));
            setting.setLED_SEC((String) dto.get("ledSec"));
            setting.setRI_MS((String) dto.get("riMs"));
            setting.setTX_POWER((String) dto.get("txPower"));
            setting.setRANDOM_DV((String) dto.get("randomDv"));
            setting.setRF_PROFILE((String) dto.get("rfProfile"));
            setting.setCHANNEL((String) dto.get("channel"));
            setting.setSLEEP_MODE((String) dto.get("sleepMode"));
            setting.setSLEEP_TH_HOLD((String) dto.get("sleepThHold"));
            setting.setSLEEP_INTERVAL((String) dto.get("sleepInterval"));
            setting.setSLEEP_PERIOD((String) dto.get("sleepPeriod"));
            setting.setBC_VER((String) dto.get("bcVer"));
            setting.setBC_PERIOD((String) dto.get("bcPeriod"));
            setting.setBC_SLEEP((String) dto.get("bcSleep"));
            setting.setDEVICE_IP((String) dto.get("deviceIp"));
            setting.setSERVER_IP((String) dto.get("serverIp"));
            setting.setGATEWAY((String) dto.get("gateway"));
            setting.setSUB_MASK((String) dto.get("subMask"));
            setting.setTDMA((String) dto.get("tdma"));
            setting.setPORT((String) dto.get("port"));
            setting.setCREATE_DT(new Date());
            setting.setCREATE_ID((String) dto.get("updateId"));
            setting.setUPDATE_DT(new Date());
            setting.setUPDATE_ID((String) dto.get("updateId"));
            settingRepo.save(setting);
            Map<String, Object> result = new HashMap<>();
            result.put("TAG_NO", newVersion.getTagNo());
            result.put("TAG_VER", newVersion.getTAG_VER());
            result.put("CREATE_DT", newVersion.getCREATE_DT());
            result.put("CREATE_ID", newVersion.getCREATE_ID());
            result.put("UPDATE_DT", newVersion.getUPDATE_DT());
            result.put("UPDATE_ID", newVersion.getUPDATE_ID());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    public ResponseEntity<?> updateVersionInfo(String tagNo, Map<String, Object> dto) {
        try {
            Setting_Info setting = settingRepo.findAll().stream()
                .filter(s -> tagNo.equals(s.getTagNo()))
                .findFirst().orElse(null);
            if (setting != null) {
                setting.setHW_VER((String) dto.get("hwVer"));
                setting.setFW_VER((String) dto.get("fwVer"));
                setting.setLED_SEC((String) dto.get("ledSec"));
                setting.setRI_MS((String) dto.get("riMs"));
                setting.setTX_POWER((String) dto.get("txPower"));
                setting.setRANDOM_DV((String) dto.get("randomDv"));
                setting.setRF_PROFILE((String) dto.get("rfProfile"));
                setting.setCHANNEL((String) dto.get("channel"));
                setting.setSLEEP_MODE((String) dto.get("sleepMode"));
                setting.setSLEEP_TH_HOLD((String) dto.get("sleepThHold"));
                setting.setSLEEP_INTERVAL((String) dto.get("sleepInterval"));
                setting.setSLEEP_PERIOD((String) dto.get("sleepPeriod"));
                setting.setBC_VER((String) dto.get("bcVer"));
                setting.setBC_PERIOD((String) dto.get("bcPeriod"));
                setting.setBC_SLEEP((String) dto.get("bcSleep"));
                setting.setDEVICE_IP((String) dto.get("deviceIp"));
                setting.setSERVER_IP((String) dto.get("serverIp"));
                setting.setGATEWAY((String) dto.get("gateway"));
                setting.setSUB_MASK((String) dto.get("subMask"));
                setting.setTDMA((String) dto.get("tdma"));
                setting.setPORT((String) dto.get("port"));
                setting.setUPDATE_DT(new Date());
                setting.setUPDATE_ID((String) dto.get("updateId"));
                settingRepo.save(setting);
                Map<String, Object> result = new HashMap<>();
                result.put("TAG_NO", setting.getTagNo());
                result.put("UPDATE_DT", setting.getUPDATE_DT());
                result.put("UPDATE_ID", setting.getUPDATE_ID());
                return ResponseEntity.ok(result);
            }
            return ResponseEntity.ok(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
