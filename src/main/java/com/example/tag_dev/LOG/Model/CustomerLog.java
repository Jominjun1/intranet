package com.example.tag_dev.LOG.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name="TB_LOG_CUSTOMER")
public class CustomerLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="seq")
    private Long log_seq;

    private String customer_code; // 고객사 코드

    private String customer_name; // 고객사 이름
    private String address; // 고객사 주소
    private String userName; // 등록한 사람
    private Date regDt; // 등록일
    private Date updateDt;
}
