package com.example.tag_dev.LOG.LogRepository;

import com.example.tag_dev.LOG.Model.CommonInfoLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface CommonInfoLogRepository extends JpaRepository<CommonInfoLog, Long> {
    
    @Query("SELECT c FROM CommonInfoLog c WHERE c.createDt BETWEEN :startDate AND :endDate")
    Page<CommonInfoLog> findByRegDtBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate, Pageable pageable);
}
