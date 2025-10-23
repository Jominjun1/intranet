package com.example.tag_dev.Common.Repository;

import com.example.tag_dev.Common.Model.Customers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomersRepository extends JpaRepository<Customers, String> {

    @Query("SELECT c FROM Customers c WHERE c.customer_Code =:customerCode")
    Optional<Customers> findByCustomerCode(@Param("customerCode") String customerCode);

}
