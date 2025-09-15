package com.example.tag_dev.LOG.Repository;

import com.example.tag_dev.LOG.Model.VersionInfoLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface VersionInfoLogRepository extends JpaRepository<VersionInfoLog, Long> {
    
    @Query("SELECT v FROM VersionInfoLog v WHERE v.createDt BETWEEN :startDate AND :endDate")
    Page<VersionInfoLog> findByCreateDtBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate, Pageable pageable);
    
    @Query("SELECT v FROM VersionInfoLog v WHERE v.createDt BETWEEN :startDate AND :endDate")
    List<VersionInfoLog> findByCreateDtBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
