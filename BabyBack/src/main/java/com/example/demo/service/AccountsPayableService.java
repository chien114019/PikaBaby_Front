package com.example.demo.service;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.AccountsPayable;
import com.example.demo.model.PurchaseOrder;
import com.example.demo.repository.AccountsPayableRepository;
import com.example.demo.repository.PurchaseOrderRepository;

@Service
public class AccountsPayableService {
	@Autowired
	private PurchaseOrderRepository purchaseOrderRepo;
	
	@Autowired
	private AccountsPayableRepository accountsPayableRepo;
	
	
	public void save(PurchaseOrder order) {
		purchaseOrderRepo.save(order);
		// 計算總金額
        BigDecimal totalAmount = order.getDetails().stream()
            .map(d -> d.getUnitPrice().multiply(BigDecimal.valueOf(d.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 建立應付帳款
        AccountsPayable payable = new AccountsPayable();
        payable.setPurchaseOrder(order);
        payable.setPayableDate(new Date()); // 或根據設定的付款日邏輯
        payable.setAmount(totalAmount);
        payable.setStatus("未付款");

        accountsPayableRepo.save(payable);
    }
	
}
