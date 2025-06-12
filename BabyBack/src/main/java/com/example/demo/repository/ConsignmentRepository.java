package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Consignment;
import com.example.demo.model.ProductType;
import com.example.demo.model.Customer;


public interface ConsignmentRepository extends JpaRepository<Consignment, Integer>{
	List<Consignment> findAllByProductTypeAndReviewAndDelivery(ProductType pType, Integer review, Integer delivery);
	List<Consignment> findAllByProductTypeAndReview(ProductType pType, Integer review);
	List<Consignment> findAllByProductTypeAndDelivery(ProductType pType, Integer delivery);
	List<Consignment> findAllByProductType(ProductType pType);
	List<Consignment> findAllByReviewAndDelivery(Integer review, Integer delivery);
	List<Consignment> findAllByReview(Integer review);
	List<Consignment> findAllByDelivery(Integer delivery);
	
	List<Consignment> findAllByCustomer(Customer customer);
}
