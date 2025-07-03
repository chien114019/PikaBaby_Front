package com.example.demo.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Customer;
import com.example.demo.model.Product;
import com.example.demo.model.Receivable;
import com.example.demo.model.Response;
import com.example.demo.model.SalesOrder;
import com.example.demo.model.SalesOrderDetail;
import com.example.demo.model.ShippingOrder;
import com.example.demo.model.ShippingOrderDetail;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.ReceivableRepository;
import com.example.demo.repository.SalesOrderRepository;
import com.example.demo.repository.ShippingOrderDetailRepository;
import com.example.demo.repository.ShippingOrderRepository;
import com.example.demo.repository.SalesOrderDetailRepository;

@Service
public class SalesOrderService {

    @Autowired
    private SalesOrderRepository repository;
    
    @Autowired
    private SalesOrderDetailRepository detailRepository;
    
    @Autowired
    private SalesOrderRepository salesOrderRepository;
    
    @Autowired
    private ReceivableRepository receivableRepository;

    @Autowired
    private CustomerRepository cRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CustomerService customerService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private ShippingOrderRepository shippingOrderRepository ;
    
    @Autowired
    private ShippingOrderDetailRepository shippingOrderDetailRepository;
    
    @Autowired
    private OrderStatusService orderStatusService;
    
    @Transactional
    public void updatePayStatus(Integer orderId, Integer payStatus) {
        SalesOrder order = getById(orderId);
        if (order != null) {
            order.setPayStatus(payStatus);
            repository.save(order);
            System.out.println("✅ 訂單 " + orderId + " 付款狀態已更新為：" + payStatus);
        } else {
            System.err.println("❌ 找不到訂單 ID：" + orderId + "，無法更新付款狀態");
            throw new IllegalArgumentException("找不到訂單 ID：" + orderId);
        }
    }


    @Transactional
    public Integer save(SalesOrder order) {
        System.out.println("=== 開始保存訂單 ===");
        System.out.println("訂單包含商品數量: " + (order.getDetails() != null ? order.getDetails().size() : 0));
        
        // 確保每個 SalesOrderDetail 都有正確的 order 關聯
        if (order.getDetails() != null && !order.getDetails().isEmpty()) {
            for (SalesOrderDetail detail : order.getDetails()) {
                detail.setOrder(order);
                System.out.println("設定訂單詳情 - 商品: " + detail.getProduct().getName() + 
                                  ", 數量: " + detail.getQuantity() + 
                                  ", 單價: " + detail.getUnitPrice());
            }
        }
        
        // 先儲存訂單以獲取ID
        SalesOrder savedOrder = repository.save(order);
        System.out.println("✅ 訂單主檔已保存，ID: " + savedOrder.getId());
        
        // 明確保存每個 SalesOrderDetail
        if (savedOrder.getDetails() != null && !savedOrder.getDetails().isEmpty()) {
            for (SalesOrderDetail detail : savedOrder.getDetails()) {
                detail.setOrder(savedOrder); // 確保關聯正確
                SalesOrderDetail savedDetail = detailRepository.save(detail);
                System.out.println("✅ 訂單詳情已保存，ID: " + savedDetail.getId() + 
                                  ", 商品ID: " + savedDetail.getProduct().getId() +
                                  ", 商品名稱: " + savedDetail.getProduct().getName() +
                                  ", 數量: " + savedDetail.getQuantity());
                
                // 立即檢查保存後的庫存變化
                Long newStock = getCurrentStock(savedDetail.getProduct().getId());
                System.out.println("📊 保存後商品 " + savedDetail.getProduct().getName() + " 的庫存: " + newStock);
            }
        }
        
        // 扣減庫存並更新商品庫存
        if (savedOrder.getDetails() != null && !savedOrder.getDetails().isEmpty()) {
            for (SalesOrderDetail detail : savedOrder.getDetails()) {
                Product product = detail.getProduct();
                Long orderQuantity = detail.getQuantity();
                
                System.out.println("準備扣減庫存：商品 " + product.getName() + " (ID: " + product.getId() + ")");
                // 使用計算庫存檢查
                Long currentStock = getCurrentStock(product.getId());
                System.out.println("訂購數量: " + orderQuantity + ", 目前庫存: " + currentStock);
                
                // 檢查庫存但不直接扣減（庫存扣減通過SalesOrderDetail的存在來體現）
                if (currentStock >= orderQuantity) {
                    System.out.println("✅ 商品 " + product.getName() + " 庫存檢查通過: " + orderQuantity + "，當前庫存: " + currentStock);
                } else {
                    System.err.println("❌ 警告：商品 " + product.getName() + " 庫存不足，無法扣減。目前庫存: " + currentStock + ", 需要: " + orderQuantity);
                }
            }
        }
        
        // 建立出貨單
        ShippingOrder shippingOrder = new ShippingOrder();
        shippingOrder.setSalesOrder(savedOrder);
        shippingOrder.setShippingDate(LocalDate.now());
        shippingOrder.setStatus("待出貨");
        ShippingOrder newData = shippingOrderRepository.save(shippingOrder);
        
        System.out.println("New Shipping: " + newData.getStatus());
        

	     // 建立出貨明細
	     for (SalesOrderDetail detail : savedOrder.getDetails()) {
	         ShippingOrderDetail shippingDetail = new ShippingOrderDetail();
	         shippingDetail.setShippingOrder(shippingOrder);
	         shippingDetail.setProduct(detail.getProduct());
	         shippingDetail.setQuantity(detail.getQuantity());
	         shippingOrderDetailRepository.save(shippingDetail);
	
	         System.out.println("📦 建立出貨明細 - 商品：" + detail.getProduct().getName() +
	                            "，數量：" + detail.getQuantity());
	     }
	
	     System.out.println("✅ 出貨單建立完成，ID: " + shippingOrder.getId());

        // 建立應收帳款資料
        Receivable r = new Receivable();
        r.setOrder(savedOrder);
        r.setCustomer(savedOrder.getCustomer());
        r.setAmount(savedOrder.getTotalAmount());
        r.setStatus("未收款");
        
        receivableRepository.save(r);
        
        System.out.println("✅ 訂單完整保存成功，ID: " + savedOrder.getId() + "，總金額: " + savedOrder.getTotalAmount());
        System.out.println("=== 訂單保存完成 ===");
        
        return savedOrder.getId();
    }

