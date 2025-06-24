package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Supplier;
import com.example.demo.repository.SupplierRepository;

@Service
public class SupplierService {
    @Autowired
    private SupplierRepository repository;

    //功能：回傳資料庫中「所有的供應商」資料
    public List<Supplier> listAll() {
        return repository.findAll();
    }

    //功能：透過供應商 ID 找出一筆資料;例如要編輯某筆供應商，會去 /suppliers/edit/3，這時就會呼叫這個方法
    public Supplier getById(Integer id) {
        return repository.findById(id).orElse(null);
    }
    
    
    //搜尋關鍵字的供應商
    public List<Supplier> searchByName(String keyword) {
        return repository.findByNameContaining(keyword);
    }
    
    //功能：儲存供應商物件。如果是新的會新增（insert），如果已有 ID 就更新（update）
    public void save(Supplier supplier) {
        repository.save(supplier);
    }

    //功能：根據 ID 刪除某筆供應商資料
    public void deleteById(Integer id) {
        Supplier supplier = repository.findById(id)
            .orElseThrow(() -> new IllegalStateException("找不到供應商 ID: " + id));
        supplier.setDeleted(true);  // ✅ 軟刪除
        repository.save(supplier);
    }
    
    public List<Supplier> findAllNotDeleted() {
        return repository.findByDeletedFalse(); // 只撈未刪除資料
    }



}
