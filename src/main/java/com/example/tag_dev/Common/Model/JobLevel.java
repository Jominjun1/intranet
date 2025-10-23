package com.example.tag_dev.Common.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="TB_BI_JOB_LEVEL")
public class JobLevel {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long levelNum;

    private String levelName; // 직급이름
    private String levelNameEN; // 직급영어명
}
