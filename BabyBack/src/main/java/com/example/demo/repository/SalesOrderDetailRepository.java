package com.example.demo.repository;

import com.example.demo.model.SalesOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SalesOrderDetailRepository extends JpaRepository<SalesOrderDetail, Integer> {

    @Query("SELECT SUM(s.quantity) FROM SalesOrderDetail s WHERE s.supplierProduct.product.id = :productId")
    Long sumQuantityBySupplierProductProductId(@Param("productId") Integer productId);
    
    // 修正版本：確保查詢所有訂單詳情，不管訂單狀態
    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM SalesOrderDetail s WHERE s.product.id = :productId")
    Long sumQuantityByProductId(@Param("productId") Integer productId);
    
    // 新增：只計算已確認訂單的銷售量
    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM SalesOrderDetail s WHERE s.product.id = :productId AND s.order.status != -1")
    Long sumQuantityByProductIdExcludeCancelled(@Param("productId") Integer productId);
    
    // 新增：根據商品ID查詢所有訂單詳情（用於除錯）
    @Query("SELECT s FROM SalesOrderDetail s WHERE s.product.id = :productId")
    java.util.List<SalesOrderDetail> findByProductId(@Param("productId") Integer productId);
    
    
   
}
