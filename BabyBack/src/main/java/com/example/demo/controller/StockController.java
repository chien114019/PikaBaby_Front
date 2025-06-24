package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.service.ProductService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class StockController {

    @Autowired
    private ProductService productService;

    @GetMapping("/stock")
    public String showStock(@RequestParam(required = false) String keyword, Model model) {
        List<Product> products;

        if (keyword != null && !keyword.isBlank()) {
            products = productService.searchByName(keyword);
        } else {
            products = productService.getAllProducts();
        }

        // 將每個產品的動態庫存算出來（修正：使用正確的計算方法）
        Map<Integer, Long> stockMap = new HashMap<>();
        for (Product p : products) {
            // ✅ 修正：使用正確的庫存計算方法（進貨 - 銷售）
            Long stock = productService.getCurrentCalculatedStock(p.getId());
            stockMap.put(p.getId(), stock);
            
            // 除錯日誌
            System.out.println("📊 庫存查詢 - 商品ID: " + p.getId() + 
                              ", 商品名稱: " + p.getName() + 
                              ", 計算庫存: " + stock);
        }

        model.addAttribute("products", products);
        model.addAttribute("stockMap", stockMap); // 將庫存 map 傳到前端
        model.addAttribute("keyword", keyword);

        return "stock/list";
    }

    
    

    
}
