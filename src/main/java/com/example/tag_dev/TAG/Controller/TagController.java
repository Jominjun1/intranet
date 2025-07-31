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

    // 스마트태그 재고 목록 조회 (TAG_NO 기준 매칭, 검색조건 포함)
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
    @GetMapping("/proc_step_{tagNo}")
    public ResponseEntity<?> getProcStep(@PathVariable String tagNo) {
        try {
            log.info("처리단계  상세 조회 요청 : {}" , tagNo);
            return ResponseEntity.ok(tagService.getProcStep(tagNo));
        } catch (Exception e) {
            log.info("에러 발생 : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // 세팅정보(제품버전) 상세 조회 (최신)
    @GetMapping("/setting_info_{tagNo}")
    public ResponseEntity<?> getSettingInfo(@PathVariable String tagNo) {
        try {
            log.info("세팅정보 상세 조회 요청 : {}" , tagNo);
            return ResponseEntity.ok(tagService.getSettingInfo(tagNo));
        } catch (Exception e) {
            log.info("에러 발생 : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // 세팅정보 수정 (제품버전 0.1 증가)
    @PutMapping("/update_setting_{tagNo}")
    public ResponseEntity<?> updateSettingInfo(@PathVariable String tagNo, @RequestBody TagSettingDTO SettingDTO) {
        try {
            log.info("세팅정보 수정 요청 : {}" , tagNo);
            return ResponseEntity.ok(tagService.updateSettingInfo(tagNo, SettingDTO));
        } catch (Exception e) {
            log.info("에러 발생 : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // AS 이력 조회 (생성일 순으로 정렬, MAC주소 포함, 삭제여부 필터링)
    @GetMapping("/prod_as_{tagNo}")
    public ResponseEntity<?> getProdAsList(@PathVariable String tagNo, @RequestParam(required = false, defaultValue = "active") String filter) {
        try{
            log.info("AS 이력 조회 요청 : {}" , tagNo);
            return ResponseEntity.ok(tagService.getProdAsList(tagNo, filter));
        }catch (Exception e) {
            log.info("에러 발생 : {}" , e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // AS 등록
    @PostMapping("/{tagNo}_as")
    public ResponseEntity<?> createProdAs(@PathVariable String tagNo, @RequestBody Map<String, Object> dto) {
        try{
            log.info("AS 등록 요청 : {}",  tagNo);
            return ResponseEntity.ok(tagService.createProdAs(tagNo, dto));
        }catch (Exception e) {
            log.info("에러 발생 : {}" , e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // AS 수정 (기존 AS 기록 수정)
    @PutMapping("/update_{tagNo}_as")
    public ResponseEntity<?> updateProdAs(@PathVariable String tagNo, @RequestBody Map<String, Object> dto) {
        try{
            log.info("AS 수정 요청 : {}" , tagNo);
            return ResponseEntity.ok(tagService.updateProdAs(tagNo, dto));
        }catch (Exception e) {
            log.info("에러 발생 : {}" , e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // AS 삭제
    @DeleteMapping("/delete/{prodAsId}")
    public ResponseEntity<?> deleteProdAs(@PathVariable Long prodAsId) {
        try{
            log.info("AS 삭제 요청 : {}" , prodAsId);
            return ResponseEntity.ok(tagService.deleteProdAs(prodAsId));
        }catch (Exception e) {
            log.info("에러 발생 : {}" , e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // 제품버전 이력 전체 조회 (세팅정보 포함)
    @GetMapping("/version-history")
    public ResponseEntity<?> getVersionHistory(@PathVariable String tagNo) {
        try{
            log.info("제품 버전 이력 전체 조회 요청 : {}" , tagNo);
            return ResponseEntity.ok(tagService.getVersionHistory(tagNo));
        }catch (Exception e) {
            log.info("에러 발생 : {}" , e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // 공통정보 이력 전체 조회 (MAC/공장코드/SN)
    @GetMapping("/common_history")
    public ResponseEntity<?> getCommonHistory(@PathVariable String tagNo) {
        try{
            return ResponseEntity.ok(tagService.getCommonHistory(tagNo));
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

    // 공통정보 수정
    @PutMapping("/update/common_{tagNo}")
    public ResponseEntity<?> updateCommonInfo(@PathVariable String tagNo, @RequestBody Map<String, Object> dto) {
        try{
            return ResponseEntity.ok(tagService.updateCommonInfo(tagNo, dto));
        }catch (Exception e) {
            log.info("에러 발생 : {}" , e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // 제품버전 등록
    @PostMapping("/version_{tagNo}")
    public ResponseEntity<?> createVersionInfo(@PathVariable String tagNo, @RequestBody Map<String, Object> dto) {
        try{
            return ResponseEntity.ok(tagService.createVersionInfo(tagNo, dto));
        }catch (Exception e) {
            log.info("에러 발생 : {}" , e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // 제품버전 수정
    @PutMapping("/update/version_{tagNo}")
    public ResponseEntity<?> updateVersionInfo(@PathVariable String tagNo, @RequestBody Map<String, Object> dto) {
        try{
            return ResponseEntity.ok(tagService.updateVersionInfo(tagNo, dto));
        }catch (Exception e) {
            log.info("에러 발생 : {}" , e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
} 