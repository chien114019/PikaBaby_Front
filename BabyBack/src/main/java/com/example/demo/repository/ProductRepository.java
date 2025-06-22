package com.example.demo.repository;

import com.example.demo.model.Product;
import com.example.demo.model.SupplierProduct;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Integer> {
	List<Product> findByNameContainingIgnoreCase(String name);
	
	@Query("SELECT sp FROM SupplierProduct sp JOIN FETCH sp.supplier JOIN FETCH sp.product")
	List<SupplierProduct> findAllWithDetails();
	
	List<Product> findByPublishedTrue();

	List<Product> findByDeletedFalse();

	Optional<Product> findById(Integer id);
	
//	List<Product> findAllByPublishedAndAge1(Boolean published, Boolean age1);
//
//	List<Product> findAllByPublishedAndAge2(Boolean published, Boolean age1);
//
//	List<Product> findAllByPublishedAndAge3(Boolean published, Boolean age1);
//
//	List<Product> findAllByPublishedAndAge4(Boolean published, Boolean age1);

}
