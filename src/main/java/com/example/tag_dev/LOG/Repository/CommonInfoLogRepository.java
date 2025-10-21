package com.example.tag_dev.LOG.Repository;

import com.example.tag_dev.LOG.Model.CommonInfoLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface CommonInfoLogRepository extends JpaRepository<CommonInfoLog, Long> {

    @Query("SELECT c FROM CommonInfoLog c WHERE c.createDt BETWEEN :startDate AND :endDate")
    List<CommonInfoLog> findByCreateDtBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
