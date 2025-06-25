package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Customer;
import com.example.demo.model.CustomerAddress;
import com.example.demo.repository.AddressRepository;

@Service
public class AddressService {
	@Autowired
	private AddressRepository addressRepo;

	public List<CustomerAddress> getAllByCustomer(Customer customer) {
		return addressRepo.findByCustomer(customer);
	}

	public CustomerAddress save(CustomerAddress address) {
		return addressRepo.save(address);
	}

	public void deleteById(Integer id) {
		addressRepo.deleteById(id);
	}

	public CustomerAddress findById(Integer id) {
		return addressRepo.findById(id).orElseThrow(() -> new RuntimeException("地址不存在"));
	}

	public void clearDefaultOrder(Customer customer) {
		CustomerAddress currentDefault = addressRepo.findByCustomerAndIsDefaultOrderTrue(customer);
		if (currentDefault != null) {
			currentDefault.setIsDefaultOrder(false);
			addressRepo.save(currentDefault);
		}
	}

	public void clearDefaultShipping(Customer customer) {
		CustomerAddress currentDefault = addressRepo.findByCustomerAndIsDefaultShippingTrue(customer);
		if (currentDefault != null) {
			currentDefault.setIsDefaultShipping(false);
			addressRepo.save(currentDefault);
		}
	}

	public CustomerAddress getHomeAddress(Customer cust) {
		return addressRepo.findByCustomerAndIsDefaultOrderTrue(cust);
	}

	public CustomerAddress getDeliverAddress(Customer cust) {
		return addressRepo.findByCustomerAndIsDefaultShippingTrue(cust);
	}

}
