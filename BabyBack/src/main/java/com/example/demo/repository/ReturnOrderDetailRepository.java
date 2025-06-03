package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.ReturnOrderDetail;

public interface ReturnOrderDetailRepository extends JpaRepository<ReturnOrderDetail, Long> {
}

