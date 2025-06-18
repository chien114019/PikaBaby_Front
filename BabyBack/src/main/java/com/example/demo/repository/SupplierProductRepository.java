package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.SupplierProduct;

public interface SupplierProductRepository extends JpaRepository<SupplierProduct, Long> {
	List<SupplierProduct> findBySupplierId(Long supplierId);
}
