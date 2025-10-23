package com.example.tag_dev.Common.Service;

import com.example.tag_dev.Common.DTO.CustomerDTO;
import com.example.tag_dev.Common.Model.Customers;
import com.example.tag_dev.Common.Model.JobLevel;
import com.example.tag_dev.Common.Repository.CustomersRepository;
import com.example.tag_dev.Common.Repository.JobLevelRepository;
import com.example.tag_dev.LOG.Model.CustomerLog;
import com.example.tag_dev.LOG.Repository.CustomerLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommonService {

    private final CustomersRepository customersRepository;
    private final JobLevelRepository jobLevelRepository;
    private final CustomerLogRepository customerLogRepository;

    public ResponseEntity<?> getCustomers() {
        try {
            List<Customers> customers = customersRepository.findAll();
            return ResponseEntity.ok(customers);
        } catch (Exception e) {
            System.err.println("부서 목록 조회 오류: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("고객사 목록 조회 중 오류 발생");
        }
    }
    public ResponseEntity<?> createCustomer(CustomerDTO customerDTO) {
        try{
            Customers customers = new Customers();
            customers.setCustomer_Code(customerDTO.getCustomer_Code());
            customers.setCustomer_Name(customerDTO.getCustomer_Name());
            customers.setUserName("관리자");
            customers.setRegDt(new Date());
            customers.setAddress(customerDTO.getAddress());

            customersRepository.save(customers);

            CustomerLog customerLog = new CustomerLog();
            customerLog.setCustomer_code(customerDTO.getCustomer_Code());
            customerLog.setCustomer_name(customerDTO.getCustomer_Name());
            customerLog.setUserName(customerDTO.getUserName());
            customerLog.setRegDt(customerDTO.getRegDt());
            customerLog.setAddress(customerDTO.getAddress());

            customerLogRepository.save(customerLog);

            return ResponseEntity.ok(customers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("고객사 추가 중 오류 발생");
        }
    }

    public ResponseEntity<?> updateCustomer(String customerCode, CustomerDTO customerDTO) {
        Optional<Customers> customers = customersRepository.findById(customerCode);
        if(customers.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("고객사 없음");
        }

        Customers customer = customers.get();

        if(customerDTO.getCustomer_Code() != null) customer.setCustomer_Code(customerDTO.getCustomer_Code());
        if(customerDTO.getCustomer_Name() != null) customer.setCustomer_Name(customerDTO.getCustomer_Name());

        customer.setUpdateDt(new Date());
        customer.setUserName(customerDTO.getUserName());
        customersRepository.save(customer);

        createCustomerLog(customerDTO);

        return ResponseEntity.ok("고객사 추가 완료");
    }

    public ResponseEntity<?> createJobLevel(String jobName) {
        try{
            JobLevel jobLevels = new JobLevel();
            jobLevels.setLevelName(jobName);

            jobLevelRepository.save(jobLevels);

            return ResponseEntity.ok("직급 추가 완료");
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("직급 추가 중 오류 발생");
        }
    }

    public ResponseEntity<?> getJobLevels(){
        try{
            List<JobLevel> jobLevels = jobLevelRepository.findAll();
            return ResponseEntity.ok(jobLevels);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("직급 조회 중 오류 발생");
        }
    }


    private void createCustomerLog(CustomerDTO customerDTO) {
        CustomerLog customerLog = new CustomerLog();
        customerLog.setCustomer_code(customerDTO.getCustomer_Code());
        customerLog.setCustomer_name(customerDTO.getCustomer_Name());

        customerLog.setUpdateDt(new Date());
        customerLog.setUserName(customerDTO.getUserName());

        customerLogRepository.save(customerLog);

    }

}
