package com.example.tag_dev.SYSTEM.Controller;

import com.example.tag_dev.SYSTEM.DTO.DailyDTO;
import com.example.tag_dev.SYSTEM.Model.DailyReport_info;
import com.example.tag_dev.SYSTEM.Service.DailyService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/daily")
public class DailyReportController {

    private final DailyService dailyService;

    @PostMapping("/create")
    public ResponseEntity<?> createReport(HttpSession session, @RequestBody DailyDTO dto) {
        String userName = (String) session.getAttribute("user_name");
        DailyReport_info report = dailyService.createReport(userName, dto);
        return ResponseEntity.ok(report);
    }

    // 특정 날짜 보고 조회
    @GetMapping("/list")
    public ResponseEntity<?> getReports(HttpSession session,
                                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        String userName = (String) session.getAttribute("user_name");
        List<DailyReport_info> list = dailyService.getReportsByUser(userName, date);
        return ResponseEntity.ok(list);
    }

    // 보고 수정
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateReport(HttpSession session,
                                          @RequestBody DailyDTO dto) {
        String userName = (String) session.getAttribute("user_name");
        DailyReport_info report = dailyService.updateReport(userName , dto);
        return ResponseEntity.ok(report);
    }
    // 보고 삭제
    @PutMapping("/delete/{id}")
    public ResponseEntity<?> deleteReport(HttpSession session, @PathVariable Long id) {
        String userName = (String) session.getAttribute("user_name");
        DailyReport_info report = dailyService.deleteReport(userName, id);
        return ResponseEntity.ok(report);
    }
}
