package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Linepay.CheckoutPaymentRequestForm;
import com.example.demo.model.Linepay.Response;
import com.example.demo.service.LinePayService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/linepay")
public class LinePayController {
	
	@Autowired
	LinePayService linePayService;
	
	private String payStatus = "";	// 1:成功，-1:失敗

	
	@PostMapping("/request")
	public ResponseEntity<Response> PayRequest(@RequestBody CheckoutPaymentRequestForm form) {
		try {
			System.out.println("PayRequest()");
			
			return linePayService.RequestService(form);
			
		} catch (Exception e) {
			System.out.println(e);
			
			Response response = new Response();
			response.setReturnCode("-1");
			return ResponseEntity.ok(response);
		}
	}
	
	@GetMapping("/confirm")
	public void PayConfirm(@RequestParam String transactionId, 
			@RequestParam String orderId, HttpServletResponse res) {
		try {
			System.out.println("PayConfirm()");
			
			Response result = linePayService.ConfirmService(transactionId, orderId);
			if (result.getReturnCode().equals("0000")) {
				payStatus = "1";
			}
			else {
				payStatus = "-1";
			}
			res.sendRedirect("http://localhost:8080/Payment.html");
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	@GetMapping("/checkStatus")
	public ResponseEntity<Response> getPayStatus() {
		Response response = new Response();
		response.setReturnCode(payStatus);
		payStatus = "";
		return ResponseEntity.ok(response);
	}
	
	
}
