package com.example.demo.controller;

import com.example.demo.model.Supplier;
import com.example.demo.service.SupplierService; // 改用 Service
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/suppliers")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @GetMapping
    public String list(@RequestParam(required = false) String keyword, Model model) {
        List<Supplier> suppliers;
        if (keyword != null && !keyword.isEmpty()) {
            suppliers = supplierService.searchByName(keyword); // 搜尋用 Service
        } else {
            suppliers = supplierService.listAll(); // 全部查詢用 Service
        }
        model.addAttribute("suppliers", suppliers);
        model.addAttribute("keyword", keyword); // 回填搜尋框的內容
        return "supplier/list";
    }

    @GetMapping("/new")
    public String newSupplier(Model model) {
        model.addAttribute("supplier", new Supplier());
        return "supplier/form";
    }

    @GetMapping("/edit/{id}")
    public String editSupplier(@PathVariable Integer id, Model model) {
        Supplier supplier = supplierService.getById(id);
        model.addAttribute("supplier", supplier);
        return "supplier/form";
    }

    @PostMapping("/save")
    public String saveSupplier(@ModelAttribute Supplier supplier) {
        supplierService.save(supplier);
        return "redirect:/suppliers";
    }

    @GetMapping("/delete/{id}")
    public String deleteSupplier(@PathVariable Integer id) {
        supplierService.deleteById(id);
        return "redirect:/suppliers";
    }
}
