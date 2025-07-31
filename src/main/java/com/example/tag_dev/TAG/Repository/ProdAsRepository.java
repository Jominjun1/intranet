package com.example.tag_dev.TAG.Repository;

import com.example.tag_dev.TAG.Model.Prod_As;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdAsRepository extends JpaRepository<Prod_As, Long> {
}
