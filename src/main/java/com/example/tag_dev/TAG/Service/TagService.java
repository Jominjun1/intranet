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
    private final BasicInfoRepository basicInfoRepository;
    private final CommonInfoRepository commonInfoRepository;
    private final VersionInfoRepository versionInfoRepository;
    private final ProcStepRepository procStepRepository;
    private final SettingInfoRepository settingInfoRepository;
    private final ProdAsRepository prodAsRepository;
    private final ProdAsLogRepository prodAsLogRepository;
    private final ProcStepLogRepository procStepLogRepository;
    private final SettingInfoLogRepository settingInfoLogRepository;
    private final BasicInfoLogRepository basicInfoLogRepository;
    private final VersionInfoLogRepository versionInfoLogRepository;
    private final CommonInfoLogRepository commonInfoLogRepository;

    public TagService(BasicInfoRepository basicInfoRepository, CommonInfoRepository commonInfoRepository, VersionInfoRepository versionInfoRepository, ProcStepRepository procStepRepository, SettingInfoRepository settingInfoRepository, ProdAsRepository prodAsRepository, ProdAsLogRepository prodAsLogRepository, ProcStepLogRepository procStepLogRepository, SettingInfoLogRepository settingInfoLogRepository, BasicInfoLogRepository basicInfoLogRepository, VersionInfoLogRepository versionInfoLogRepository, CommonInfoLogRepository commonInfoLogRepository) {
        this.basicInfoRepository = basicInfoRepository;
        this.commonInfoRepository = commonInfoRepository;
        this.versionInfoRepository = versionInfoRepository;
        this.procStepRepository = procStepRepository;
        this.settingInfoRepository = settingInfoRepository;
        this.prodAsRepository = prodAsRepository;
        this.prodAsLogRepository = prodAsLogRepository;
        this.procStepLogRepository = procStepLogRepository;
        this.settingInfoLogRepository = settingInfoLogRepository;
        this.basicInfoLogRepository = basicInfoLogRepository;
        this.versionInfoLogRepository = versionInfoLogRepository;
        this.commonInfoLogRepository = commonInfoLogRepository;
    }


    // 조회 (필터 - Mac주소 , 시리얼번호 , 공장코드 , 삭제 여부 )
    public ResponseEntity<?> getTagInventoryList(String macAddr, String facCd, String facNo, String delFilter) {
        try {
            // 검색 조건이 없으면 빈 배열 반환
            boolean hasSearchCondition = (macAddr != null && !macAddr.trim().isEmpty()) ||
                    (facCd != null && !facCd.trim().isEmpty()) ||
                    (facNo != null && !facNo.trim().isEmpty());

            if (!hasSearchCondition) {
                return ResponseEntity.ok(new ArrayList<>());
            }
            List<Basic_Info> basics = basicInfoRepository.findAll();
            List<Common_Info> commons = commonInfoRepository.findAll();
            List<Map<String, Object>> result = new ArrayList<>();
            for (Basic_Info basic : basics) {
                Common_Info common = commons.stream()
                        .filter(c -> basic.getOrdNo().equals(
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
                    continue;
                }
                
                // Status 필터링 적용
                String status = basic.getStatus();
                if (delFilter != null && !delFilter.trim().isEmpty()) {
                    if ("active".equals(delFilter) && "Y".equals(status)) {
                        continue; // 삭제된 항목 제외
                    } else if ("deleted".equals(delFilter) && !"Y".equals(status)) {
                        continue; // 삭제된 항목만
                    }
                }
                
                Map<String, Object> row = new HashMap<>();
                row.put("tag_No", basic.getOrdNo());
                row.put("tag_Type", basic.getTagType());
                row.put("erp_Code", basic.getErpCode());
                row.put("Mng_Category", basic.getMngCategory());
                row.put("Lot", basic.getLot());
                row.put("Prod_order", basic.getProd_order());
                row.put("Project_code", basic.getProject_code());
                row.put("Project_manager", basic.getProject_manager());
                row.put("Mac_duple_yn", basic.getMac_duple_yn());
                row.put("Status", status); // Status 추가

                // common 정보가 있으면 추가
                row.put("mac_Addr", common.getMacAddr());
                row.put("fac_Cd", common.getFacCd());
                row.put("fac_No", common.getFacNo());
                Optional<Version_Info> ver = versionInfoRepository.findAll().stream().filter(v -> v.getOrdNo().equals(basic.getOrdNo())).max(Comparator.comparing(Version_Info::getTag_version));
                row.put("tag_Version", ver.map(Version_Info::getTag_version).orElse("1.0"));
                long asCount = prodAsRepository.findAll().stream()
                        .filter(a -> a.getOrdNo().equals(basic.getOrdNo()))
                        .filter(a -> a.getAsCnt() != null && a.getAsCnt() > 0)
                        .count();
                row.put("as_Cnt", asCount);
                result.add(row);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    //
    public ResponseEntity<?> getProcStep(String ordNo) {
        try {
            Proc_Step result = procStepRepository.findAll().stream().filter(p -> ordNo.equals(p.getOrdNo())).findFirst().orElse(null);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    public ResponseEntity<?> getSettingInfo(String ordNo) {
        try {
            Setting_Info result = settingInfoRepository.findAll().stream().filter(s -> ordNo.equals(s.getOrdNo())).findFirst().orElse(null);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    public ResponseEntity<?> getLatestVersionInfo(String ordNo) {
        try {
            Version_Info result = versionInfoRepository.findAll().stream()
                    .filter(v -> ordNo.equals(v.getOrdNo()))
                    .max(Comparator.comparing(Version_Info::getTag_version))
                    .orElse(null);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    public ResponseEntity<?> updateSettingInfo(String ordNo, TagSettingDTO dto) {
        try {
            Setting_Info setting = settingInfoRepository.findAll().stream().filter(s -> ordNo.equals(s.getOrdNo())).findFirst().orElse(null);
            if (setting != null) {
                boolean isUpdated = false;
                if(setting.getHW_version() != null &&  !setting.getHW_version().trim().isEmpty()){
                    setting.setHW_version(dto.getHW_VER());
                    isUpdated = true;
                }
                if(setting.getFW_version() != null && !setting.getFW_version().trim().isEmpty()){
                    setting.setFW_version(dto.getFW_VER());
                    isUpdated = true;
                }
                if(setting.getStatus() != null && !setting.getStatus().trim().isEmpty()) {
                    setting.setStatus(dto.getStatus());
                    isUpdated = true;
                }
                if(isUpdated) {
                    setting.setUPDATE_DT(new Date());
                    setting.setUPDATE_ID(UUID.randomUUID().toString());
                    settingInfoRepository.save(setting);


                    SettingInfoLog settingInfoLog = new SettingInfoLog();
                    settingInfoLog.setOrdNo(ordNo);
                    settingInfoLog.setHW_VER(dto.getHW_VER());
                    settingInfoLog.setFW_VER(dto.getFW_VER());
                    settingInfoLog.setUPDATE_ID(dto.getUPDATE_ID());
                    settingInfoLog.setUPDATE_DT(new Date());
                    settingInfoLog.setUPDATE_ID(dto.getUPDATE_ID());
                    if (Objects.equals(dto.getStatus(), "Y")) {
                        settingInfoLog.setStatus("삭제");
                    } else {
                        settingInfoLog.setStatus("수정");
                    }
                    settingInfoLogRepository.save(settingInfoLog);

                    Optional<Version_Info> verOpt = versionInfoRepository.findAll().stream().filter(v -> v.getOrdNo().equals(ordNo)).max(Comparator.comparing(Version_Info::getTag_version));
                    String newVer = verOpt.map(v -> {
                        try {
                            double vNum = Double.parseDouble(v.getTag_version());
                            return String.format("%.1f", vNum + 0.1);
                        } catch (Exception e) {
                            return "1.1";
                        }
                    }).orElse("1.1");
                    Version_Info newVersion = new Version_Info();
                    if(newVersion.getOrdNo() != null && !newVersion.getOrdNo().trim().isEmpty()){
                        newVersion.setOrdNo(ordNo);
                    }
                    if(newVersion.getTag_version() != null && !newVersion.getTag_version().trim().isEmpty()){
                        newVersion.setTag_version(newVer);
                    }
                    if(newVersion.getCREATE_DT() != null){
                        newVersion.setCREATE_DT(new Date());
                    }
                    if(newVersion.getCREATE_ID() != null){
                        newVersion.setCREATE_ID(dto.getUPDATE_ID());
                    }
                    if (Objects.equals(dto.getStatus(), "Y")) {
                        newVersion.setStatus("Y");
                    }
                    versionInfoRepository.save(newVersion);

                    VersionInfoLog versionInfoLog = new VersionInfoLog();
                    versionInfoLog.setOrdNo(newVersion.getOrdNo());
                    versionInfoLog.setTAG_VER(newVersion.getTag_version());
                    versionInfoLog.setCREATE_ID(newVersion.getCREATE_ID());
                    versionInfoLog.setCreateDt(newVersion.getCREATE_DT());
                    versionInfoLog.setUPDATE_ID(newVersion.getUPDATE_ID());
                    versionInfoLog.setUPDATE_DT(new Date());

                    versionInfoLogRepository.save(versionInfoLog);

                    if (Objects.equals(dto.getStatus(), "Y")) {
                        versionInfoLog.setStatus("삭제");
                    } else {
                        versionInfoLog.setStatus("수정");
                    }
                }
            }
            return ResponseEntity.ok(setting);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    public ResponseEntity<?> getProdAsList(String ordNo, String filter) {
        try {
            List<Prod_As> all = prodAsRepository.findAll();
            List<Common_Info> commons = commonInfoRepository.findAll();
            List<Map<String, Object>> result = new ArrayList<>();
            
            for (Prod_As as : all) {
                if (ordNo.equals(as.getOrdNo())) {
                    // 필터링 로직 적용
                    if ("active".equals(filter) && "Y".equals(as.getStatus())) {
                        continue; // 삭제된 항목 제외
                    } else if ("deleted".equals(filter) && !"Y".equals(as.getStatus())) {
                        continue; // 삭제된 항목만
                    }
                    
                    Map<String, Object> asMap = new HashMap<>();
                    asMap.put("id", as.getProd_as_id());
                    asMap.put("tag_NO", as.getOrdNo());
                    asMap.put("as_Cnt", as.getAsCnt());
                    asMap.put("as_Doc", as.getAsDoc());
                    asMap.put("occr_Dt", as.getOccr_dt());
                    asMap.put("occr_RSN", as.getOcc_rrsn());
                    asMap.put("close_Dt", as.getClose_dt());
                    asMap.put("close_RSLT", as.getClose_rslt());
                    asMap.put("delivery_DT", as.getDelivery_dt());
                    asMap.put("create_DT", as.getCreate_dt());
                    asMap.put("create_ID", as.getCreate_id());
                    asMap.put("update_DT", as.getUpdate_dt());
                    asMap.put("update_ID", as.getUpdate_id());
                    asMap.put("del_YN", as.getStatus());
                    
                    // MAC 주소 정보 추가
                    Common_Info common = commons.stream()
                            .filter(c -> ordNo.equals(
                                    (c.getMacAddr() != null ? c.getMacAddr().replace(":", "") : "") +
                                            (c.getFacCd() != null ? c.getFacCd() : "") +
                                            (c.getFacNo() != null ? c.getFacNo() : "")
                            ))
                            .findFirst().orElse(null);
                    asMap.put("mac_ADDR", common != null ? common.getMacAddr() : "");
                    result.add(asMap);
                }
            }
            
            // 생성일 기준으로 정렬 (최신순)
            result.sort((a, b) -> {
                Date dateA = (Date) a.get("create_DT");
                Date dateB = (Date) b.get("create_DT");
                if (dateA == null && dateB == null) return 0;
                if (dateA == null) return 1;
                if (dateB == null) return -1;
                return dateB.compareTo(dateA);
            });
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    public ResponseEntity<?> createProdAs(String ordNo, Map<String, Object> dto) {
        try {
            Prod_As newAs = new Prod_As();
            newAs.setOrdNo(ordNo);
            newAs.setAsDoc((String) dto.get("as_Doc"));
            newAs.setOccr_dt(parseDate((String) dto.get("occr_Dt")));
            newAs.setOcc_rrsn((String) dto.get("occr_RRsn"));
            newAs.setClose_dt(parseDate((String) dto.get("close_Dt")));
            newAs.setClose_rslt((String) dto.get("close_Rslt"));
            newAs.setDelivery_dt(parseDate((String) dto.get("delivery_Dt")));
            newAs.setCreate_dt(new Date());
            newAs.setCreate_id((String) dto.get("update_Id"));
            newAs.setUpdate_dt(new Date());
            newAs.setUpdate_id((String) dto.get("update_Id"));
            newAs.setStatus("Y");
            List<Prod_As> existingAs = prodAsRepository.findAll().stream()
                    .filter(as -> ordNo.equals(as.getOrdNo()))
                    .toList();
            newAs.setAsCnt((long) (existingAs.size() + 1));
            Prod_As saved = prodAsRepository.save(newAs);

            ProdAsLog prodAsLog = new ProdAsLog();
            prodAsLog.setAS_CNT(saved.getAsCnt());
            prodAsLog.setAS_DOC(saved.getAsDoc());
            prodAsLog.setOCCR_DT(saved.getOccr_dt());
            prodAsLog.setOCCR_RSN(saved.getOcc_rrsn());
            prodAsLog.setCLOSE_DT(saved.getClose_dt());
            prodAsLog.setCLOSE_RSLT(saved.getClose_rslt());
            prodAsLog.setDELIVERY_DT(saved.getDelivery_dt());
            prodAsLog.setCreateDt(new Date());
            prodAsLog.setCREATE_ID(saved.getCreate_id());
            prodAsLog.setUPDATE_DT(new Date());
            prodAsLog.setUPDATE_ID(saved.getUpdate_id());
            prodAsLog.setStatus("생성");
            prodAsLog.setOrdNo(saved.getOrdNo());

            prodAsLogRepository.save(prodAsLog);

            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    public ResponseEntity<?> updateProdAs(String ordNo, Map<String, Object> dto) {
        try {
            final Long asId = dto.get("id") != null ? Long.valueOf(dto.get("id").toString()) : null;
            if (asId != null) {
                Optional<Prod_As> existingAs = prodAsRepository.findAll().stream()
                        .filter(as -> asId.equals(as.getProd_as_id()))
                        .findFirst();
                if (existingAs.isPresent()) {
                    Prod_As as = existingAs.get();
                    as.setAsDoc((String) dto.get("as_Doc"));
                    as.setOccr_dt(parseDate((String) dto.get("occr_Dt")));
                    as.setOcc_rrsn((String) dto.get("occr_RRsn"));
                    as.setClose_dt(parseDate((String) dto.get("close_Dt")));
                    as.setClose_rslt((String) dto.get("close_Rslt"));
                    as.setDelivery_dt(parseDate((String) dto.get("delivery_Dt")));
                    as.setUpdate_dt(new Date());
                    as.setUpdate_id((String) dto.get("update_Id"));
                    Prod_As saved = prodAsRepository.save(as);

                    ProdAsLog prodAsLog = new ProdAsLog();
                    prodAsLog.setAS_CNT(saved.getAsCnt());
                    prodAsLog.setAS_DOC(saved.getAsDoc());
                    prodAsLog.setOCCR_DT(saved.getOccr_dt());
                    prodAsLog.setOCCR_RSN(saved.getOcc_rrsn());
                    prodAsLog.setCLOSE_DT(saved.getClose_dt());
                    prodAsLog.setCLOSE_RSLT(saved.getClose_rslt());
                    prodAsLog.setDELIVERY_DT(saved.getDelivery_dt());
                    prodAsLog.setCreateDt(new Date());
                    prodAsLog.setCREATE_ID(saved.getCreate_id());
                    prodAsLog.setUPDATE_DT(new Date());
                    prodAsLog.setUPDATE_ID(saved.getUpdate_id());
                    if(Objects.equals(saved.getStatus(), "N")){
                        prodAsLog.setStatus("삭제");
                    }else {
                        prodAsLog.setStatus("수정");
                    }
                    prodAsLog.setOrdNo(saved.getOrdNo());

                    prodAsLogRepository.save(prodAsLog);

                    return ResponseEntity.ok(saved);
                }
            }
            return ResponseEntity.ok(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    public ResponseEntity<?> getVersionHistory(String ordNo) {
        try {
            List<Version_Info> versions = versionInfoRepository.findAll().stream()
                    .filter(v -> ordNo.equals(v.getOrdNo()))
                    .toList();
            List<Setting_Info> settings = settingInfoRepository.findAll().stream()
                    .filter(s -> ordNo.equals(s.getOrdNo()))
                    .toList();
            List<Map<String, Object>> result = new ArrayList<>();
            for (Version_Info version : versions) {
                Map<String, Object> versionData = new HashMap<>();
                versionData.put("seq", version.getVersion_info_seq());
                versionData.put("tag_No", version.getOrdNo());
                versionData.put("tag_version", version.getTag_version());
                versionData.put("create_Dt", version.getCREATE_DT());
                versionData.put("create_Id", version.getCREATE_ID());
                versionData.put("update_Dt", version.getUPDATE_DT());
                versionData.put("update_Id", version.getUPDATE_ID());
                Setting_Info setting = settings.stream()
                        .filter(s -> s.getOrdNo().equals(ordNo))
                        .findFirst().orElse(null);
                if (setting != null) {
                    versionData.put("HW_VERSION", setting.getHW_version());
                    versionData.put("FW_VERSION", setting.getFW_version());
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

    public ResponseEntity<?> getCommonHistory(String ordNo) {
        try {
            List<Common_Info> all = commonInfoRepository.findAll();
            List<Map<String, Object>> result = new ArrayList<>();
            for (Common_Info c : all) {
                String composedTagNo = (c.getMacAddr() != null ? c.getMacAddr().replace(":", "") : "") +
                        (c.getFacCd() != null ? c.getFacCd() : "") +
                        (c.getFacNo() != null ? c.getFacNo() : "");
                if (ordNo.equals(composedTagNo)) {
                    Map<String, Object> commonMap = new HashMap<>();
                    commonMap.put("mac_ADDR", c.getMacAddr());
                    commonMap.put("fac_CD", c.getFacCd());
                    commonMap.put("fac_NO", c.getFacNo());
                    commonMap.put("create_DT", c.getCreate_dt());
                    commonMap.put("create_ID", c.getCreate_id());
                    commonMap.put("update_DT", c.getUpdate_dt());
                    commonMap.put("update_ID", c.getUpdate_id());
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
            newCommon.setCreate_dt(new Date());
            newCommon.setCreate_id((String) dto.get("updateId"));
            newCommon.setUpdate_dt(new Date());
            newCommon.setUpdate_id((String) dto.get("updateId"));
            newCommon.setStatus("Y");
            Common_Info saved = commonInfoRepository.save(newCommon);

            CommonInfoLog commonInfoLog = new CommonInfoLog();
            commonInfoLog.setStatus("생성");
            commonInfoLog.setMacAddr(saved.getMacAddr());
            commonInfoLog.setFacCd(saved.getFacCd());
            commonInfoLog.setFacNo(saved.getFacNo());
            commonInfoLog.setCreateDt(saved.getCreate_dt());
            commonInfoLog.setCREATE_ID(saved.getCreate_id());
            commonInfoLog.setUPDATE_DT(saved.getUpdate_dt());
            commonInfoLog.setUPDATE_ID(saved.getUpdate_id());
            commonInfoLogRepository.save(commonInfoLog);

            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    public ResponseEntity<?> updateCommonInfo(String ordNo, Map<String, Object> dto) {
        try {
            List<Common_Info> all = commonInfoRepository.findAll();
            Common_Info existing = null;
            for (Common_Info c : all) {
                String composedTagNo = (c.getMacAddr() != null ? c.getMacAddr().replace(":", "") : "") +
                        (c.getFacCd() != null ? c.getFacCd() : "") +
                        (c.getFacNo() != null ? c.getFacNo() : "");
                if (ordNo.equals(composedTagNo)) {
                    existing = c;
                    break;
                }
            }
            if (existing != null) {
                existing.setMacAddr((String) dto.get("mac_Addr"));
                existing.setFacCd((String) dto.get("fac_Cd"));
                existing.setFacNo((String) dto.get("fac_No"));
                existing.setUpdate_dt(new Date());
                existing.setUpdate_id((String) dto.get("update_Id"));
                Common_Info saved = commonInfoRepository.save(existing);

                CommonInfoLog commonInfoLog = new CommonInfoLog();
                commonInfoLog.setStatus("수정");
                commonInfoLog.setMacAddr(saved.getMacAddr());
                commonInfoLog.setFacCd(saved.getFacCd());
                commonInfoLog.setFacNo(saved.getFacNo());
                commonInfoLog.setCreateDt(saved.getCreate_dt());
                commonInfoLog.setCREATE_ID(saved.getCreate_id());
                commonInfoLog.setUPDATE_DT(saved.getUpdate_dt());
                commonInfoLog.setUPDATE_ID(saved.getUpdate_id());
                commonInfoLogRepository.save(commonInfoLog);

                return ResponseEntity.ok(saved);
            }
            return ResponseEntity.ok(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    public ResponseEntity<?> createVersionInfo(String ordNo, Map<String, Object> dto) {
        try {
            Version_Info newVersion = new Version_Info();
            newVersion.setOrdNo(ordNo);
            newVersion.setTag_version((String) dto.get("tag_Version"));
            newVersion.setCREATE_DT(new Date());
            newVersion.setCREATE_ID((String) dto.get("update_Id"));
            newVersion.setUPDATE_DT(new Date());
            newVersion.setUPDATE_ID((String) dto.get("update_Id"));
            newVersion.setStatus("Y");
            versionInfoRepository.save(newVersion);

            Setting_Info setting = settingInfoRepository.findAll().stream()
                    .filter(s -> ordNo.equals(s.getOrdNo()))
                    .findFirst().orElse(new Setting_Info());
            setting.setOrdNo(ordNo);
            setting.setHW_version((String) dto.get("HW_VERSION"));
            setting.setFW_version((String) dto.get("FW_VERSION"));
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
            setting.setStatus("생성");

            settingInfoRepository.save(setting);
            Map<String, Object> result = new HashMap<>();
            result.put("ORD_NO", newVersion.getOrdNo());
            result.put("TAG_VER", newVersion.getTag_version());
            result.put("CREATE_DT", newVersion.getCREATE_DT());
            result.put("CREATE_ID", newVersion.getCREATE_ID());
            result.put("UPDATE_DT", newVersion.getUPDATE_DT());
            result.put("UPDATE_ID", newVersion.getUPDATE_ID());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    public ResponseEntity<?> updateVersionInfo(String ordNo, Map<String, Object> dto) {
        try {
            Optional<Version_Info> existingVersion = versionInfoRepository.findAll().stream()
                    .filter(v -> ordNo.equals(v.getOrdNo()))
                    .max(Comparator.comparing(Version_Info::getTag_version));
            if (existingVersion.isPresent()) {
                Version_Info version = existingVersion.get();
                version.setTag_version((String) dto.get("tag_Version"));
                version.setUPDATE_DT(new Date());
                version.setUPDATE_ID((String) dto.get("update_Id"));
                versionInfoRepository.save(version);

                VersionInfoLog versionInfoLog = new VersionInfoLog();
                versionInfoLog.setOrdNo(version.getOrdNo());
                versionInfoLog.setTAG_VER(version.getTag_version());
                versionInfoLog.setCREATE_ID(version.getCREATE_ID());
                versionInfoLog.setCreateDt(version.getCREATE_DT());
                versionInfoLog.setUPDATE_ID(version.getUPDATE_ID());
                versionInfoLog.setUPDATE_DT(new Date());
                versionInfoLog.setStatus("수정");
                versionInfoLogRepository.save(versionInfoLog);

                return ResponseEntity.ok(version);
            }
            return ResponseEntity.ok(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    public ResponseEntity<?> getTagNumbers(String query) {
        try {
            List<Basic_Info> basics = basicInfoRepository.findAll();
            List<String> tagNumbers = new ArrayList<>();
            
            for (Basic_Info basic : basics) {
                String ordNo = basic.getOrdNo();
                if (query == null || query.trim().isEmpty() || 
                    ordNo.toLowerCase().contains(query.toLowerCase())) {
                    tagNumbers.add(ordNo);
                }
            }
            if (tagNumbers.size() > 20) {
                tagNumbers = tagNumbers.subList(0, 20);
            }
            
            return ResponseEntity.ok(tagNumbers);
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
}