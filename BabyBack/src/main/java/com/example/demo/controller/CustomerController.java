package com.example.demo.controller;

import com.example.demo.model.Customer;
import com.example.demo.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/customers") // 所有路徑都會以 /customers 開頭
public class CustomerController {
    @Autowired
    private CustomerService service;
    
    // 顯示客戶清單
    // 路徑：/customers，方法：GET
    @GetMapping
    public String list(Model model) {
        model.addAttribute("customers", service.listAll()); // 將所有客戶資料加入 model
        return "customer/list";  // 回傳顯示清單的 Thymeleaf 頁面
    }
    
    // 顯示新增客戶的表單
    // 路徑：/customers/new，方法：GET
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("customer", new Customer());// 建立一個空的 Customer 物件供表單綁定使用
        return "customer/form"; // 回傳顯示新增／編輯表單的頁面
    }

    // 儲存客戶資料（新增或更新）
    // 路徑：/customers/save，方法：POST
    @PostMapping("/save")
    public String save(@ModelAttribute Customer customer) {
        service.save(customer); // 呼叫服務層儲存資料
        return "redirect:/customers";// 儲存後重新導向到客戶清單頁
    }

    // 顯示編輯特定客戶的表單
    // 路徑：/customers/edit/{id}，方法：GET
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("customer", service.getById(id));// 根據 ID 查詢客戶並加入 model
        return "customer/form";// 使用相同的表單頁面進行編輯
    }

    // 刪除指定 ID 的客戶資料
    // 路徑：/customers/delete/{id}，方法：GET
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);// 呼叫服務層刪除該客戶
        return "redirect:/customers";// 刪除後回到清單頁面
    }
    
    
}
