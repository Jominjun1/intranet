package com.example.tag_dev.TAG.Repository;

import com.example.tag_dev.TAG.Model.Proc_Step;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcStepRepository extends JpaRepository<Proc_Step, Long> {
}
