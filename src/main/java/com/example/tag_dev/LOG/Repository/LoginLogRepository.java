package com.example.tag_dev.LOG.Repository;

import com.example.tag_dev.LOG.Model.LoginLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {

    @Query("SELECT u FROM LoginLog u WHERE u.regDt BETWEEN :startDate AND :endDate")
    List<LoginLog> findByRegDtBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
