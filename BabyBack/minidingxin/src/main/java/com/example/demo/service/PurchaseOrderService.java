package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.PurchaseOrder;
import com.example.demo.repository.PurchaseOrderRepository;

@Service
public class PurchaseOrderService {
    @Autowired
    private PurchaseOrderRepository repository;

    public void save(PurchaseOrder order) {
        repository.save(order);
    }

    public List<PurchaseOrder> listAll() {
        return repository.findAll();
    }
}
