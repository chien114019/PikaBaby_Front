package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Company;
import com.example.demo.model.Company2;
import com.example.demo.repository.CompanyRepository;

@Service
public class CompanyService implements Company2 {

    @Autowired
    private CompanyRepository companyRepository;

    @Override
    public Company getCompanyInfo() {
        return companyRepository.findById("1").orElse(new Company()); // 假設 ID = 1 是唯一公司
    }

    @Override
    public void saveOrUpdate(Company company) {
        company.setId(1L); // 固定ID（如果你只有一家）
        companyRepository.save(company);
    }
}


