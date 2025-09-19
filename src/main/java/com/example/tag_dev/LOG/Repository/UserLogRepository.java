package com.example.tag_dev.LOG.Repository;

import com.example.tag_dev.LOG.Model.UserLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface UserLogRepository extends JpaRepository<UserLog, Long> {

    @Query("SELECT u FROM UserLog u WHERE u.regDt BETWEEN :startDate AND :endDate")
    List<UserLog> findByRegDtBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
