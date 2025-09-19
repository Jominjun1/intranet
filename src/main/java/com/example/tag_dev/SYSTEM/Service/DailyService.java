package com.example.tag_dev.SYSTEM.Service;

import com.example.tag_dev.LOG.Repository.DailyReportLogRepository;
import com.example.tag_dev.SYSTEM.Repository.DailyReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DailyService {

    private final DailyReportRepository dailyReportRepository;
    private final DailyReportLogRepository dailyReportLogRepository;


}
