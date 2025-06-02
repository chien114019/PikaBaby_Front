package com.example.demo.service;

import com.example.demo.model.SalesOrder;
import com.example.demo.repository.SalesOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalesOrderService {

    @Autowired
    private SalesOrderRepository repository;

    public void save(SalesOrder order) {
        repository.save(order);
    }

    public List<SalesOrder> listAll() {
        return repository.findAll();
    }
}
