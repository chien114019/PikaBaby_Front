package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.SalesOrderDetail;

public interface SalesOrderDetailRepository extends JpaRepository<SalesOrderDetail, Integer> {
    // 可加上自定查詢方法
	
	//0611喬新增
	@Query("SELECT SUM(s.quantity) FROM SalesOrderDetail s WHERE s.product.id = :productId")
	Long sumQuantityByProductId(@Param("productId") Long productId);
}
