package com.example.demo.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Receivable;

public interface ReceivableRepository extends JpaRepository<Receivable, Long> {
	
	//0609喬新增
    List<Receivable> findByCustomerNameContainingIgnoreCase(String keyword);
    List<Receivable> findByCustomerNameContainingIgnoreCaseAndCreatedAtBetween(String keyword, Timestamp start, Timestamp end);
    List<Receivable> findByCustomerNameContainingIgnoreCaseAndCreatedAtAfter(String keyword, Timestamp start);
    List<Receivable> findByCustomerNameContainingIgnoreCaseAndCreatedAtBefore(String keyword, Timestamp end);
}
