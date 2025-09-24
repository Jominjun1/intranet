package com.example.tag_dev.SYSTEM.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="DAILY_PROJECT_R")
public class Daily_Project_R {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String taskDescription; // 업무내용
    private String hour;
    private String minute;
    private String status;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "DailyReportInfoId")
    private DailyReport_info dailyReportInfo;

    @ManyToOne
    @JoinColumn(name = "seq")
    private Project_Info project;

}
