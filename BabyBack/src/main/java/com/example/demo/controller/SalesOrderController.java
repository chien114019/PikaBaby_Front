package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;




@CrossOrigin(origins = "*")
@Controller
@RequestMapping("/orders")
public class SalesOrderController {

    @Autowired private CustomerService customerService;
    @Autowired private ProductService productService;
    @Autowired private SalesOrderService orderService;
    
    @GetMapping
    public String listOrders(Model model) {
        List<SalesOrder> orders = orderService.listAll();
        model.addAttribute("orders", orders);
        return "order/list"; 
    }

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
                            @RequestParam("quantities") Long[] quantities,
                            Model model) {

        Customer customer = customerService.getById(customerId);

        List<Product> productList = new ArrayList<>();
        for (int i = 0; i < productIds.length; i++) {
            Product product = productService.getById(productIds[i]);
            
            //0611喬註解
           // if (quantities[i] > product.getStock()) {
            	
                //0611喬註解
            	// 改用計算方式取得實際庫存
                Long stock = productService.getCurrentStock(product.getId());

                if (quantities[i] > stock) {
                // 錯誤情況處理：顯示錯誤並回表單
                model.addAttribute("order", new SalesOrder());
                model.addAttribute("customers", customerService.listAll());
                //0611喬更改
                //model.addAttribute("products", productService.listAll());
                model.addAttribute("products", productService.getAllProductsWithStock());
                
                //0611喬更改
//                model.addAttribute("errorMessage", "商品「" + product.getName() + "」庫存不足，剩餘：" + product.getStock());
                model.addAttribute("errorMessage", "商品「" + product.getName() + "」庫存不足，剩餘：" + stock);

                
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
            
            //0609喬更新
            //product.setStock(product.getStock() - quantities[i]);
            //productService.save(product); // 更新庫存

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

//   ========== 前台API============
    @GetMapping("/front/search/cust/{custId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getOrdersByCustId(@PathVariable String custId) {
    	return ResponseEntity.ok(orderService.getOrdersByCustId(custId));
    }
    
    @PutMapping("/front/cancel/{id}")
    @ResponseBody
    public ResponseEntity<Response> cancelOrderById(@PathVariable String id) {
    	Response response = orderService.cancelOrderById(id);
    	if (response.getSuccess()) {
			response.setMesg("訂單編號 " + id + " " + response.getMesg());
		} else {
			response.setMesg("訂單編號 " + id + " " + response.getMesg() + "，取消失敗");
		}
        return ResponseEntity.ok(response);
    }

}
