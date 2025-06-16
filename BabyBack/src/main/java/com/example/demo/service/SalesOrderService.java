package com.example.demo.service;

import com.example.demo.model.Receivable;
import com.example.demo.model.SalesOrder;
import com.example.demo.model.SalesOrderDetail;
import com.example.demo.repository.ReceivableRepository;
import com.example.demo.repository.SalesOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SalesOrderService {

    @Autowired
    private SalesOrderRepository repository;
    
    @Autowired
    private ReceivableRepository receivableRepository;


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
}
