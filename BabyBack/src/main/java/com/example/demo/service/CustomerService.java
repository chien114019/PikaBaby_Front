package com.example.demo.service;

import com.example.demo.model.Customer;
import com.example.demo.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository repository;

    public List<Customer> listAll() {
        return repository.findAll();
    }

    public Customer getById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public void save(Customer customer) {
        repository.save(customer);
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }
    
//    註冊抓註冊時間、給點數100點
    public Customer register(Customer Customer) {
        if (repository.findByEmail(Customer.getEmail()).isPresent()) {
            throw new RuntimeException("Email 已被註冊");
        }

        Customer.setCreatedAt(LocalDateTime.now());
        Customer.setPoints(100);

        return repository.save(Customer);
    }
    
//    登入比對
    public Optional<Customer> findByEmail(String email) {
        return repository.findByEmail(email);
    }
    
    public Optional<Customer> findById(Integer id) {
        return repository.findById(id);
    }
   
    
    // ===== 點數相關業務邏輯 =====
    
    /**
     * 根據會員ID獲取點數
     */
    public Customer getCustomerWithPoints(Integer customerId) {
        return repository.findById(customerId).orElse(null);
    }
    
    /**
     * 根據姓名或Email查詢會員點數
     */
    public Customer findCustomerByNameOrEmail(String name, String email) {
        Customer customer = null;
        
        // 優先用email查詢
        if (email != null && !email.trim().isEmpty()) {
            customer = findByEmail(email).orElse(null);
        }
        
        // 如果email查不到，再用name查詢
        if (customer == null && name != null && !name.trim().isEmpty()) {
            List<Customer> customers = repository.findByName(name);
            if (customers != null && !customers.isEmpty()) {
                customer = customers.get(0); // 取第一個找到的
            }
        }
        
        return customer;
    }
    
    /**
     * 驗證點數是否足夠使用
     */
    public boolean validatePointsUsage(Customer customer, Integer pointsToUse) {
        if (pointsToUse == null || pointsToUse <= 0) {
            return true; // 不使用點數，驗證通過
        }
        
        Integer currentPoints = customer.getPoints() != null ? customer.getPoints() : 0;
        return currentPoints >= pointsToUse;
    }
    
    /**
     * 扣除會員點數
     */
    public void deductPoints(Customer customer, Integer pointsToDeduct) {
        if (pointsToDeduct == null || pointsToDeduct <= 0) {
            return;
        }
        
        Integer currentPoints = customer.getPoints() != null ? customer.getPoints() : 0;
        if (currentPoints >= pointsToDeduct) {
            customer.setPoints(currentPoints - pointsToDeduct);
            repository.save(customer);
        } else {
            throw new RuntimeException("點數不足！目前有 " + currentPoints + " 點，但要使用 " + pointsToDeduct + " 點");
        }
    }
    
    /**
     * 增加會員點數（購物回饋）
     */
    public void addPoints(Customer customer, Integer pointsToAdd) {
        if (pointsToAdd == null || pointsToAdd <= 0) {
            return;
        }
        
        Integer currentPoints = customer.getPoints() != null ? customer.getPoints() : 0;
        customer.setPoints(currentPoints + pointsToAdd);
        repository.save(customer);
    }
    
    /**
     * 計算購物回饋點數（每消費100元得1點）
     */
    public Integer calculateEarnedPoints(double totalAmount) {
        return (int) Math.floor(totalAmount / 100);
    }
    
    /**
     * 獲取或創建客戶（用於前台訂單）
     */
    public Customer getOrCreateCustomer(String customerName, String phone, String email, String address) {
        List<Customer> existingCustomers = repository.findByName(customerName);
        Customer customer;
        
        if (existingCustomers != null && !existingCustomers.isEmpty()) {
            // 使用第一個找到的客戶，但更新其資訊
            customer = existingCustomers.get(0);
            customer.setPhone(phone);
            customer.setEmail(email);
            customer.setAddress(address);
            repository.save(customer);
        } else {
            // 創建新客戶
            customer = new Customer();
            customer.setName(customerName);
            customer.setPhone(phone);
            customer.setEmail(email);
            customer.setAddress(address);
            customer.setPoints(100); // 新會員預設100點
            repository.save(customer);
        }
        
        return customer;
    }
}
