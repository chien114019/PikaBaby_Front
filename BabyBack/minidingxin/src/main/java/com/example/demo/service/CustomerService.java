package com.example.demo.service;

import com.example.demo.model.Customer;
import com.example.demo.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository repository;

    public List<Customer> listAll() {
        return repository.findAll();
    }

    public Customer getById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public void save(Customer customer) {
        repository.save(customer);
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }
}
