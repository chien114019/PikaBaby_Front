package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.model.Product;
import com.example.demo.model.SupplierProduct;

public interface SupplierProductRepository extends JpaRepository<SupplierProduct, Integer> {
	List<SupplierProduct> findBySupplierId(Integer supplierId);
	
	@Query("""
		    SELECT sp FROM SupplierProduct sp 
		    JOIN FETCH sp.product p 
		    JOIN FETCH sp.supplier s 
		   
		""")
		List<SupplierProduct> findAllValid();  // ✅ 只包含未被刪除的商品
	
	@Query("""
		    SELECT sp FROM SupplierProduct sp 
		    JOIN FETCH sp.product p 
		    JOIN FETCH sp.supplier s 
		    WHERE p.deleted = false AND s.deleted = false
		""")
		List<SupplierProduct> findAllValidForPurchase();


	

}
