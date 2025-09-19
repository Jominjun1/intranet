package com.example.tag_dev.SYSTEM.Controller;

import com.example.tag_dev.Config.JwtTokenProvider;
import com.example.tag_dev.LOG.Model.DailyReportLog;
import com.example.tag_dev.SYSTEM.DTO.DailyDTO;
import com.example.tag_dev.SYSTEM.Service.DailyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/daily")
public class DailyReportController {

    private final DailyService dailyService;
    private final JwtTokenProvider jwtTokenProvider;
//
//    // 일일보고 조회 ( 한개 )
//    @GetMapping("/getDaily")
//    public ResponseEntity<?> getDailyReportByDailyId(Integer dailyId) {
//
//    }
//
//    // 일일보고 전체 조회
//    @GetMapping("/myDaily")
//    public ResponseEntity<?> getMyDailyReportByDailyId(String userName) {
//
//    }
//
//    // 일일보고 작성
//    @PostMapping("/create")
//    public ResponseEntity<?> createDailyReport(@RequestBody DailyDTO dailyDTO) {
//
//    }
//    // 일일보고 수정/삭제
//    @PutMapping("/update")
//    public ResponseEntity<?> updateDailyReport(@RequestBody DailyDTO dailyDTO) {
//
//    }
}
