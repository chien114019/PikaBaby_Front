package com.example.demo.repository;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.model.ReturnOrderDetail;

public interface ReturnOrderDetailRepository extends JpaRepository<ReturnOrderDetail, Integer> {
	@Query("SELECT d.product.name, COUNT(d.id) " +
		       "FROM ReturnOrderDetail d " +
		       "GROUP BY d.product.name " +
		       "ORDER BY COUNT(d.id) DESC")
		List<Object[]> getTopReturnProducts();



}

