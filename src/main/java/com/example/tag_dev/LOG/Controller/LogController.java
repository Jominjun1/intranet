package com.example.tag_dev.LOG.Controller;

import com.example.tag_dev.LOG.Service.LogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/Log")
public class LogController {

    @Autowired
    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    @GetMapping("/getLog")
    public ResponseEntity<?> getLog(@RequestParam String type ) {
        try{
            log.info("로그 조회 요청");
            return ResponseEntity.ok(logService.searchLog(type));
        } catch (Exception e) {
            log.info("에러 발생 : {}" , e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
