package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.Product;
import com.example.demo.model.PurchaseOrderDetail;

public interface PurchaseOrderDetailRepository extends JpaRepository<PurchaseOrderDetail, Long> {
	List<PurchaseOrderDetail> findBySupplierProduct_Product(Product product);

	List<PurchaseOrderDetail> findBySupplierProduct_Product_Id(Long productId);

	@Query("SELECT SUM(d.quantity) FROM PurchaseOrderDetail d WHERE d.supplierProduct.product.id = :productId")
	Long sumQuantityBySupplierProduct_Product_Id(@Param("productId") Long productId);




}
