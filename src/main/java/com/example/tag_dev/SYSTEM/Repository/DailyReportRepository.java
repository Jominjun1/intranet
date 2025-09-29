package com.example.tag_dev.SYSTEM.Repository;

import com.example.tag_dev.SYSTEM.Model.DailyReport_info;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.*;

public interface DailyReportRepository extends JpaRepository<DailyReport_info, Integer> {
    List<DailyReport_info> findByUserNameAndCreateDtBetween(String name, Date start, Date end);

    @Query("SELECT d FROM DailyReport_info d WHERE d.userName=:name")
    Optional<DailyReport_info> findById(String name);

    @Query("SELECT d FROM DailyReport_info d WHERE d.DailyReportInfoId=:id")
    Optional<DailyReport_info> findById(Long id);
}
