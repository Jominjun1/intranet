package com.example.tag_dev.LOG.Repository;

import com.example.tag_dev.LOG.Model.CustomerLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface CustomerLogRepository extends JpaRepository<CustomerLog,Long> {
    @Query("SELECT c FROM CustomerLog c WHERE c.regDt BETWEEN :startDate AND :endDate")
    List<CustomerLog> findByCreateDtBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

}
