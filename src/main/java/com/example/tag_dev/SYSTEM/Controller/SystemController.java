package com.example.tag_dev.SYSTEM.Controller;

import com.example.tag_dev.SYSTEM.Service.SystemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/sys")
public class SystemController {

    private final SystemService systemService;


    @GetMapping("/holiday/{year}")
    public ResponseEntity<?> getHoliday(@PathVariable("year") int year){
        return systemService.getHoliday(year);
    }
}
