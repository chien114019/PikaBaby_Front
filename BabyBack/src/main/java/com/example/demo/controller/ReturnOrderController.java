package com.example.demo.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;


import com.example.demo.model.Product;
import com.example.demo.model.ReturnOrder;
import com.example.demo.model.ReturnOrderDetail;
import com.example.demo.model.SalesOrder;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.ReturnOrderDetailRepository;
import com.example.demo.repository.ReturnOrderRepository;
import com.example.demo.repository.SalesOrderRepository;



@Controller
@RequestMapping("/returns")
public class ReturnOrderController {

    @Autowired
    private SalesOrderRepository salesOrderRepository;

    @Autowired
    private ReturnOrderRepository returnOrderRepository;

    @Autowired
    private ReturnOrderDetailRepository returnOrderDetailRepository;

    @Autowired
    private ProductRepository productRepository;

    // 顯示建立退貨單頁面
    @GetMapping("/create")
    public String showReturnForm(@RequestParam(required = false) Long orderId, Model model) {
        List<SalesOrder> orders = salesOrderRepository.findAll();
        model.addAttribute("orders", orders);

        if (orderId != null) {
            SalesOrder selectedOrder = salesOrderRepository.findById(orderId).orElse(null);
            model.addAttribute("selectedOrder", selectedOrder);
        }

        return "returns/create"; 
    }

    // 處理退貨單提交
    @PostMapping("/create")
    public String processReturn(
            @RequestParam Long orderId,
            @RequestParam List<Long> productId,
            @RequestParam List<Integer> qty,
            @RequestParam List<Double> unitPrice,
            @RequestParam(required = false) String reason
    ) {
        // 建立主檔
        ReturnOrder ro = new ReturnOrder();
        ro.setReturnNo("RT" + System.currentTimeMillis());
        ro.setReturnDate(LocalDate.now());
        ro.setSalesOrder(salesOrderRepository.findById(orderId).orElse(null));
        ro.setReason(reason);

        List<ReturnOrderDetail> detailList = new ArrayList<>();

        for (int i = 0; i < productId.size(); i++) {
            if (qty.get(i) > 0) {
                Product p = productRepository.findById(productId.get(i)).orElse(null);
                ReturnOrderDetail d = new ReturnOrderDetail();
                d.setProduct(p);
                d.setQty(qty.get(i));
                d.setUnitPrice(unitPrice.get(i));
                d.setTotal(qty.get(i) * unitPrice.get(i));
                d.setReturnOrder(ro);
                detailList.add(d);

                // 加回庫存
                if (p != null) {
                    p.setStock(p.getStock() + qty.get(i));
                    productRepository.save(p);
                }
            }
        }

        ro.setDetails(detailList);
        returnOrderRepository.save(ro); // 連同 detail cascade 一起存

        return "redirect:/returns/list";
    }
    
    @GetMapping("/list")
    public String showReturnList(
        @RequestParam(required = false) String customerName,
        @RequestParam(required = false) String startDate,
        @RequestParam(required = false) String endDate,
        @RequestParam(required = false) Long orderId,
        Model model) {

        List<ReturnOrder> returnOrders = returnOrderRepository.findAll(); // 可改為客製查詢

        // 過濾條件
        if (customerName != null && !customerName.isBlank()) {
            returnOrders = returnOrders.stream()
                .filter(ro -> ro.getSalesOrder().getCustomer().getName().contains(customerName))
                .collect(Collectors.toList());
        }

        if (orderId != null) {
            returnOrders = returnOrders.stream()
                .filter(ro -> ro.getSalesOrder().getId().equals(orderId))
                .collect(Collectors.toList());
        }

        if (startDate != null && endDate != null) {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            returnOrders = returnOrders.stream()
                .filter(ro -> !ro.getReturnDate().isBefore(start) && !ro.getReturnDate().isAfter(end))
                .collect(Collectors.toList());
        }

        model.addAttribute("returnOrders", returnOrders);
        return "returns/list";
    }

    
    @GetMapping("/view/{id}")
    public String viewReturnOrder(@PathVariable Long id, Model model) {
        ReturnOrder returnOrder = returnOrderRepository.findById(id).orElse(null);
        model.addAttribute("returnOrder", returnOrder);
        return "returns/view";
    }


}

