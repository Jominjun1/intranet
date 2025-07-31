package com.example.tag_dev.LOG.LogRepository;

import com.example.tag_dev.LOG.Model.BasicInfoLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface BasicInfoLogRepository extends JpaRepository<BasicInfoLog, Long> {
    
    @Query("SELECT b FROM BasicInfoLog b WHERE b.createDt BETWEEN :startDate AND :endDate")
    Page<BasicInfoLog> findByRegDtBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate, Pageable pageable);
}
