package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.ProductType;

public interface ProductTypeRepository extends JpaRepository<ProductType, Integer> {

}
