package com.example.demo.repository;

import com.example.demo.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Integer> {
    Optional<UserAccount> findByUsername(String username);
    
    //關鍵字查詢員工帳號
    List<UserAccount> findByUsernameContainingIgnoreCase(String keyword);
    //Containing 模糊比對（like '%keyword%'）;IgnoreCase 讓搜尋不區分大小寫
    
    //Query Method Name Derivation（方法名稱導出查詢）
    //          方法	                            對應 SQL 範例
    //findByUsername(String s)	            WHERE username = ?
    //findByUsernameContaining(String k)	WHERE username LIKE %?%       //Containing包含
    //findByRoleAndEnabled(...)	            WHERE role = ? AND enabled = ?
}
