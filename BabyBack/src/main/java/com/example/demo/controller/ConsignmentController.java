package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/secondhand")
public class ConsignmentController {
	@GetMapping("/consignments")
	public String getConsignments() {
		return "secondhand/consignments";
	}
	

}
