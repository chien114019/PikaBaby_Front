package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Supplier;
import com.example.demo.repository.SupplierRepository;

@Service
public class SupplierService {
    @Autowired
    private SupplierRepository repository;

    public List<Supplier> listAll() {
        return repository.findAll();
    }

    public Supplier getById(Integer id) {
        return repository.findById(id).orElse(null);
    }
    
    
    //搜尋關鍵字
    public List<Supplier> searchByName(String keyword) {
        return repository.findByNameContaining(keyword);
    }
    
    public void save(Supplier supplier) {
        repository.save(supplier);
    }

    public void deleteById(Integer id) {
        repository.deleteById(id);
    }

}
