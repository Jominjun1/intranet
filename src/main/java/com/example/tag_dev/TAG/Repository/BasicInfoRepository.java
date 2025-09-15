package com.example.tag_dev.TAG.Repository;

import com.example.tag_dev.TAG.Model.Basic_Info;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BasicInfoRepository extends JpaRepository<Basic_Info, Long> {

}
