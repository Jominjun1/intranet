package com.example.tag_dev.TAG.Repository;

import com.example.tag_dev.TAG.Model.Version_Info;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VersionInfoRepository extends JpaRepository<Version_Info , Long> {
}
