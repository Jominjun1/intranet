package com.example.tag_dev.SYSTEM.Repository;

import com.example.tag_dev.SYSTEM.Model.DailyReport_info;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyReportRepository extends JpaRepository<DailyReport_info, Integer> {
}
