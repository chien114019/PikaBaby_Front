package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.SalesOrder;
import com.example.demo.model.SalesOrderDetail;
import com.example.demo.model.Customer;
import com.example.demo.model.Product;
import com.example.demo.service.SalesOrderService;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500"}, allowedHeaders = "*")
public class OrderApiController {

    @Autowired
    private SalesOrderService salesOrderService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @PostMapping("/orders")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> orderData) {
        try {
            // 1. 創建客戶資料
            Customer customer = new Customer();
            customer.setName((String) orderData.get("name"));
            customer.setPhone((String) orderData.get("phone"));
            customer.setEmail((String) orderData.get("email"));
            customer.setAddress((String) orderData.get("address"));
            customerRepository.save(customer);

            // 2. 創建訂單
            SalesOrder order = new SalesOrder();
            order.setCustomer(customer);
            order.setOrderDate(new Date());
            order.setStatus(0);  // 設置訂單狀態為已成立
            order.setPayStatus(0);  // 設置支付狀態為未付款
            
            // 3. 處理訂單明細
            ArrayList<Map<String, Object>> items = (ArrayList<Map<String, Object>>) orderData.get("items");
            ArrayList<SalesOrderDetail> details = new ArrayList<>();
            
            for (Map<String, Object> item : items) {
                SalesOrderDetail detail = new SalesOrderDetail();
                // 這裡假設前端傳來的商品資料中有商品ID，如果沒有的話需要用商品名稱查詢
                Product product = productRepository.findById(Long.parseLong(item.get("productId").toString()))
                    .orElseThrow(() -> new RuntimeException("Product not found"));
                
                detail.setProduct(product);
                detail.setQuantity(Long.parseLong(item.get("quantity").toString()));
                detail.setUnitPrice(BigDecimal.valueOf(Double.parseDouble(item.get("price").toString())));
                detail.setOrder(order);
                details.add(detail);
            }
            
            order.setDetails(details);
            
            // 4. 保存訂單
            salesOrderService.save(order);
            
            // 5. 返回成功響應
            Map<String, Object> response = new HashMap<>();
            response.put("orderId", order.getId());
            response.put("message", "Order created successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to create order: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<?> getOrder(@PathVariable Long id) {
        try {
            SalesOrder order = salesOrderService.getById(id);
            if (order == null) {
                return ResponseEntity.notFound().build();
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", order.getId());
            response.put("orderDate", order.getOrderDate());
            response.put("name", order.getCustomer().getName());
            response.put("phone", order.getCustomer().getPhone());
            response.put("email", order.getCustomer().getEmail());
            response.put("address", order.getCustomer().getAddress());
            response.put("totalAmount", order.getTotalAmount());
            
            List<Map<String, Object>> items = new ArrayList<>();
            for (SalesOrderDetail detail : order.getDetails()) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", detail.getProduct().getName());
                item.put("quantity", detail.getQuantity());
                item.put("price", detail.getUnitPrice());
                items.add(item);
            }
            response.put("items", items);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to get order: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
} 