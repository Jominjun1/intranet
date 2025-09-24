package com.example.tag_dev.SYSTEM.Repository;

import com.example.tag_dev.SYSTEM.Model.Project_Info;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project_Info , Long> {

    @Query("SELECT MAX(p.projectCode) FROM Project_Info p WHERE p.projectCode LIKE CONCAT(:yearSuffix, '%')")
    String findMaxCodeByYear(@Param("yearSuffix") String yearSuffix);

    @Query("SELECT p FROM Project_Info p WHERE p.projectCode =:projectCode")
    Optional<Project_Info> findByProjectCode(String projectCode);
}
