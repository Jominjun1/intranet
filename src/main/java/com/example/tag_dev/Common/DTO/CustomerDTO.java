package com.example.tag_dev.Common.DTO;

import lombok.Data;

import java.util.Date;

@Data
public class CustomerDTO {
    private String customer_Code; // 고객사 코드
    private String customer_Name; // 고객사 이름
    private String address; // 고객사 주소
    private String userName;
    private Date regDt;
    private Date updateDt;
}
