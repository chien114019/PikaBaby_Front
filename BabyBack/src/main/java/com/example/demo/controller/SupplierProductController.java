package com.example.demo.controller;

import com.example.demo.model.SupplierProduct;
import com.example.demo.repository.SupplierProductRepository;
import com.example.demo.service.SupplierProductService;
import com.example.demo.service.ProductService;
import com.example.demo.service.SupplierService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/supplier-products")
public class SupplierProductController {

    @Autowired
    private SupplierProductService service;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private ProductService productService;
    
    @Autowired
    private SupplierProductRepository supplierProductRepository;

    @GetMapping
    public String list(Model model) {
        List<SupplierProduct> list = supplierProductRepository.findAllValid();
        model.addAttribute("supplierProducts", list);
        return "supplier-product/list";  // 必須建立這個 HTML
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("supplierProduct", new SupplierProduct());
        model.addAttribute("suppliers", supplierService.listAll());
        model.addAttribute("products", productService.listAll());
        return "supplier-product/form";  // 必須建立這個 HTML
    }

    @PostMapping("/save")
    public String save(@ModelAttribute SupplierProduct supplierProduct) {
        service.save(supplierProduct);
        return "redirect:/supplier-products";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        service.delete(id);
        return "redirect:/supplier-products";
    }
    
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Integer id, Model model) {
        SupplierProduct sp = service.getById(id);
        model.addAttribute("supplierProduct", sp);
        model.addAttribute("suppliers", supplierService.listAll());
        model.addAttribute("products", productService.listAll());
        return "supplier-product/form";
    }

}
