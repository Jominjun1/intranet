package com.example.tag_dev.LOG.Repository;

import com.example.tag_dev.LOG.Model.ProcStepLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface ProcStepLogRepository extends JpaRepository<ProcStepLog, Long> {
    
    @Query("SELECT p FROM ProcStepLog p WHERE p.createDt BETWEEN :startDate AND :endDate")
    Page<ProcStepLog> findByCreateDtBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate, Pageable pageable);
    
    @Query("SELECT p FROM ProcStepLog p WHERE p.createDt BETWEEN :startDate AND :endDate")
    List<ProcStepLog> findByCreateDtBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
