package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.service.*;

import jakarta.servlet.http.HttpSession;

import com.example.demo.repository.*;
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




@CrossOrigin(originPatterns = "*", allowCredentials = "true")
@Controller
@RequestMapping("/orders")
public class SalesOrderController {

    @Autowired private CustomerService customerService;
    @Autowired private ProductService productService;
    @Autowired private SalesOrderService orderService;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private AddressService addressService;
    @Autowired private ShippingOrderRepository shippingOrderRepository;

    
    @GetMapping
    public String listOrders(Model model) {
        List<SalesOrder> orders = orderService.listAll();
        model.addAttribute("orders", orders);
        

        // 查詢已有出貨單的 SalesOrder ID
        List<ShippingOrder> shippingOrders = shippingOrderRepository.findAll();
        List<Integer> shippedSalesOrderIds = shippingOrders.stream()
                .map(o -> o.getSalesOrder().getId())
                .toList();

        model.addAttribute("shippedSalesOrderIds", shippedSalesOrderIds); // ✅ 傳入前端用來判斷狀態
        return "order/list"; 
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("order", new SalesOrder());
        model.addAttribute("customers", customerService.listAll());
        model.addAttribute("products", productService.listAll());
        return "order/form";
    }
    
    @PostMapping("/delete/{id}")
    public String deleteOrder(@PathVariable Integer id) {
        orderService.deleteById(id);
        return "redirect:/orders";
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
            detail.setUnitPrice(product.getPrice()); // 現在會從SupplierProduct獲取價格
            detailList.add(detail);
        }

        order.setDetails(detailList);
        orderService.save(order);
        return "redirect:/orders";
    }

