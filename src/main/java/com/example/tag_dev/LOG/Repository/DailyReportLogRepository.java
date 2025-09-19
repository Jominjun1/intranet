package com.example.tag_dev.LOG.Repository;

import com.example.tag_dev.LOG.Model.DailyReportLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyReportLogRepository extends JpaRepository<DailyReportLog, Integer> {
}
