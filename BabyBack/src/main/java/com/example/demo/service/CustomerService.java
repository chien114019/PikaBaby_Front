package com.example.demo.service;

import com.example.demo.model.Customer;
import com.example.demo.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository repository;

    public List<Customer> listAll() {
        return repository.findAll();
    }

    public Customer getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void save(Customer customer) {
        repository.save(customer);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
    
//    註冊抓註冊時間、給點數100點
    public Customer register(Customer Customer) {
        if (repository.findByEmail(Customer.getEmail()).isPresent()) {
            throw new RuntimeException("Email 已被註冊");
        }

        Customer.setCreatedAt(LocalDateTime.now());
        Customer.setPoints(100);

        return repository.save(Customer);
    }
    
//    登入比對
    public Optional<Customer> findByEmail(String email) {
    return repository.findByEmail(email);
}
}
