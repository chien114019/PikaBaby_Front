package com.example.demo.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.Product;
import com.example.demo.model.PurchaseOrder;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Integer> {

	long countByOrderDate(LocalDate now);
	
	@Query("SELECT FUNCTION('DATE_FORMAT', o.orderDate, '%Y-%m') AS month, SUM(d.unitPrice * d.quantity) " +
		       "FROM PurchaseOrder o JOIN o.details d " +
		       "GROUP BY month ORDER BY month")
	List<Object[]> getMonthlyPurchaseCostRaw();
	
	@Query("""
		    SELECT SUM(pod.quantity) 
		    FROM PurchaseOrderDetail pod 
		    WHERE pod.supplierProduct.product.id = :productId
		""")
		Long sumQuantityByProductId(@Param("productId") Integer productId);
	

}
