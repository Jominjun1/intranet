package com.example.tag_dev.TAG.Controller;

import com.example.tag_dev.TAG.DTO.TagSettingDTO;
import com.example.tag_dev.TAG.Service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/tags")
public class TagController {

    @Autowired
    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    // 스마트태그 재고 목록 조회 (ORD_NO 기준 매칭, 검색조건 포함)
    @GetMapping("/getTagList")
    public ResponseEntity<?> getTagInventoryList(@RequestParam(required = false) String macAddr, @RequestParam(required = false) String facCd,
            @RequestParam(required = false) String facNo, @RequestParam(required = false, defaultValue = "all") String delFilter) {
        try {
            log.info("재고 목록 조회 요청 : {} , {} , {} , {}" , macAddr, facCd, facNo, delFilter);
            return ResponseEntity.ok(tagService.getTagInventoryList(macAddr, facCd, facNo, delFilter));
        }catch (Exception e) {
            log.info("에러 발생 : {}" , e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // 처리단계 상세 조회
    @GetMapping("/proc_step_{ordNo}")
    public ResponseEntity<?> getProcStep(@PathVariable String ordNo) {
        try {
            log.info("처리단계  상세 조회 요청 : {}" , ordNo);
            return ResponseEntity.ok(tagService.getProcStep(ordNo));
        } catch (Exception e) {
            log.info("에러 발생 : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // 세팅정보(제품버전) 상세 조회 (최신)
    @GetMapping("/setting_info_{ordNo}")
    public ResponseEntity<?> getSettingInfo(@PathVariable String ordNo) {
        try {
            log.info("세팅정보 상세 조회 요청 : {}" , ordNo);
            return ResponseEntity.ok(tagService.getSettingInfo(ordNo));
        } catch (Exception e) {
            log.info("에러 발생 : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // 최신 버전 정보 조회
    @GetMapping("/latest_version_{ordNo}")
    public ResponseEntity<?> getLatestVersionInfo(@PathVariable String ordNo) {
        try {
            log.info("최신 버전 정보 조회 요청 : {}" , ordNo);
            return ResponseEntity.ok(tagService.getLatestVersionInfo(ordNo));
        } catch (Exception e) {
            log.info("에러 발생 : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // 세팅정보 수정/삭제 (제품버전 0.1 증가)
    @PutMapping("/update_setting_{ordNo}")
    public ResponseEntity<?> updateSettingInfo(@PathVariable String ordNo, @RequestBody TagSettingDTO SettingDTO, @RequestHeader("Authorization") String token) {
        try {
            log.info("세팅정보 수정 요청 : {}" , ordNo);
            return ResponseEntity.ok(tagService.updateSettingInfo(ordNo, SettingDTO, token));
        } catch (Exception e) {
            log.info("에러 발생 : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // AS 이력 조회 (생성일 순으로 정렬, MAC주소 포함, 삭제여부 필터링)
    @GetMapping("/prod_as_{ordNo}")
    public ResponseEntity<?> getProdAsList(@PathVariable String ordNo, @RequestParam(required = false, defaultValue = "active") String filter) {
        try{
            log.info("AS 이력 조회 요청 : {}" , ordNo);
            return ResponseEntity.ok(tagService.getProdAsList(ordNo, filter));
        }catch (Exception e) {
            log.info("에러 발생 : {}" , e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // AS 등록
    @PostMapping("/{ordNo}_as")
    public ResponseEntity<?> createProdAs(@PathVariable String ordNo, @RequestBody Map<String, Object> dto) {
        try{
            log.info("AS 등록 요청 : {}",  ordNo);
            return ResponseEntity.ok(tagService.createProdAs(ordNo, dto));
        }catch (Exception e) {
            log.info("에러 발생 : {}" , e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // AS 수정/삭제 (기존 AS 기록 수정)
    @PutMapping("/update_{ordNo}_as")
    public ResponseEntity<?> updateProdAs(@PathVariable String ordNo, @RequestBody Map<String, Object> dto) {
        try{
            log.info("AS 수정 요청 : {}" , ordNo);
            return ResponseEntity.ok(tagService.updateProdAs(ordNo, dto));
        }catch (Exception e) {
            log.info("에러 발생 : {}" , e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // 제품버전 이력 전체 조회 (세팅정보 포함)
    @GetMapping("/version-history/{ordNo}")
    public ResponseEntity<?> getVersionHistory(@PathVariable String ordNo) {
        try{
            log.info("제품 버전 이력 전체 조회 요청 : {}" , ordNo);
            return ResponseEntity.ok(tagService.getVersionHistory(ordNo));
        }catch (Exception e) {
            log.info("에러 발생 : {}" , e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // 공통정보 이력 전체 조회 (MAC/공장코드/SN)
    @GetMapping("/common_history_{ordNo}")
    public ResponseEntity<?> getCommonHistory(@PathVariable String ordNo) {
        try{
            log.info("공통 정보 이력 조회 요청 : {}" , ordNo);
            return ResponseEntity.ok(tagService.getCommonHistory(ordNo));
        }catch (Exception e) {
            log.info("에러 발생 : {}" , e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // 공통정보 등록
    @PostMapping("/common_history")
    public ResponseEntity<?> createCommonInfo(@RequestBody Map<String, Object> dto) {
        try{
            return ResponseEntity.ok(tagService.createCommonInfo(dto));
        }catch (Exception e) {
            log.info("에러 발생 : {}" , e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // 공통정보 수정/삭제
    @PutMapping("/update/common_{ordNo}")
    public ResponseEntity<?> updateCommonInfo(@PathVariable String ordNo, @RequestBody Map<String, Object> dto) {
        try{
            return ResponseEntity.ok(tagService.updateCommonInfo(ordNo, dto));
        }catch (Exception e) {
            log.info("에러 발생 : {}" , e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // 제품버전 등록
    @PostMapping("/version_{ordNo}")
    public ResponseEntity<?> createVersionInfo(@PathVariable String ordNo, @RequestBody Map<String, Object> dto) {
        try{
            return ResponseEntity.ok(tagService.createVersionInfo(ordNo, dto));
        }catch (Exception e) {
            log.info("에러 발생 : {}" , e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // 제품버전 수정/삭제
    @PutMapping("/update/version_{ordNo}")
    public ResponseEntity<?> updateVersionInfo(@PathVariable String ordNo, @RequestBody Map<String, Object> dto) {
        try{
            return ResponseEntity.ok(tagService.updateVersionInfo(ordNo, dto));
        }catch (Exception e) {
            log.info("에러 발생 : {}" , e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // 태그번호 자동완성
    @GetMapping("/tag-numbers")
    public ResponseEntity<?> getTagNumbers(@RequestParam(required = false) String query) {
        try{
            log.info("태그번호 자동완성 요청 : {}" , query);
            return ResponseEntity.ok(tagService.getTagNumbers(query));
        }catch (Exception e) {
            log.info("에러 발생 : {}" , e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
} 