package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.model.ReturnOrder;

public interface ReturnOrderRepository extends JpaRepository<ReturnOrder, Integer> {
	@Query("SELECT FUNCTION('DATE_FORMAT', r.returnDate, '%Y-%m'), SUM(d.total) " +
		       "FROM ReturnOrder r JOIN r.details d " +
		       "GROUP BY FUNCTION('DATE_FORMAT', r.returnDate, '%Y-%m') " +
		       "ORDER BY FUNCTION('DATE_FORMAT', r.returnDate, '%Y-%m')")
		List<Object[]> getMonthlyTotal();

}

