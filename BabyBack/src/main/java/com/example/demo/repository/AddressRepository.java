package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Customer;
import com.example.demo.model.CustomerAddress;

public interface AddressRepository extends JpaRepository<CustomerAddress, Integer>{

	 List<CustomerAddress> findByCustomer(Customer customer);

	    // 找預設地址（可選）
	    CustomerAddress findByCustomerAndIsDefaultOrderTrue(Customer customer);
	    CustomerAddress findByCustomerAndIsDefaultShippingTrue(Customer customer);
	
	
}
