package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.model.Consignment;
import com.example.demo.model.Response;
import com.example.demo.model.Withdrawal;
import com.example.demo.service.ConsignmentService;

@CrossOrigin(origins = "*")
@Controller
@RequestMapping("/secondhand")
public class ConsignmentController {
	
	@Autowired
	private ConsignmentService service;
	
	List<Consignment> consignments;

//	--------------- 前台 API ----------------
	
	@GetMapping("/consign/cust/{custId}")
	@ResponseBody
	public List<Consignment> getConsignmentsByCustId(@PathVariable String custId, @RequestParam(required = false) String applyStart,
			@RequestParam(required = false) String applyEnd, @RequestParam(required = false) String type, 
			@RequestParam(required = false) String review) {
		
		List<Consignment> consignments = new ArrayList();
		try {
			if (applyStart != null && applyStart != "") {
				if (applyEnd != null && applyEnd != "") {
					if (type != null && type != "") {
						if (review != null && review != "") {
							consignments = service.getAllByCustIdAndDateAndTypeAndReview(custId, applyStart, applyEnd, type, review);
						} else {
							consignments = service.getAllByCustIdAndDateAndType(custId, applyStart, applyEnd, type);
						}
					} else {
						if (review != null && review != "") {
							consignments = service.getAllByCustIdDateAndReview(custId, applyStart, applyEnd, review);
						} else {
							consignments = service.getAllByCustIdAndDate(custId, applyStart, applyEnd);
						}
					}
				} else {
					if (type != null && type != "") {
						if (review != null && review != "") {
							consignments = service.getAllByCustIdAndApplyStartAndTypeAndReview(custId, applyStart, type, review);
						} else {
							consignments = service.getAllByCustIdAndApplyStartAndType(custId, applyStart, type);
						}
						
					} else {
						if (review != null && review != "") {
							consignments = service.getAllByCustIdAndApplyStartAndReview(custId, applyStart, review);
						} else {
							consignments = service.getAllByCustIdAndApplyStart(custId, applyStart);
						}
					}
				}
			}
			else {
				if (applyEnd != null && applyEnd != "") {
					if (type != null && type != "") {
						if (review != null && review != "") {
							consignments = service.getAllByCustIdAndApplyEndAndTypeAndReview(custId, applyEnd, type, review);
						} else {
							consignments = service.getAllByCustIdAndApplyEndAndType(custId, applyEnd, type);
						}
					} else {
						if (review != null && review != "") {
							consignments = service.getAllByCustIdAndApplyEndAndReview(custId, applyEnd, review);
						} else {
							consignments = service.getAllByCustIdAndApplyEnd(custId, applyEnd);
						}
					}			
				} else {
					if (type != null && type != "") {
						if (review != null && review != "") {
							consignments = service.getAllByCustIdAndTypeAndReview(custId, type, review);
						} else {
							consignments = service.getAllByCustIdAndType(custId, type);
						}
					} else {
						if (review != null && review != "") {
							consignments = service.getAllByCustIdAndReview(custId, review);
						} else {
							consignments = service.getAllByCustId(custId);
						}
					}
				}			
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		
		return consignments;
		
	}
	
	@DeleteMapping("/consign/delete/id/{id}")
	@ResponseBody
	public ResponseEntity<Response> cancelConsignmentApply(@PathVariable String id) {
		Response response;
		Consignment target = service.getById(id);

		if (target != null) {
			response = service.deleteConsignmentById(id);
			if (response.getSuccess()) {
				response.setMesg("取消成功");				
			} else {
				response.setMesg("取消失敗");
			}
		} else {
			response = new Response();
			response.setMesg("查無紀錄，無法取消");
			response.setSuccess(false);
		}
		
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/consign/create")
	@ResponseBody
	public ResponseEntity<Response> createConsignment() {
		Response response = new Response();
		return ResponseEntity.ok(response);
	}
	
	
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
