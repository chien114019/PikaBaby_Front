package com.example.demo.controller;

import com.example.demo.model.Supplier;
import com.example.demo.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/suppliers")
public class SupplierController {

    @Autowired
    private SupplierRepository supplierRepo;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("suppliers", supplierRepo.findAll());
        return "supplier/list";
    }

    @GetMapping("/new")
    public String newSupplier(Model model) {
        model.addAttribute("supplier", new Supplier());
        return "supplier/form";
    }

    @GetMapping("/edit/{id}")
    public String editSupplier(@PathVariable Integer id, Model model) {
        Supplier supplier = supplierRepo.findById(id).orElseThrow();
        model.addAttribute("supplier", supplier);
        return "supplier/form";
    }

    @PostMapping("/save")
    public String saveSupplier(@ModelAttribute Supplier supplier) {
        supplierRepo.save(supplier);
        return "redirect:/suppliers";
    }

    @GetMapping("/delete/{id}")
    public String deleteSupplier(@PathVariable Integer id) {
        supplierRepo.deleteById(id);
        return "redirect:/suppliers";
    }
}
