package com.example.demo.controller;

import java.util.List;

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
	
	List<Consignment> consignments;

	@GetMapping("/consignments")
	public String getConsignments(Model model, @RequestParam(required = false) String type, 
			@RequestParam(required = false) String review, @RequestParam(required = false) String delivery) {
	
		if (type != null && type != "") {
			if (review != null && review != "") {
				if (delivery != null && delivery != "") {
					consignments = service.getAllByProductTypeAndReviewAndDelivery(type, review, delivery);
				
				} else {
					consignments = service.getAllByProductTypeAndReview(type, review);
				}
			} else {
				if (delivery != null && delivery != "") {
					consignments = service.getAllByProductTypeAndDelivery(type, delivery);
					
				} else {
					consignments = service.getAllByProductType(type);
				}				
			}
		} else {
			if (review != null && review != "") {
				if (delivery != null && delivery != "") {
					consignments = service.getAllByReviewAndDelivery(review, delivery);
					
				} else {
					consignments = service.getAllByReview(review);	
				}
			} else {
				if (delivery != null && delivery != "") {
					consignments = service.getAllByDelivery(delivery);
					
				} else {
					consignments = service.getAll();

				}
			}
		}

		model.addAttribute("type", type);
		model.addAttribute("review", review);
		model.addAttribute("delivery", delivery);
		model.addAttribute("consignments", consignments);
		return "secondhand/consignments";
	}
	
	@GetMapping("/consign")
	public ResponseEntity<Consignment> getConsignById(@RequestParam String id) {
		return ResponseEntity.ok(service.getById(id));
	}
	

}
