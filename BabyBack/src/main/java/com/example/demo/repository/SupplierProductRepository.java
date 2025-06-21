package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.model.Product;
import com.example.demo.model.SupplierProduct;

public interface SupplierProductRepository extends JpaRepository<SupplierProduct, Integer> {
	List<SupplierProduct> findBySupplierId(Integer supplierId);
	

}
