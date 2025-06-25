package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Customer;
import com.example.demo.model.CustomerFavorites;
import com.example.demo.model.Product;

import java.util.List;
import java.util.Optional;


public interface CustomerFavoritesRepository extends JpaRepository<CustomerFavorites, Integer> {
	 // 查詢某會員的所有收藏
    List<CustomerFavorites> findByCustomer(Customer customer);

    // 檢查是否已收藏
    Optional<CustomerFavorites> findByCustomerAndProduct(Customer customer, Product product);

    // 移除特定收藏
    void deleteByCustomerAndProduct(Customer customer, Product product);
    
    List<CustomerFavorites> findAllByCustomer(Customer cust);
}
