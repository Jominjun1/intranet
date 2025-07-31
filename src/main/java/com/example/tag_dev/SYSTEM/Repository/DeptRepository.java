package com.example.tag_dev.SYSTEM.Repository;

import com.example.tag_dev.SYSTEM.Model.Dept_Info;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DeptRepository extends JpaRepository<Dept_Info , String> {

    @Query("SELECT d FROM Dept_Info d WHERE d.deptCode =:deptCode")
    Optional<Dept_Info> findByDeptCode(@Param("deptCode") String deptCode);

    @Modifying
    @Query("DELETE FROM Dept_Info d WHERE d.deptCode =:deptCode")
    void deleteByDeptCode(String deptCode);
}
