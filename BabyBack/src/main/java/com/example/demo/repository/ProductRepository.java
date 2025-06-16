package com.example.demo.repository;

import com.example.demo.model.Product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {
	List<Product> findByNameContainingIgnoreCase(String name);
	
	@Query("SELECT p FROM Product p LEFT JOIN FETCH p.supplier")
	List<Product> findAllWithSupplier();


}
