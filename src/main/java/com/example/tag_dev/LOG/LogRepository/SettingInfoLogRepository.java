package com.example.tag_dev.LOG.LogRepository;

import com.example.tag_dev.LOG.Model.SettingInfoLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface SettingInfoLogRepository extends JpaRepository<SettingInfoLog, Long> {
    
    @Query("SELECT s FROM SettingInfoLog s WHERE s.createDt BETWEEN :startDate AND :endDate")
    Page<SettingInfoLog> findByRegDtBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate, Pageable pageable);
}
