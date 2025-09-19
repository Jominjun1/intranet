package com.example.tag_dev.SYSTEM.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import org.springframework.transaction.annotation.Transactional;

@Entity
@Table(name="DAILY_PROJECT_R")
public class Daily_ProJect_R {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String taskDescription; // 업무내용
    private String hour;
    private String minute;
    private String status;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "DAILY_REPORT_ID")
    private DailyReport_info dailyReportInfo;

    @ManyToOne
    @JoinColumn(name = "PROJECT_CODE")
    private Project_Info project;

}
