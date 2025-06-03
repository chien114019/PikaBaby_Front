package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.ReturnOrder;

public interface ReturnOrderRepository extends JpaRepository<ReturnOrder, Long> {
}

