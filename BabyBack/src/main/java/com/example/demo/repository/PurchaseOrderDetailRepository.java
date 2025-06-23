package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.Product;
import com.example.demo.model.PurchaseOrderDetail;

public interface PurchaseOrderDetailRepository extends JpaRepository<PurchaseOrderDetail, Integer> {
	List<PurchaseOrderDetail> findBySupplierProduct_Product(Product product);

	Integer findBySupplierProduct_Product_Id(Integer productId);

	@Query("SELECT SUM(d.quantity) FROM PurchaseOrderDetail d WHERE d.supplierProduct.product.id = :productId")
	Integer sumQuantityBySupplierProduct_Product_Id(@Param("productId") Integer productId);


	@Query("""
		    SELECT DISTINCT pod.supplierProduct.product FROM PurchaseOrderDetail pod WHERE pod.supplierProduct.product.deleted = false
		""")
		List<Product> findDistinctProductsInPurchaseHistory();





}
