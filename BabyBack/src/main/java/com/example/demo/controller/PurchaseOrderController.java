package com.example.demo.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.format.annotation.DateTimeFormat;


import com.example.demo.model.Product;
import com.example.demo.model.PurchaseOrder;
import com.example.demo.model.PurchaseOrderDetail;
import com.example.demo.model.Supplier;
import com.example.demo.service.ProductService;
import com.example.demo.service.PurchaseOrderService;
import com.example.demo.service.SupplierService;

@Controller
@RequestMapping("/purchases")
public class PurchaseOrderController {

    @Autowired private SupplierService supplierService;
    @Autowired private ProductService productService;
    @Autowired private PurchaseOrderService orderService;
    

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("suppliers", supplierService.listAll());
        model.addAttribute("products", productService.listAll());
        return "purchase/form";
    }

    @PostMapping("/save")
    public String saveOrder(@RequestParam Long supplierId,
                            @RequestParam("productIds") Long[] productIds,
                            @RequestParam("quantities") Long[] quantities,
                            @RequestParam("unitPrice") Long[] unitPrice,
                            @RequestParam("orderDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date orderDate) {

        Supplier supplier = supplierService.getById(supplierId);
        PurchaseOrder order = new PurchaseOrder();
        order.setSupplier(supplier);
        order.setOrderDate(orderDate);

        List<PurchaseOrderDetail> detailList = new ArrayList<>();
        for (int i = 0; i < productIds.length; i++) {
            Product product = productService.getById(productIds[i]);
            
            if(product.getSupplier()==null) {
            	product.setSupplier(supplier);
            	productService.save(product);
            }
            
            PurchaseOrderDetail detail = new PurchaseOrderDetail();
            detail.setOrder(order);
            detail.setProduct(product);
            detail.setQuantity(quantities[i]);
            detail.setUnitPrice(product.getPrice());
            detailList.add(detail);
        }

        order.setDetails(detailList);
        orderService.save(order);
        return "redirect:/purchases";
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("orders", orderService.listAll());
        return "purchase/list";
    }
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
    	  orderService.deleteById(id);
        return "redirect:/purchases";
    }
}

