package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.model.SalesOrder;
import com.example.demo.model.Customer;


public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {
	
	@Query(value = """
		    SELECT DATE_FORMAT(o.order_date, '%Y-%m') AS month,
		           SUM(d.quantity * d.unit_price) AS total
		    FROM sales_order o
		    JOIN sales_order_detail d ON o.id = d.order_id
		    GROUP BY month
		    ORDER BY month
		""", nativeQuery = true)
		List<Object[]> getMonthlySalesRaw();

	List<SalesOrder> findAllByCustomer(Customer customer);
}
