package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.Company;
import com.example.demo.service.CompanyService;

@Controller
@RequestMapping("/company")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    // 顯示公司基本資料
    @GetMapping
    public String showCompanyInfo(Model model) {
        Company company = companyService.getCompanyInfo();
        model.addAttribute("company", company);
        return "company/list";
    }

    // 進入編輯頁（固定撈 ID 為 1）
    @GetMapping("/form")
    public String editCompanyForm(Model model) {
        Company company = companyService.getCompanyInfo();
        model.addAttribute("company", company);
        return "company/form";
    }

    // 處理儲存
    @PostMapping("/save")
    public String updateCompany(@ModelAttribute Company company) {
        companyService.saveOrUpdate(company);
        return "redirect:/company";
    }

    // 可選擇 ID 的版本（如未來支援多公司）
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("company", companyService.getById(id));
        return "company/form";
    }
}
