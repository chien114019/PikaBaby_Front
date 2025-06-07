package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.model.Consignment;
import com.example.demo.service.ConsignmentService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@Controller
@RequestMapping("/secondhand")
public class ConsignmentController {
	
	@Autowired
	private ConsignmentService service;
	
	@GetMapping("/consignments")
	public String getConsignments(Model model) {
		model.addAttribute("consignments", service.getAll());
		return "secondhand/consignments";
	}
	

}
