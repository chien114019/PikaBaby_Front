package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Supplier;


public interface SupplierRepository extends JpaRepository<Supplier, Integer> {
	
	List<Supplier> findByNameContaining(String keyword);
	 //Containing 模糊比對（like '%keyword%'）;
	//JPA 自動轉成SQL：SELECT * FROM supplier WHERE name LIKE '%keyword%'
	
	List<Supplier> findByDeletedFalse();

}
