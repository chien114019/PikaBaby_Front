package com.example.demo.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        // 自動產生單號
        if (order.getOrderNumber() == null || order.getOrderNumber().isEmpty()) {
            order.setOrderNumber(generateOrderNumber());
        }

        // 儲存進貨單（先儲存才會有 order.id）
        purchaseOrderRepo.save(order);

        // 計算總金額
        BigDecimal totalAmount = order.getDetails().stream()
        	.filter(d -> d.getUnitPrice() != null && d.getQuantity() != null)
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

    @Transactional
    public void deleteById(Integer id) {
        PurchaseOrder order = purchaseOrderRepo.findById(id).orElse(null);
        if (order != null) {
            // 先刪除應付帳款
            AccountsPayable payable = accountsPayableRepo.findByPurchaseOrder(order);
            if (payable != null) {
                accountsPayableRepo.delete(payable);
            }

            // 先清空明細才能刪主表
            order.getDetails().clear();
            purchaseOrderRepo.save(order);

            // 再刪主表
            purchaseOrderRepo.delete(order);
        }
    }


    public String generateOrderNumber() {
        String prefix = "PO"; // PO = Purchase Order
        String datePart = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String randomPart = String.format("%03d", new Random().nextInt(1000));
        return prefix + datePart + randomPart;
    }
    
    public PurchaseOrder getById(Integer id) {
        return purchaseOrderRepo.findById(id).orElse(null);
    }

}
