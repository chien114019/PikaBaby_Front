package com.example.demo.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.Consignment;
import com.example.demo.model.ProductType;
import com.example.demo.model.Customer;

public interface ConsignmentRepository extends JpaRepository<Consignment, Integer> {

//	============後台API=============
	List<Consignment> findAllByProductTypeAndReviewAndDelivery(ProductType pType, Integer review, Integer delivery);

	List<Consignment> findAllByProductTypeAndReview(ProductType pType, Integer review);

	List<Consignment> findAllByProductTypeAndDelivery(ProductType pType, Integer delivery);

	List<Consignment> findAllByProductType(ProductType pType);

	List<Consignment> findAllByReviewAndDelivery(Integer review, Integer delivery);

	List<Consignment> findAllByReview(Integer review);

	List<Consignment> findAllByDelivery(Integer delivery);
	
	@Query("""
			SELECT c FROM Consignment c
			WHERE c.customer.id = :custId AND review > 0 AND c.withdrawal IS NULL
			""")
	List<Consignment> getStorageByCust(@Param("custId") Long custId);

//	============前台API=============
	List<Consignment> findAllByCustomer(Customer customer);

	List<Consignment> findAllByCustomerAndApplyDateBetweenAndProductTypeAndReview(Customer cust, Date applyStart,
			Date applyEnd, ProductType type, Integer review);

	List<Consignment> findAllByCustomerAndApplyDateBetweenAndProductType(Customer cust, Date applyStart, Date applyEnd,
			ProductType type);

	List<Consignment> findAllByCustomerAndApplyDateBetweenAndReview(Customer cust, Date applyStart, Date applyEnd,
			Integer review);

	List<Consignment> findAllByCustomerAndApplyDateBetween(Customer cust, Date applyStart, Date applyEnd);

	List<Consignment> findAllByCustomerAndApplyDateGreaterThanEqualAndProductTypeAndReview(Customer cust,
			Date applyStart, ProductType pType, Integer review);

	List<Consignment> findAllByCustomerAndApplyDateGreaterThanEqualAndProductType(Customer cust, Date applyStart,
			ProductType pType);

	List<Consignment> findAllByCustomerAndApplyDateGreaterThanEqualAndReview(Customer cust, Date applyStart,
			Integer review);

	List<Consignment> findAllByCustomerAndApplyDateGreaterThanEqual(Customer cust, Date applyStart);

	List<Consignment> findAllByCustomerAndApplyDateLessThanEqualAndProductTypeAndReview(Customer cust, Date applyEnd,
			ProductType pType, Integer review);

	List<Consignment> findAllByCustomerAndApplyDateLessThanEqualAndProductType(Customer cust, Date applyEnd,
			ProductType pType);

	List<Consignment> findAllByCustomerAndApplyDateLessThanEqualAndReview(Customer cust, Date applyEnd, Integer review);

	List<Consignment> findAllByCustomerAndApplyDateLessThanEqual(Customer cust, Date applyEnd);

	List<Consignment> findAllByCustomerAndProductTypeAndReview(Customer cust, ProductType pType, Integer review);

	List<Consignment> findAllByCustomerAndProductType(Customer cust, ProductType pType);

	List<Consignment> findAllByCustomerAndReview(Customer cust, Integer review);
}
