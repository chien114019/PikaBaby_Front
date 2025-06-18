package com.example.demo.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Customer;
import com.example.demo.model.Receivable;
import com.example.demo.model.Response;
import com.example.demo.model.SalesOrder;
import com.example.demo.model.SalesOrderDetail;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.ReceivableRepository;
import com.example.demo.repository.SalesOrderRepository;

@Service
public class SalesOrderService {

    @Autowired
    private SalesOrderRepository repository;
    
    @Autowired
    private ReceivableRepository receivableRepository;

    @Autowired
    private CustomerRepository cRepository;

    public void save(SalesOrder order) {
    	
        repository.save(order);
        
        // 計算總金額
        BigDecimal total = BigDecimal.ZERO;
        for (SalesOrderDetail d : order.getDetails()) {
        	 BigDecimal subtotal = d.getUnitPrice().multiply(BigDecimal.valueOf(d.getQuantity()));
        	 total = total.add(subtotal);
        }
        
        // 建立應收帳款資料
        Receivable r = new Receivable();
        r.setOrder(order);
        r.setCustomer(order.getCustomer());
        r.setAmount(order.getTotalAmount());
        r.setStatus("未收款");
        

        receivableRepository.save(r);
    }

    public List<SalesOrder> listAll() {
        return repository.findAll();
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

    	Customer cust = cRepository.findById(Long.parseLong(custId)).orElse(null);
    	
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
}
