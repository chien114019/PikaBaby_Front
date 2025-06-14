package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.service.ProductService;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductService service;

    //0611喬新增
    @GetMapping
    public String list(Model model) {
        model.addAttribute("products", service.getAllProductsWithStock());
        return "product/list";
    }
    
    
//    @GetMapping
//    public String list(Model model) {
//        model.addAttribute("products", service.listAll());
//        return "product/list";
//    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("product", new Product());
        return "product/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Product product,
                       @RequestParam("imageFiles") MultipartFile[] imageFiles) { //接收 <input type="file" name="images" multiple> 的所有上傳圖
        try {
            service.save(product, imageFiles);
        } catch (IOException e) { //若使用者上傳壞圖或檔案轉換錯誤，會被捕捉並避免 crash
            e.printStackTrace(); // 也可以記 log
            return "error"; // 或回傳錯誤頁
        }
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("product", service.getById(id));
        return "product/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/products";
    }
}
