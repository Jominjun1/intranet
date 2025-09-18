package com.example.tag_dev.LOG.Controller;

import com.example.tag_dev.LOG.Service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/Log")
public class LogController {

    private final LogService logService;

    @GetMapping("/getLog")
    public ResponseEntity<?> getLog(@RequestParam String type, @RequestParam(required = false) String startDate,
                                    @RequestParam(required = false) String endDate) {
        log.info("로그 조회 요청 - 타입: {}, 시작일: {}, 종료일: {}", type, startDate, endDate);
        return ResponseEntity.ok(logService.searchLog(type, startDate, endDate));
    }
}
