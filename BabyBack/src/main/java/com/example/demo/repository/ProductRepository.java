package com.example.demo.repository;

import com.example.demo.model.Product;
import com.example.demo.model.SupplierProduct;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {
	List<Product> findByNameContainingIgnoreCase(String name);
	
	@Query("SELECT sp FROM SupplierProduct sp JOIN FETCH sp.supplier JOIN FETCH sp.product")
	List<SupplierProduct> findAllWithDetails();

	

}
