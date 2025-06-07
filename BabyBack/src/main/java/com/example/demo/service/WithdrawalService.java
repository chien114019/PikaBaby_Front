package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Withdrawal;
import com.example.demo.repository.WithdrawalRepository;

@Service
public class WithdrawalService {

	@Autowired
	private WithdrawalRepository repository;
	
	public List<Withdrawal> getAll() {
		return repository.findAll();
	}
}
