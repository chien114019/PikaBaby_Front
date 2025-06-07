package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.service.WithdrawalService;



@Controller
@RequestMapping("/secondhand")
public class WithdrawalController {
	
	@Autowired
	private WithdrawalService service;
	
	@GetMapping("/withdrawals")
	public String getWithdrawal(Model model) {
		model.addAttribute("withdraws", service.getAll());
		return "secondhand/withdrawals";
	}
	

}
