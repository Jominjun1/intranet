package com.example.tag_dev.LOG.LogRepository;

import com.example.tag_dev.LOG.Model.DeptLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeptLogRepository extends JpaRepository<DeptLog,String> {
}
