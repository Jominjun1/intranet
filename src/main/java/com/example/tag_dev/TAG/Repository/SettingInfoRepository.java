package com.example.tag_dev.TAG.Repository;

import com.example.tag_dev.TAG.Model.Setting_Info;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingInfoRepository extends JpaRepository<Setting_Info, Long> {
}
