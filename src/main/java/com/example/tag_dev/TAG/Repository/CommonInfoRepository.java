package com.example.tag_dev.TAG.Repository;

import com.example.tag_dev.TAG.Model.Common_Info;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommonInfoRepository extends JpaRepository<Common_Info, Long> {
}
