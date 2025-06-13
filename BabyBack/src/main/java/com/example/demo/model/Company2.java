package com.example.demo.model;

public interface Company2 {
    Company getCompanyInfo();
    Company getById(Long id);
    void saveOrUpdate(Company company);
}
