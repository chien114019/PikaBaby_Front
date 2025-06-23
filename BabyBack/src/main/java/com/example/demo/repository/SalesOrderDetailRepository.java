package com.example.demo.repository;

import com.example.demo.model.SalesOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SalesOrderDetailRepository extends JpaRepository<SalesOrderDetail, Integer> {

    @Query("SELECT SUM(s.quantity) FROM SalesOrderDetail s WHERE s.supplierProduct.product.id = :productId")
    Long sumQuantityBySupplierProductProductId(@Param("productId") Integer productId);
    
    
   
}
