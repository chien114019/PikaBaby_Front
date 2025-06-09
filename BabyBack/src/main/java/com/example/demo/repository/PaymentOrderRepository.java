package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.PaymentOrder;

public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, Long> {
	@Query("SELECT SUM(p.appliedAmount) FROM PaymentOrder p WHERE p.salesOrder.id = :orderId")
	Double getTotalPaidByOrder(@Param("orderId") Long orderId);

}

