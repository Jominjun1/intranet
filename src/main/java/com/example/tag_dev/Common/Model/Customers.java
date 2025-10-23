package com.example.tag_dev.Common.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name="TB_BI_CUSTOMERS")
public class Customers {

    @Id
    private String customer_Code; // 고객사 코드

    private String customer_Name; // 고객사 이름
    private String address; // 고객사 주소
    private String userName; // 등록한 사람
    private Date regDt; // 등록일
    private Date updateDt;
}