    public List<SalesOrder> listAll() {
        return repository.findAll();
    }
    
    public SalesOrder getById(Integer id) {
        return repository.findById(id).orElse(null);
    }
    
    public void deleteById(Integer id) {
        repository.deleteById(id);
    }
    
    public Map<String, Object> getOrdersByCustId(String custId) {
    	/*
    	 * {
    	 * 		orders: [
    	 * 			{
    	 * 				id: "",
    	 * 				date: "",
    	 * 				status: "",
    	 * 				payStatus: "",
    	 * 				total: ""
    	 * 			}.....	
    	 * 		],
    	 * 		response: {
    	 * 			success: "",
    	 * 			mesg: ""
    	 * 		}
    	 * }
    	 */
    	
    	Map<String, Object> returnMap = new HashMap<String, Object>();
    	List<Map<String, Object>> orderList = new ArrayList();
    	Response response = new Response();

    	Customer cust = cRepository.findById((int) Long.parseLong(custId)).orElse(null);
    	
    	if (cust != null) {
    		List<SalesOrder> orders = repository.findAllByCustomer(cust);
    		for (SalesOrder order : orders) {
				Map<String, Object> map = new HashMap();
				map.put("id", order.getId());
				map.put("date", order.getOrderDate());
				map.put("status", order.getStatus());
				map.put("payStatus", order.getPayStatus());
				map.put("total", order.getTotalAmount());
				orderList.add(map);
			}
    		
    		response.setSuccess(true);
    		response.setMesg("查詢成功");
    		
		} else {    		
    		response.setSuccess(false);
    		response.setMesg("查無此顧客");
		}
		
    	returnMap.put("orders", orderList);
		returnMap.put("response", response);
    	return returnMap;
	}
    
    public Response cancelOrderById(String id) {
		Response response = new Response();
    	SalesOrder target = repository.findById((int) Long.parseLong(id)).orElse(null);
		if (target != null) {
			target.setStatus(-1);
			target.setPayStatus(1);
			repository.save(target);
			response.setSuccess(true);
			response.setMesg("取消成功");
		} else {
			response.setSuccess(false);
			response.setMesg("查無此訂單");
		}
    	return response;
	}
    
    // ===== 前台訂單相關業務邏輯 =====
    
    /**
     * 處理前台購物車訂單（包含點數處理）
     */
    @Transactional
    public Map<String, Object> processCartOrder(Map<String, Object> orderData) throws Exception {
        System.out.println("🛒🛒🛒 === 開始處理購物車訂單 === 🛒🛒🛒");
        
        // 獲取訂單基本資料
        String customerName = (String) orderData.get("name");
        String phone = (String) orderData.get("phone");
        String email = (String) orderData.get("email");
        String address = (String) orderData.get("address");
        String paymentMethod = (String) orderData.get("paymentMethod");
        
        System.out.println("👤 客戶資料 - 姓名: " + customerName + ", 電話: " + phone + ", 地址: " + address);
        
        // 獲取點數使用資料
        Integer pointsUsed = 0;
        if (orderData.get("pointsUsed") != null) {
            pointsUsed = ((Number) orderData.get("pointsUsed")).intValue();
        }
        
        // 驗證必要欄位
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new IllegalArgumentException("客戶姓名不能為空");
        }
        
        // 獲取或創建客戶
//        Customer customer = customerService.getOrCreateCustomer(customerName, phone, email, address);
        Customer customer = customerService.findByEmail(email).orElse(null);
        
