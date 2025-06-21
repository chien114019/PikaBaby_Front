package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.UserAccount;
import com.example.demo.repository.UserAccountRepository;

@Service
public class UserAccountService {
	
@Autowired
private UserAccountRepository userAccountRepository;
	
	public List<UserAccount> searchByUsername(String keyword) {
	    return userAccountRepository.findByUsernameContainingIgnoreCase(keyword);
	}
}
