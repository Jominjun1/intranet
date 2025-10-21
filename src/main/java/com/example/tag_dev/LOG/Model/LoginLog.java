package com.example.tag_dev.LOG.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name="TB_LOG_LOGN")
public class LoginLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="seq")
    private Long logId; // 로그 ID

    @Column(name="login_id")
    private String loginId; // 로그인 ID

    private String ip_addr; // IP주소
    private String http_refr; // HTTP 레퍼런스
    private String Status;
    @Column(name="reg_dt")
    private Date regDt; // 등록일자
    private Date update_dt; // 수정일자
    private String update_id; // 수정한 사람
}
