package com.example.tag_dev.LOG.Repository;

import com.example.tag_dev.LOG.Model.ProdAsLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface ProdAsLogRepository extends JpaRepository<ProdAsLog, Long> {
    
    @Query("SELECT p FROM ProdAsLog p WHERE p.createDt BETWEEN :startDate AND :endDate")
    Page<ProdAsLog> findByCreateDtBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate, Pageable pageable);
    
    @Query("SELECT p FROM ProdAsLog p WHERE p.createDt BETWEEN :startDate AND :endDate")
    List<ProdAsLog> findByCreateDtBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
