package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.model.Consignment;
import com.example.demo.model.Response;
import com.example.demo.service.ConsignmentService;

@CrossOrigin(origins = "*")
@Controller
@RequestMapping("/secondhand")
public class ConsignmentController {
	
	@Autowired
	private ConsignmentService service;
	
	List<Consignment> consignments;

//	--------------- 前台 API ----------------
	
//	@GetMapping("/consign/cust/{custId}")
//	@ResponseBody
//	public List<Consignment> getConsignmentsByCustId(@PathVariable String custId) {
//		return service.getAllByCustId(custId);
//	}
	
	
//	--------------- 後台 API -----------------
	
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
	
	@GetMapping("/consign/{id}")
	public ResponseEntity<Consignment> getConsignById(@PathVariable String id) {
		return ResponseEntity.ok(service.getById(id));
	}
	
	@PostMapping("/consign/edit/{id}")
	public ResponseEntity<Response> editConsignment(@PathVariable String id, @RequestBody Map<String, String> body) {
		/* Resquest: { review: "", price: ""} */
		
		String review = body.get("review");
		String price = body.get("price");
		
		Response response = service.editConsignment(id, review, price);		
		return ResponseEntity.ok(response);
	}
	

}
