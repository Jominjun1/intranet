package com.example.tag_dev.TAG.Service;

import com.example.tag_dev.Config.JwtTokenProvider;
import com.example.tag_dev.LOG.Model.CommonInfoLog;
import com.example.tag_dev.LOG.Model.ProdAsLog;
import com.example.tag_dev.LOG.Model.SettingInfoLog;
import com.example.tag_dev.LOG.Model.VersionInfoLog;
import com.example.tag_dev.LOG.Repository.*;
import com.example.tag_dev.TAG.DTO.TagSettingDTO;
import com.example.tag_dev.TAG.Model.*;
import com.example.tag_dev.TAG.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.example.tag_dev.USER.Service.UserService.getStrings;

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
    private final JwtTokenProvider jwtTokenProvider;

    public TagService(BasicInfoRepository basicInfoRepository, CommonInfoRepository commonInfoRepository, VersionInfoRepository versionInfoRepository, ProcStepRepository procStepRepository, SettingInfoRepository settingInfoRepository, ProdAsRepository prodAsRepository, ProdAsLogRepository prodAsLogRepository, ProcStepLogRepository procStepLogRepository, SettingInfoLogRepository settingInfoLogRepository, BasicInfoLogRepository basicInfoLogRepository, VersionInfoLogRepository versionInfoLogRepository, CommonInfoLogRepository commonInfoLogRepository, JwtTokenProvider jwtTokenProvider) {
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
        this.jwtTokenProvider = jwtTokenProvider;
    }


    // 조회 (필터 - Mac주소 , 시리얼번호 , 공장코드 , 삭제 여부 )
    public ResponseEntity<?> getTagInventoryList(String macAddr, String facCd, String facNo, String delFilter) {
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
            row.put("ordNo", basic.getOrdNo()); // 호환성을 위해 ordNo도 추가
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
            Optional<Version_Info> ver = versionInfoRepository.findAll().stream()
                    .filter(v -> v.getOrdNo() != null && v.getOrdNo().equals(basic.getOrdNo()))
                    .max(Comparator.comparing(Version_Info::getTag_version));
            row.put("tag_Version", ver.map(Version_Info::getTag_version).orElse("1.0"));
            long asCount = prodAsRepository.findAll().stream()
                    .filter(a -> a.getOrdNo() != null && a.getOrdNo().equals(basic.getOrdNo()))
                    .filter(a -> a.getAsCnt() != null && a.getAsCnt() > 0)
                    .count();
            row.put("as_Cnt", asCount);
            result.add(row);
        }
        return ResponseEntity.ok(result);
    }

    //
    public ResponseEntity<?> getProcStep(String ordNo) {
        Proc_Step result = procStepRepository.findAll().stream().filter(p -> ordNo.equals(p.getOrdNo())).findFirst().orElse(null);
        return ResponseEntity.ok(result);
    }

    public ResponseEntity<?> getSettingInfo(String ordNo) {
        Setting_Info result = settingInfoRepository.findAll().stream().filter(s -> ordNo.equals(s.getOrdNo())).findFirst().orElse(null);
        return ResponseEntity.ok(result);
    }

    public ResponseEntity<?> getLatestVersionInfo(String ordNo) {
        Version_Info result = versionInfoRepository.findAll().stream()
                .filter(v -> ordNo.equals(v.getOrdNo()))
                .max(Comparator.comparing(Version_Info::getTag_version))
                .orElse(null);
        return ResponseEntity.ok(result);
    }

    public ResponseEntity<?> updateSettingInfo(String ordNo, TagSettingDTO dto, String token) {
        // 토큰에서 사용자 ID 추출
        if (jwtTokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
        }
        Long userId = jwtTokenProvider.extractUserId(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자 ID를 추출할 수 없습니다.");
        }

        Optional<Setting_Info> settingOpt = settingInfoRepository.findByOrdNo(ordNo);
        if (settingOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 태그의 세팅정보를 찾을 수 없습니다.");
        }

        Setting_Info setting = settingOpt.get();

        // DTO의 값들을 엔티티에 수동으로 매핑
        updateSettingFromDTO(setting, dto);

        // 수정 정보 설정
        setting.setUPDATE_DT(new Date());
        setting.setUPDATE_ID(userId.toString()); // 토큰에서 추출한 실제 사용자 ID 사용

        settingInfoRepository.save(setting);

        // 세팅정보 로그 생성 (토큰에서 추출한 사용자 ID 전달)
        createSettingInfoLog(ordNo, dto, userId);

        // 버전 정보 업데이트 (토큰에서 추출한 사용자 ID 전달)
        updateVersionInfo(ordNo, dto, userId);

        return ResponseEntity.ok(setting);
    }

    // DTO의 값들을 Setting_Info 엔티티에 수동으로 매핑하는 헬퍼 메서드
    private void updateSettingFromDTO(Setting_Info setting, TagSettingDTO dto) {
        // null이 아니고 빈 문자열이 아닌 경우에만 설정
        if (dto.getHW_VER() != null && !dto.getHW_VER().trim().isEmpty()) {
            setting.setHW_version(dto.getHW_VER());
        }
        if (dto.getFW_VER() != null && !dto.getFW_VER().trim().isEmpty()) {
            setting.setFW_version(dto.getFW_VER());
        }
        if (dto.getLED_SEC() != null && !dto.getLED_SEC().trim().isEmpty()) {
            setting.setLED_SEC(dto.getLED_SEC());
        }
        if (dto.getRI_MS() != null && !dto.getRI_MS().trim().isEmpty()) {
            setting.setRI_MS(dto.getRI_MS());
        }
        if (dto.getTX_POWER() != null && !dto.getTX_POWER().trim().isEmpty()) {
            setting.setTX_POWER(dto.getTX_POWER());
        }
        if (dto.getRANDOM_DV() != null && !dto.getRANDOM_DV().trim().isEmpty()) {
            setting.setRANDOM_DV(dto.getRANDOM_DV());
        }
        if (dto.getRF_PROFILE() != null && !dto.getRF_PROFILE().trim().isEmpty()) {
            setting.setRF_PROFILE(dto.getRF_PROFILE());
        }
        if (dto.getCHANNEL() != null && !dto.getCHANNEL().trim().isEmpty()) {
            setting.setCHANNEL(dto.getCHANNEL());
        }
        if (dto.getSLEEP_MODE() != null && !dto.getSLEEP_MODE().trim().isEmpty()) {
            setting.setSLEEP_MODE(dto.getSLEEP_MODE());
        }
        if (dto.getSLEEP_TH_HOLD() != null && !dto.getSLEEP_TH_HOLD().trim().isEmpty()) {
            setting.setSLEEP_TH_HOLD(dto.getSLEEP_TH_HOLD());
        }
        if (dto.getSLEEP_INTERVAL() != null && !dto.getSLEEP_INTERVAL().trim().isEmpty()) {
            setting.setSLEEP_INTERVAL(dto.getSLEEP_INTERVAL());
        }
        if (dto.getSLEEP_PERIOD() != null && !dto.getSLEEP_PERIOD().trim().isEmpty()) {
            setting.setSLEEP_PERIOD(dto.getSLEEP_PERIOD());
        }
        if (dto.getBC_VER() != null && !dto.getBC_VER().trim().isEmpty()) {
            setting.setBC_VER(dto.getBC_VER());
        }
        if (dto.getBC_PERIOD() != null && !dto.getBC_PERIOD().trim().isEmpty()) {
            setting.setBC_PERIOD(dto.getBC_PERIOD());
        }
        if (dto.getBC_SLEEP() != null && !dto.getBC_SLEEP().trim().isEmpty()) {
            setting.setBC_SLEEP(dto.getBC_SLEEP());
        }
        if (dto.getDEVICE_IP() != null && !dto.getDEVICE_IP().trim().isEmpty()) {
            setting.setDEVICE_IP(dto.getDEVICE_IP());
        }
        if (dto.getSERVER_IP() != null && !dto.getSERVER_IP().trim().isEmpty()) {
            setting.setSERVER_IP(dto.getSERVER_IP());
        }
        if (dto.getGATEWAY() != null && !dto.getGATEWAY().trim().isEmpty()) {
            setting.setGATEWAY(dto.getGATEWAY());
        }
        if (dto.getSUB_MASK() != null && !dto.getSUB_MASK().trim().isEmpty()) {
            setting.setSUB_MASK(dto.getSUB_MASK());
        }
        if (dto.getTDMA() != null && !dto.getTDMA().trim().isEmpty()) {
            setting.setTDMA(dto.getTDMA());
        }
        if (dto.getPORT() != null && !dto.getPORT().trim().isEmpty()) {
            setting.setPORT(dto.getPORT());
        }
        if (dto.getStatus() != null && !dto.getStatus().trim().isEmpty()) {
            setting.setStatus(dto.getStatus());
        }
    }

    // 세팅정보 로그 생성 헬퍼 메서드
    private void createSettingInfoLog(String ordNo, TagSettingDTO dto, Long userId) {
        SettingInfoLog settingInfoLog = new SettingInfoLog();
        settingInfoLog.setOrdNo(ordNo);
        settingInfoLog.setHW_VER(dto.getHW_VER());
        settingInfoLog.setFW_VER(dto.getFW_VER());
        settingInfoLog.setLED_SEC(dto.getLED_SEC());
        settingInfoLog.setRI_MS(dto.getRI_MS());
        settingInfoLog.setTX_POWER(dto.getTX_POWER());
        settingInfoLog.setRANDOM_DV(dto.getRANDOM_DV());
        settingInfoLog.setRF_PROFILE(dto.getRF_PROFILE());
        settingInfoLog.setCHANNEL(dto.getCHANNEL());
        settingInfoLog.setSLEEP_MODE(dto.getSLEEP_MODE());
        settingInfoLog.setSLEEP_TH_HOLD(dto.getSLEEP_TH_HOLD());
        settingInfoLog.setSLEEP_INTERVAL(dto.getSLEEP_INTERVAL());
        settingInfoLog.setSLEEP_PERIOD(dto.getSLEEP_PERIOD());
        settingInfoLog.setBC_VER(dto.getBC_VER());
        settingInfoLog.setBC_PERIOD(dto.getBC_PERIOD());
        settingInfoLog.setBC_SLEEP(dto.getBC_SLEEP());
        settingInfoLog.setDEVICE_IP(dto.getDEVICE_IP());
        settingInfoLog.setSERVER_IP(dto.getSERVER_IP());
        settingInfoLog.setGATEWAY(dto.getGATEWAY());
        settingInfoLog.setSUB_MASK(dto.getSUB_MASK());
        settingInfoLog.setTDMA(dto.getTDMA());
        settingInfoLog.setPORT(dto.getPORT());
        settingInfoLog.setUPDATE_ID(userId.toString());
        settingInfoLog.setUPDATE_DT(new Date());

        if (Objects.equals(dto.getStatus(), "Y")) {
            settingInfoLog.setStatus("삭제");
        } else {
            settingInfoLog.setStatus("수정");
        }

        settingInfoLogRepository.save(settingInfoLog);
    }

    // 버전 정보 업데이트 헬퍼 메서드
    private void updateVersionInfo(String ordNo, TagSettingDTO dto, Long userId) {
        Optional<Version_Info> verOpt = versionInfoRepository.findAll().stream()
                .filter(v -> v.getOrdNo() != null && v.getOrdNo().equals(ordNo))
                .max(Comparator.comparing(Version_Info::getTag_version));

        String newVer = verOpt.map(v -> {
            try {
                double vNum = Double.parseDouble(v.getTag_version());
                return String.format("%.1f", vNum + 0.1);
            } catch (Exception e) {
                return "1.1";
            }
        }).orElse("1.1");

        Version_Info newVersion = new Version_Info();
        newVersion.setOrdNo(ordNo);
        newVersion.setTag_version(newVer);
        newVersion.setCREATE_DT(new Date());
        newVersion.setCREATE_ID(userId.toString());

        if (Objects.equals(dto.getStatus(), "Y")) {
            newVersion.setStatus("Y");
        }

        versionInfoRepository.save(newVersion);

        // 버전 정보 로그 생성
        createVersionInfoLog(newVersion, userId);
    }

    // 버전 정보 로그 생성 헬퍼 메서드
    private void createVersionInfoLog(Version_Info newVersion, Long userId) {
        VersionInfoLog versionInfoLog = new VersionInfoLog();
        versionInfoLog.setOrdNo(newVersion.getOrdNo());
        versionInfoLog.setTAG_VER(newVersion.getTag_version());
        versionInfoLog.setCREATE_ID(newVersion.getCREATE_ID());
        versionInfoLog.setCreateDt(newVersion.getCREATE_DT());
        versionInfoLog.setUPDATE_ID(userId.toString()); // 토큰에서 추출한 사용자 ID 설정
        versionInfoLog.setUPDATE_DT(new Date());

        if (Objects.equals(newVersion.getStatus(), "Y")) {
            versionInfoLog.setStatus("삭제");
        } else {
            versionInfoLog.setStatus("수정");
        }

        versionInfoLogRepository.save(versionInfoLog);
    }

    public ResponseEntity<?> getProdAsList(String ordNo, String filter) {
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
    }

    public ResponseEntity<?> createProdAs(String ordNo, Map<String, Object> dto) {
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
    }

    public ResponseEntity<?> updateProdAs(String ordNo, Map<String, Object> dto) {
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
                if (Objects.equals(saved.getStatus(), "N")) {
                    prodAsLog.setStatus("삭제");
                } else {
                    prodAsLog.setStatus("수정");
                }
                prodAsLog.setOrdNo(saved.getOrdNo());

                prodAsLogRepository.save(prodAsLog);

                return ResponseEntity.ok(saved);
            }
        }
        return ResponseEntity.ok(null);
    }

    public ResponseEntity<?> getVersionHistory(String ordNo) {
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
    }

    public ResponseEntity<?> getCommonHistory(String ordNo) {
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
    }

    public ResponseEntity<?> createCommonInfo(Map<String, Object> dto) {
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
    }

    public ResponseEntity<?> updateCommonInfo(String ordNo, Map<String, Object> dto) {
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

        if (existing == null) {
            return ResponseEntity.ok(null);
        }

        // Map의 값들을 Common_Info 엔티티에 동적으로 설정
        updateEntityFromMap(existing, dto);

        // 업데이트 정보 설정
        existing.setUpdate_dt(new Date());
        existing.setUpdate_id((String) dto.get("update_Id"));

        Common_Info saved = commonInfoRepository.save(existing);

        // 공통정보 로그 생성
        createCommonInfoLog(saved);

        return ResponseEntity.ok(saved);
    }

    // Map의 값들을 엔티티에 동적으로 설정하는 헬퍼 메서드
    private void updateEntityFromMap(Object entity, Map<String, Object> dto) {
        try {
            Class<?> entityClass = entity.getClass();
            for (Map.Entry<String, Object> entry : dto.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                // null이거나 빈 문자열인 경우 건너뛰기
                if (value == null || (value instanceof String && ((String) value).trim().isEmpty())) {
                    continue;
                }

                // Map의 키를 엔티티 필드명으로 변환 (예: mac_Addr -> macAddr)
                String fieldName = convertToFieldName(key);

                try {
                    Field field = entityClass.getDeclaredField(fieldName);
                    field.setAccessible(true);

                    // 타입 변환 및 설정
                    Object convertedValue = convertValue(value, field.getType());
                    field.set(entity, convertedValue);
                } catch (NoSuchFieldException e) {
                    // 필드가 존재하지 않는 경우 무시
                    continue;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("엔티티 업데이트 중 오류 발생", e);
        }
    }

    // Map 키를 엔티티 필드명으로 변환하는 헬퍼 메서드
    private String convertToFieldName(String mapKey) {
        // 예: mac_Addr -> macAddr, fac_Cd -> facCd
        if (mapKey.contains("_")) {
            String[] parts = mapKey.split("_");
            StringBuilder fieldName = new StringBuilder(parts[0].toLowerCase());
            for (int i = 1; i < parts.length; i++) {
                if (!parts[i].isEmpty()) {
                    fieldName.append(Character.toUpperCase(parts[i].charAt(0)))
                            .append(parts[i].substring(1).toLowerCase());
                }
            }
            return fieldName.toString();
        }
        return mapKey.toLowerCase();
    }

    // 값을 적절한 타입으로 변환하는 헬퍼 메서드
    private Object convertValue(Object value, Class<?> targetType) {
        if (value == null) {
            return null;
        }

        if (targetType.isAssignableFrom(value.getClass())) {
            return value;
        }

        if (targetType == String.class) {
            return value.toString();
        }

        if (targetType == Integer.class || targetType == int.class) {
            try {
                return Integer.parseInt(value.toString());
            } catch (NumberFormatException e) {
                return 0;
            }
        }

        if (targetType == Long.class || targetType == long.class) {
            try {
                return Long.parseLong(value.toString());
            } catch (NumberFormatException e) {
                return 0L;
            }
        }

        if (targetType == Double.class || targetType == double.class) {
            try {
                return Double.parseDouble(value.toString());
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }

        if (targetType == Boolean.class || targetType == boolean.class) {
            if (value instanceof Boolean) {
                return value;
            }
            return Boolean.parseBoolean(value.toString());
        }

        return value;
    }

    // 공통정보 로그 생성 헬퍼 메서드
    private void createCommonInfoLog(Common_Info saved) {
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
    }

    public ResponseEntity<?> createVersionInfo(String ordNo, Map<String, Object> dto) {
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
    }

    public ResponseEntity<?> updateVersionInfo(String ordNo, Map<String, Object> dto) {
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
    }

    public ResponseEntity<?> getTagNumbers(String query) {
        List<Basic_Info> basics = basicInfoRepository.findAll();
        List<String> tagNumbers = new ArrayList<>();

        for (Basic_Info basic : basics) {
            String ordNo = basic.getOrdNo();
            // ordNo가 null이 아닌 경우에만 처리
            if (ordNo != null && (query == null || query.trim().isEmpty() ||
                    ordNo.toLowerCase().contains(query.toLowerCase()))) {
                tagNumbers.add(ordNo);
            }
        }
        if (tagNumbers.size() > 20) {
            tagNumbers = tagNumbers.subList(0, 20);
        }
        return ResponseEntity.ok(tagNumbers);
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