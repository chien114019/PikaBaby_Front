package com.example.demo.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Customer;
import com.example.demo.model.Receivable;
import com.example.demo.model.SalesOrder;

//繼承自 JpaRepository
//操作的是 Receivable 類別對應的資料表，而主鍵是 Long 型別
public interface ReceivableRepository extends JpaRepository<Receivable, Integer> {
	
	//0609喬新增 
	//不論如何都要輸入關鍵字查詢
	
	//查詢**客戶名稱中包含某個關鍵字（不分大小寫）**的所有 Receivable 資料
    List<Receivable> findByCustomerNameContainingIgnoreCase(String keyword);
    //同時符合兩個條件：CustomerName 包含 keyword（不分大小寫）& CreatedAt 這欄的時間在 start 和 end 之間
    List<Receivable> findByCustomerNameContainingIgnoreCaseAndCreatedAtBetween(String keyword, Timestamp start, Timestamp end);
    //條件為：CustomerName 包含 keyword（不分大小寫）& CreatedAt 晚於 start 時間
    List<Receivable> findByCustomerNameContainingIgnoreCaseAndCreatedAtAfter(String keyword, Timestamp start);
    //條件為：CustomerName 包含 keyword & CreatedAt 早於 end 時間
    List<Receivable> findByCustomerNameContainingIgnoreCaseAndCreatedAtBefore(String keyword, Timestamp end);
    
    List<Receivable> findAllByCustomer(Customer cust);
    
    Receivable findByOrder(SalesOrder order);
    
}
