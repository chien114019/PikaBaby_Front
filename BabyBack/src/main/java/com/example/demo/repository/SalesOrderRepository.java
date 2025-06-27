package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.SalesOrder;
import com.example.demo.model.Customer;

public interface SalesOrderRepository extends JpaRepository<SalesOrder, Integer> {
	
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
	
	@Query("""
			SELECT SUM(sod.quantity*sod.unitPrice) FROM SalesOrder so
			JOIN so.details sod
			WHERE so.customer = :customer
			""")
	Integer getConsumptionByCustomer(@Param("customer") Customer customer);
	
	@Query("""
			SELECT COUNT(so) FROM SalesOrder so
			WHERE so.customer = :customer
			""")
	Integer getOrderTotalByCustomer(@Param("customer") Customer customer);
	
	Optional<SalesOrder> findByOrderNumber(String orderNumber);

	
	
}
