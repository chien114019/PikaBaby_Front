package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.PurchaseOrderDetail;

public interface PurchaseOrderDetailRepository extends JpaRepository<PurchaseOrderDetail, Long> {
//	@Query("SELECT SUM(p.quantity) FROM PurchaseOrderDetail p WHERE p.product.id = :productId")
//	Long findTotalPurchasedQuantityByProductId(@Param("productId") Long productId);
	
	//0611喬新增
	@Query("SELECT SUM(p.quantity) FROM PurchaseOrderDetail p WHERE p.product.id = :productId")
	Long sumQuantityByProductId(@Param("productId") Long productId);

	List<PurchaseOrderDetail> findByProductId(Long productId);
	

}
