package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.AccountsPayable;
import com.example.demo.model.PurchaseOrder;

public interface AccountsPayableRepository extends JpaRepository<AccountsPayable, Long> {
	 //透過關聯的進貨單查應付帳款
    AccountsPayable findByPurchaseOrder(PurchaseOrder purchaseOrder);
    
    
}
