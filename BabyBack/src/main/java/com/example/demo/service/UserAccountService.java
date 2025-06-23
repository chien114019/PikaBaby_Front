package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.model.UserAccount;
import com.example.demo.repository.UserAccountRepository;

@Service
public class UserAccountService {
	
@Autowired
private UserAccountRepository userAccountRepository;


//在SecurityConfig定義
@Autowired
private PasswordEncoder passwordEncoder;

	//關鍵字查詢查詢使用者帳號
	public List<UserAccount> searchByUsername(String keyword) {
	    return userAccountRepository.findByUsernameContainingIgnoreCase(keyword);
	}
	
	// 儲存或更新帳號
    public void save(UserAccount userAccount) {
        if (userAccount.getId() == null) {
            // 新增帳號：加密密碼
            String encodedPwd = passwordEncoder.encode(userAccount.getPassword());
            userAccount.setPassword(encodedPwd);
        } else {
            // 編輯帳號：檢查是否有更改密碼
            UserAccount original = userAccountRepository.findById(userAccount.getId()).orElse(null);
            if (original != null) {
                // 如果使用者改了密碼（且不是空的），就加密後覆蓋；否則保留原密碼
                String newPwd = userAccount.getPassword();
                if (newPwd != null && !newPwd.isEmpty() && 
                    !passwordEncoder.matches(newPwd, original.getPassword())) {
                    userAccount.setPassword(passwordEncoder.encode(newPwd));
                } else {
                    userAccount.setPassword(original.getPassword());
                }
            }
        }

        // 若 enabled 沒填，預設為 true
        if (userAccount.getEnabled() == null) {
            userAccount.setEnabled(true);
        }

        userAccountRepository.save(userAccount);
    }

	
}
