package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.SupplierProduct;
import com.example.demo.repository.SupplierProductRepository;

@Service
public class SupplierProductService {

    @Autowired
    private SupplierProductRepository repository;

    public void save(SupplierProduct sp) {
        repository.save(sp);
    }

    public List<SupplierProduct> listAll() {
        return repository.findAll();
    }

    public SupplierProduct getById(Integer supplierProductIds) {
        return repository.findById(supplierProductIds).orElse(null);
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }
    
    public List<SupplierProduct> findBySupplierId(Integer supplierId) {
        return repository.findBySupplierId(supplierId);
    }
    
    public List<SupplierProduct> findAll() {
        return repository.findAll();
    }
    
    public List<SupplierProduct> findAllValidForPurchase() {
        return repository.findAllValidForPurchase();
    }

}
