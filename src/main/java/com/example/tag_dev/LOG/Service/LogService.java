package com.example.tag_dev.LOG.Service;

import com.example.tag_dev.LOG.LogRepository.*;
import com.example.tag_dev.TAG.Repository.SettingInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class LogService {

    @Autowired
    private final BasicInfoLogRepository basicInfoLogRepository;
    private final CommonInfoLogRepository commonInfoLogRepository;
    private final DeptLogRepository deptLogRepository;
    private final ProjectLogRepository projectLogRepository;
    private final ProdAsLogRepository prodAsLogRepository;
    private final ProcStepLogRepository procStepLogRepository;
    private final SettingInfoRepository settingInfoRepository;
    private final UserLogRepository userLogRepository;
    private final VersionInfoLogRepository versionInfoLogRepository;

    public LogService(BasicInfoLogRepository basicInfoLogRepository, CommonInfoLogRepository commonInfoLogRepository, DeptLogRepository deptLogRepository, ProjectLogRepository projectLogRepository, ProdAsLogRepository prodAsLogRepository, ProcStepLogRepository procStepLogRepository, SettingInfoRepository settingInfoRepository, UserLogRepository userLogRepository, VersionInfoLogRepository versionInfoLogRepository) {
        this.basicInfoLogRepository = basicInfoLogRepository;
        this.commonInfoLogRepository = commonInfoLogRepository;
        this.deptLogRepository = deptLogRepository;
        this.projectLogRepository = projectLogRepository;
        this.prodAsLogRepository = prodAsLogRepository;
        this.procStepLogRepository = procStepLogRepository;
        this.settingInfoRepository = settingInfoRepository;
        this.userLogRepository = userLogRepository;
        this.versionInfoLogRepository = versionInfoLogRepository;
    }

    // 로그 조회 ( 동적 )
    public ResponseEntity<?> searchLog(String type) {
        return ResponseEntity.ok().build();
    }
}
