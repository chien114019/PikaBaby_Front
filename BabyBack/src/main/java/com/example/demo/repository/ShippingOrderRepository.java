package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.SalesOrder;
import com.example.demo.model.ShippingOrder;

public interface ShippingOrderRepository extends JpaRepository<ShippingOrder, Integer> {
	ShippingOrder findBySalesOrder(SalesOrder order);
}