package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/orders")
public class SalesOrderController {

    @Autowired private CustomerService customerService;
    @Autowired private ProductService productService;
    @Autowired private SalesOrderService orderService;

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("order", new SalesOrder());
        model.addAttribute("customers", customerService.listAll());
        model.addAttribute("products", productService.listAll());
        return "order/form";
    }

    @PostMapping("/save")
    public String saveOrder(@RequestParam Integer customerId,
                            @RequestParam("productIds") Integer[] productIds,
                            @RequestParam("quantities") Integer[] quantities,
                            Model model) {

        Customer customer = customerService.getById(customerId);

        List<Product> productList = new ArrayList<>();
        for (int i = 0; i < productIds.length; i++) {
            Product product = productService.getById(productIds[i]);
            if (quantities[i] > product.getStock()) {
                // 錯誤情況處理：顯示錯誤並回表單
                model.addAttribute("order", new SalesOrder());
                model.addAttribute("customers", customerService.listAll());
                model.addAttribute("products", productService.listAll());
                model.addAttribute("errorMessage", "商品「" + product.getName() + "」庫存不足，剩餘：" + product.getStock());
                return "order/form";
            }
            productList.add(product);
        }

        // 沒有錯誤就繼續建立訂單
        SalesOrder order = new SalesOrder();
        order.setCustomer(customer);
        order.setOrderDate(new Date());

        List<SalesOrderDetail> detailList = new ArrayList<>();
        for (int i = 0; i < productIds.length; i++) {
            Product product = productList.get(i);
            product.setStock(product.getStock() - quantities[i]);
            productService.save(product); // 更新庫存

            SalesOrderDetail detail = new SalesOrderDetail();
            detail.setOrder(order);
            detail.setProduct(product);
            detail.setQuantity(quantities[i]);
            detail.setUnitPrice(product.getPrice());
            detailList.add(detail);
        }

        order.setDetails(detailList);
        orderService.save(order);
        return "redirect:/orders";
    }


}