        // 驗證點數
        if (pointsUsed > 0 && !customerService.validatePointsUsage(customer, pointsUsed)) {
            Integer currentPoints = customer.getPoints() != null ? customer.getPoints() : 0;
            throw new IllegalArgumentException("點數不足！您目前有 " + currentPoints + " 點，但要使用 " + pointsUsed + " 點");
        }
        
        // 處理訂單商品
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> items = (List<Map<String, Object>>) orderData.get("items");
        
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("購物車是空的");
        }
        
        // 創建銷售訂單
        SalesOrder order = new SalesOrder();
        String orderNumber = "ORDER" + System.currentTimeMillis();
        order.setOrderNumber(orderNumber);
        order.setCustomer(customer);
        order.setOrderDate(new Date());
        order.setRecipientName(customerName);
        order.setRecipientPhone(phone);
        order.setRecipientEmail(email);
        order.setShippingAddress(address);
        order.setPaymentMethod(paymentMethod);
        
        List<SalesOrderDetail> detailList = new ArrayList<>();
        double totalAmount = 0;
        
        // 處理每個商品
        for (Map<String, Object> item : items) {
            SalesOrderDetail detail = processOrderItem(item, order);
            detailList.add(detail);
            totalAmount += detail.getUnitPrice() * detail.getQuantity();
        }
        
        order.setDetails(detailList);
        
        // 保存訂單
        Integer orderIdInt = save(order);
        
        // 處理點數交易
        if (pointsUsed > 0) {
            customerService.deductPoints(customer, pointsUsed);
        }
        
        // 計算並給予購物回饋點數
        Integer earnedPoints = customerService.calculateEarnedPoints(totalAmount);
        if (earnedPoints > 0) {
            customerService.addPoints(customer, earnedPoints);
        }
        
        Map<String, Object> result = new HashMap<>();

        try {
        	System.out.println("更新訂單狀態");        	
            orderStatusService.updatePayStatus(orderIdInt, 0); // 0 表示已付款
        	
            // 返回結果
            result.put("success", true);
            result.put("message", "訂單創建成功");
            result.put("orderId", order.getId());
            result.put("totalAmount", totalAmount);
            result.put("pointsUsed", pointsUsed);
            result.put("pointsEarned", earnedPoints);
            result.put("remainingPoints", customer.getPoints());
            result.put("customerId", customer.getId());

        } catch (Exception ex) {
            System.err.println("❌ 更新付款狀態時發生錯誤：" + ex.getMessage());
            ex.printStackTrace();
            result.put("success", false);
            result.put("message", "訂單創建失敗");
        }
        
        
        return result;
    }
    
    /**
     * 處理單個訂單商品
     */
    private SalesOrderDetail processOrderItem(Map<String, Object> item, SalesOrder order) throws Exception {
        // 安全的類型轉換
        Integer productId = convertToInteger(item.get("productId"));
        Long quantity = convertToLong(item.get("quantity"));
        Double price = convertToDouble(item.get("price"));
        
        // 驗證數據
        if (productId == null) {
            throw new IllegalArgumentException("商品ID不能為空");
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("商品數量無效");
        }
        if (price == null || price < 0) {
            throw new IllegalArgumentException("商品價格無效");
        }
        
        // 獲取商品
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            throw new IllegalArgumentException("找不到商品ID: " + productId);
        }
        
        // 檢查庫存
        Long stock = getCurrentStock(product.getId());
        if (quantity > stock) {
            throw new IllegalArgumentException("商品「" + product.getName() + "」庫存不足，剩餘：" + stock);
        }
        
        // 創建訂單詳情
        SalesOrderDetail detail = new SalesOrderDetail();
        detail.setOrder(order);
        detail.setProduct(product);
        detail.setQuantity(quantity);
        detail.setUnitPrice(price);
        
        return detail;
    }
    
    /**
     * 獲取當前庫存（使用動態計算）
     */
    private Long getCurrentStock(Integer productId) {
        return productService.getCurrentCalculatedStock(productId);
    }
    
    // 類型轉換輔助方法
    private Integer convertToInteger(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Integer) return (Integer) obj;
        if (obj instanceof Number) return ((Number) obj).intValue();
        return null;
    }
    
    private Long convertToLong(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Long) return (Long) obj;
        if (obj instanceof Integer) return ((Integer) obj).longValue();
        if (obj instanceof Number) return ((Number) obj).longValue();
        return null;
    }
    
    private Double convertToDouble(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Double) return (Double) obj;
        if (obj instanceof Integer) return ((Integer) obj).doubleValue();
        if (obj instanceof Number) return ((Number) obj).doubleValue();
        return null;
    }
    
    public BigDecimal findAmountByOrderId(String orderId) {
        return salesOrderRepository.findByOrderNumber(orderId)
            .map(order -> BigDecimal.valueOf(order.getTotalAmount()))
            .orElseThrow(() -> new IllegalArgumentException("查無訂單金額"));
    }


}
