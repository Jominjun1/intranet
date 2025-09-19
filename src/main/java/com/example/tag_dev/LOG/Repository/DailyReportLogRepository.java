package com.example.tag_dev.LOG.Repository;

import com.example.tag_dev.LOG.Model.DailyReportLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface DailyReportLogRepository extends JpaRepository<DailyReportLog, Integer> {

    @Query("SELECT b FROM DailyReportLog b WHERE b.createDt BETWEEN :startDate AND :endDate")
    List<DailyReportLog> findByCreateDtBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
