package com.example.tag_dev.LOG.Repository;

import com.example.tag_dev.LOG.Model.ProjectLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface ProjectLogRepository extends JpaRepository<ProjectLog , Long> {
    @Query("SELECT p FROM ProjectLog p WHERE p.startDt BETWEEN :startDate AND :endDate")
    Page<ProjectLog> findByRegDtBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate, Pageable pageable);

    @Query("SELECT b FROM ProjectLog b WHERE b.createDt BETWEEN :startDate AND :endDate")
    List<ProjectLog> findByCreateDtBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

}
