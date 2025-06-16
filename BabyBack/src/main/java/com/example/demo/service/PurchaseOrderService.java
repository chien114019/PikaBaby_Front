package com.example.demo.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.AccountsPayable;
import com.example.demo.model.PurchaseOrder;
import com.example.demo.repository.AccountsPayableRepository;
import com.example.demo.repository.PurchaseOrderRepository;

@Service
public class PurchaseOrderService {
    
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepo;
    
    @Autowired
    private AccountsPayableRepository accountsPayableRepo;

    public void save(PurchaseOrder order) {
        // 儲存進貨單
        purchaseOrderRepo.save(order);

        // 計算總金額
        BigDecimal totalAmount = order.getDetails().stream()
            .map(d -> d.getUnitPrice().multiply(BigDecimal.valueOf(d.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 建立應付帳款
        AccountsPayable payable = new AccountsPayable();
        payable.setPurchaseOrder(order);
        payable.setPayableDate(new Date());
        payable.setAmount(totalAmount);
        payable.setStatus("未付款");
        payable.setSupplier(order.getSupplier());

        accountsPayableRepo.save(payable);
    }

    public void delete(PurchaseOrder order) {
        purchaseOrderRepo.delete(order);
    }

    public List<PurchaseOrder> listAll() {
        return purchaseOrderRepo.findAll();
    }
    
    public void deleteById(Long id) {
        // 先找出進貨單
        PurchaseOrder order = purchaseOrderRepo.findById(id).orElse(null);
        if (order != null) {
            // 刪除對應的應付帳款（若有）
            AccountsPayable payable = accountsPayableRepo.findByPurchaseOrder(order);
            if (payable != null) {
                accountsPayableRepo.delete(payable);
            }

            // 刪除進貨單
            purchaseOrderRepo.delete(order);
        }
    }

    
}
