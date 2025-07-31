package com.example.tag_dev.USER.DTO;

import lombok.Data;

import java.util.Date;

@Data
public class UserDTO {
    private Long user_id;
    private String user_name;
    private String user_en_name;
    private String user_email;
    private String user_phone_num;
    private Long user_acl;
    private String login_id;
    private String password;
    private String dept_cd;
    private String user_level;
    private String user_job;
    private String user_stat;
    private String lang_cd;
    private Long reg_id;
    private Date reg_dt;
    private Long update_id;
    private Date update_dt;
    private Date login_dt;
    private Date hire_dt;
    private Date change_password_dt;
    private Long fail_login_cnt;
    private String total_vac_dt;
}
