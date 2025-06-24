package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Company;
import com.example.demo.repository.Company2;
import com.example.demo.repository.CompanyRepository;

@Service
public class CompanyService implements Company2 {

    @Autowired
    private CompanyRepository companyRepository;

    @Override
    public Company getCompanyInfo() {
        return companyRepository.findById((int) 51500025).orElse(new Company()); // 假設 ID(統編) = 51500025 是唯一公司
    }

    @Override
    public void saveOrUpdate(Company company) {
        //company.setId(1L); // 固定ID（如果你只有一家）
    	
    	// 如果資料庫是空的，就新增
        if (company.getId() == null || !companyRepository.existsById(company.getId())) {
            companyRepository.save(company); // 新增
        } else {
            // 更新
            companyRepository.save(company);
        }
    }
    
    //0613喬新增
    public Company getById(Integer id) {
        return companyRepository.findById(id).orElse(new Company());
    }

	@Override
	public Company getById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}
    
    
}


