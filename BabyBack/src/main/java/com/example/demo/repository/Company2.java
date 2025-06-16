package com.example.demo.repository;

import com.example.demo.model.Company;

public interface Company2 {
    Company getCompanyInfo();
    Company getById(Long id);
    void saveOrUpdate(Company company);
}