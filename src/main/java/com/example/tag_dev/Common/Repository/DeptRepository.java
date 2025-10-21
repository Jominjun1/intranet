package com.example.tag_dev.Common.Repository;

import com.example.tag_dev.Common.Model.Dept_Info;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DeptRepository extends JpaRepository<Dept_Info , String> {

    @Query("SELECT d FROM Dept_Info d WHERE d.deptCode =:deptCode")
    Optional<Dept_Info> findByDeptCode(@Param("deptCode") String deptCode);

}
