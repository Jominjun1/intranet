package com.example.tag_dev.Common.Controller;

import com.example.tag_dev.Common.DTO.CustomerDTO;
import com.example.tag_dev.Common.Service.CommonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/common")
public class CommonController {

    private final CommonService commonService;

    // 고객사 목록 조회
    @GetMapping("/getCustomers")
    public ResponseEntity<?> getCustomers() {
        log.info("고객사 목록 조회 요청");
        return ResponseEntity.ok(commonService.getCustomers());
    }
    // 고객사 추가
    @PostMapping("/createCustomer")
    public ResponseEntity<?> createCustomer(Authentication authentication , @RequestBody CustomerDTO customerDTO) {
        log.info("고객사 추가 요청 : {}", authentication.getName());
        return ResponseEntity.ok(commonService.createCustomer(customerDTO));
    }
    // 고객사 수정/삭제
    @PutMapping("/updateCustomer")
    public ResponseEntity<?> updateCustomer(@PathVariable String customerCode, @RequestBody CustomerDTO customerDTO, Authentication authentication) {
        log.info("고객사 수정 요청 : {}", authentication.getName());
        return ResponseEntity.ok(commonService.updateCustomer(customerCode, customerDTO));
    }

    // 직급 조회
    @GetMapping("/getJobList")
    public ResponseEntity<?> getJobList() {
        log.info("직급 목록 조회 요청");
        return ResponseEntity.ok(commonService.getJobLevels());
    }

    // 직급 추가
    @PostMapping("/createJobLevel")
    public ResponseEntity<?> createJobLevel(@RequestBody String jobName, Authentication authentication) {
        log.info("직급 추가 요헝 : {}", authentication.getName());
        return ResponseEntity.ok(commonService.createJobLevel(jobName));
    }
}
