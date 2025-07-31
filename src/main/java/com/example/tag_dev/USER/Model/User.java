package com.example.tag_dev.USER.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name ="TB_ST_USER")
public class User {

    @Id
    @Column(name = "seq")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId; // PK값

    @Column(name="user_name")
    private String userName; // 사용자 이름

    private String user_en_name; // 사용자 영어 이름

    @Column(name="user_email")
    private String userEmail; // 사용자 이메일

    @Column(name="user_phone_num")
    private String userPhoneNum; // 사용자 전화번호

    private String user_acl; // 사용자 권한 레벨 ( "1" 사용자 , "2" 과장급 이상 , "3" 개발자 , "4" 운영자)

    @Column(name ="login_id")
    @Pattern(regexp = "[a-zA-z0-9]{4,9}")
    private String loginId;

    private String password; // 로그인 비밀번호
    private String dept_cd; // 부서 코드
    private String user_level; // 사용자 직급
    private String user_job; // 사용자 직책
    private String user_stat; // 사용자 상태

    private String lang_cd; // 언어 코드
    private Long reg_id; // 등록한 사용자 ID
    private Date reg_dt; // 등록일자
    private Long update_id; // 수정한 사용자 ID
    private Date update_dt; // 수정일자
    private Date login_dt; // 로그인 일자
    private Date hire_dt; // 입사일자
    private Date change_password_dt; // 비밀번호 변경 일자
    private Long fail_login_cnt; // 실패한 로그인 횟수
    private String total_vac_dt; // 전체 휴가 일수

    private String jwt_token; // JWT 토큰
    private Date jwt_expiry_pt; // JWT 만료일자
}