//   ========== 前台API============
    @GetMapping("/front/search/cust")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getOrdersByCustId(HttpSession session) {
    	String custId = session.getAttribute("customerId").toString();
    	System.out.println("sessionId: " + session.getId());
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

    @GetMapping("/api/{id:\\\\d+}")
    @ResponseBody
    public ResponseEntity<?> getOrderApi(@PathVariable String id) {
        try {
            Map<String, Object> orderData = orderService.getOrdersByCustId(id);
            if (orderData == null || orderData.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(orderData);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to get order: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    // 新增：根據訂單ID查詢單一訂單詳情
    @GetMapping("/api/order/{orderId}")
    @ResponseBody
    public ResponseEntity<?> getOrderById(@PathVariable Integer orderId) {
        try {
            SalesOrder order = orderService.getById(orderId);
            if (order == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "訂單不存在"
                ));
            }
            
            // 構建訂單詳情資料
            Map<String, Object> orderData = new HashMap<>();
            orderData.put("id", order.getId());
            orderData.put("orderDate", order.getOrderDate());
            orderData.put("status", order.getStatus());
            orderData.put("payStatus", order.getPayStatus());
            orderData.put("totalAmount", order.getTotalAmount());
            
                                     // 客戶資訊（優先使用直接儲存的欄位）
            String customerName = order.getRecipientName();
            String customerPhone = order.getRecipientPhone();
            String customerEmail = order.getRecipientEmail();
            String customerAddress = order.getShippingAddress();
            String paymentMethod = order.getPaymentMethod();
            
            // 如果直接儲存的欄位為空，則從客戶關聯取得
            if (customerName == null && order.getCustomer() != null) {
                customerName = order.getCustomer().getName();
            }
            if (customerPhone == null && order.getCustomer() != null) {
                customerPhone = order.getCustomer().getPhone();
            }
            if (customerEmail == null && order.getCustomer() != null) {
                customerEmail = order.getCustomer().getEmail();
            }
            if (customerAddress == null && order.getCustomer() != null) {
            	CustomerAddress address = addressService.getDeliverAddress(order.getCustomer());
                customerAddress = address.getCity() + address.getDistrict() + address.getStreet();
            }
            
            orderData.put("customerName", customerName != null ? customerName : "未提供");
            orderData.put("customerPhone", customerPhone != null ? customerPhone : "未提供");
            orderData.put("customerEmail", customerEmail != null ? customerEmail : "未提供");
            orderData.put("customerAddress", customerAddress != null ? customerAddress : "未提供");
            orderData.put("paymentMethod", paymentMethod != null ? paymentMethod : "未指定");
            
            // 訂單商品詳情
            List<Map<String, Object>> items = new ArrayList<>();
            if (order.getDetails() != null) {
                for (SalesOrderDetail detail : order.getDetails()) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("productId", detail.getProduct().getId());
                    item.put("productName", detail.getProduct().getName());
                    item.put("quantity", detail.getQuantity());
                    item.put("unitPrice", detail.getUnitPrice());
                    item.put("subTotal", detail.getSubTotal());
                    items.add(item);
                }
            }
            orderData.put("items", items);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "order", orderData
            ));
            
        } catch (Exception e) {
            System.err.println("查詢訂單時發生錯誤: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "查詢訂單失敗: " + e.getMessage()
            ));
        }
    }
 
    
    // 購物車提交訂單API（重構版 - 只負責HTTP層處理）
    @PostMapping(value = "/api/cart", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> submitCartOrder(@RequestBody Map<String, Object> orderData) {
        try {
            System.out.println("🚀🚀🚀 === 收到購物車訂單請求 === 🚀🚀🚀");
            System.out.println("📋 訂單資料: " + orderData);
            System.out.println("📋 請求時間: " + new java.util.Date());
            
            // 呼叫Service處理業務邏輯
            Map<String, Object> result = orderService.processCartOrder(orderData);
            
            System.out.println("✅✅✅ 訂單處理成功！結果: " + result);
            return ResponseEntity.ok(result);
            
        } catch (IllegalArgumentException e) {
            // 業務邏輯錯誤（如點數不足、庫存不足等）
            System.err.println("❌❌❌ 業務邏輯錯誤: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
            
        } catch (Exception e) {
            // 系統錯誤
            System.err.println("❌❌❌ 系統錯誤: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "訂單創建失敗: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    // 獲取會員點數API（重構版 - 只負責HTTP層處理）
    @GetMapping("/api/member/points/{customerId}")
    @ResponseBody
    public ResponseEntity<?> getMemberPoints(@PathVariable Integer customerId) {
        try {
            Customer customer = customerService.getCustomerWithPoints(customerId);
            if (customer == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "找不到會員資料"
                ));
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("customerId", customerId);
            response.put("customerName", customer.getName());
            response.put("points", customer.getPoints() != null ? customer.getPoints() : 0);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "獲取會員點數失敗: " + e.getMessage()
            ));
        }
    }
    
    // 根據會員名稱或email獲取點數API（重構版 - 只負責HTTP層處理）
    @GetMapping("/api/member/points/search")
    @ResponseBody
    public ResponseEntity<?> getMemberPointsByNameOrEmail(@RequestParam(required = false) String name, 
                                                          @RequestParam(required = false) String email) {
        try {
            Customer customer = customerService.findCustomerByNameOrEmail(name, email);
            
            if (customer == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "找不到會員資料",
                    "points", 0
                ));
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("customerId", customer.getId());
            response.put("customerName", customer.getName());
            response.put("customerEmail", customer.getEmail());
            response.put("points", customer.getPoints() != null ? customer.getPoints() : 0);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "獲取會員點數失敗: " + e.getMessage(),
                "points", 0
            ));
        }
    }
    
    // 除錯API：檢查商品庫存
    @GetMapping("/api/debug/stock/{productId}")
    @ResponseBody
    public ResponseEntity<?> debugProductStock(@PathVariable Integer productId) {
        try {
            
            
            // 獲取商品資訊
            Product product = productService.getById(productId);
            if (product == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "找不到商品ID: " + productId
                ));
            }
            
            // 獲取計算庫存
            Long calculatedStock = productService.getCurrentCalculatedStock(productId);
            
            // 獲取資料庫庫存
            Long dbStock = product.getStock() != null ? product.getStock() : 0L;
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("productId", productId);
            response.put("productName", product.getName());
            response.put("calculatedStock", calculatedStock);
            response.put("databaseStock", dbStock);
            response.put("message", "庫存查詢成功");
            
            
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "庫存查詢失敗: " + e.getMessage()
            ));
        }
    }
}
