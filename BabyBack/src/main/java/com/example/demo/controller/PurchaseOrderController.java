package com.example.demo.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import com.example.demo.repository.PurchaseOrderRepository;
import com.example.demo.service.ProductService;
import com.example.demo.service.PurchaseOrderService;
import com.example.demo.service.SupplierProductService;
import com.example.demo.service.SupplierService;

@Controller
@RequestMapping("/purchases")
public class PurchaseOrderController {

    @Autowired private SupplierService supplierService;
    @Autowired private ProductService productService;
    @Autowired private PurchaseOrderService orderService;
    @Autowired private SupplierProductService supplierProductService;
    @Autowired private PurchaseOrderRepository purchaseOrderRepository;

    

    @GetMapping("/new")
    public String createForm(Model model) {
    	 PurchaseOrder order = new PurchaseOrder();
    	    
    	    // 產生自動單號，例如 PO202406180001
    	    String generatedOrderNumber = orderService.generateOrderNumber();
    	    order.setOrderNumber(generatedOrderNumber);

    	model.addAttribute("order", order);
        model.addAttribute("suppliers", supplierService.listAll());
        model.addAttribute("products", productService.listAll());
        model.addAttribute("supplierProducts", supplierProductService.listAll());
        return "purchase/form";
    }
    

    @PostMapping("/save")
    public String saveOrder(
            @RequestParam("supplierProductIds") Long[] supplierProductIds,
            @RequestParam("quantities") Long[] quantities,
            @RequestParam("unitPrice") BigDecimal[] unitPrice,
            @RequestParam("orderDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date orderDate) {

        PurchaseOrder order = new PurchaseOrder();
        order.setOrderDate(orderDate);

        List<PurchaseOrderDetail> detailList = new ArrayList<>();
        for (int i = 0; i < supplierProductIds.length; i++) {
            var sp = supplierProductService.getById(supplierProductIds[i]);

            PurchaseOrderDetail detail = new PurchaseOrderDetail();
            detail.setOrder(order);
            detail.setProduct(sp.getProduct());
            detail.setQuantity(quantities[i]);
            detail.setUnitPrice(sp.getPrice()); // 改抓 supplierProduct 的價格
            detailList.add(detail);
        }

        order.setSupplier(detailList.get(0).getProduct().getSupplier()); // 取第一項商品的供應商
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
    
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        PurchaseOrder order = orderService.getById(id);
        if (order == null) {
            return "redirect:/purchases";  // 如果找不到就返回清單
        }

        model.addAttribute("order", order);
        model.addAttribute("suppliers", supplierService.listAll());
        model.addAttribute("products", productService.listAll());
        model.addAttribute("supplierProducts", supplierProductService.listAll());
        return "purchase/form";  // 共用新增用的 form
    }

}

