package com.example.tag_dev.LOG.LogRepository;

import com.example.tag_dev.LOG.Model.ProdAsLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface ProdAsLogRepository extends JpaRepository<ProdAsLog, Long> {
    
    @Query("SELECT p FROM ProdAsLog p WHERE p.createDt BETWEEN :startDate AND :endDate")
    Page<ProdAsLog> findByRegDtBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate, Pageable pageable);
}
