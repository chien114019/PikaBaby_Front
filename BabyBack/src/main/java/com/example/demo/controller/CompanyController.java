package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.model.Company;
import com.example.demo.service.CompanyService;

@Controller
@RequestMapping("/company")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    // 顯示公司資料
    @GetMapping
    public String showCompanyInfo(Model model) {
        Company company = companyService.getCompanyInfo();
        model.addAttribute("company", company);
        return "company/list";
    }

    // 進入編輯頁
    @GetMapping("/form")
    public String editCompanyForm(Model model) {
        Company company = companyService.getCompanyInfo();
        model.addAttribute("company", company);
        return "company/form";
    }
    
    

    // 處理更新
    @PostMapping("/save")
    public String updateCompany(@ModelAttribute Company company) {
        companyService.saveOrUpdate(company);
        return "redirect:/company";
    }
}
