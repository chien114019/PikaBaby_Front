package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
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

	@PostMapping("/request")
	public Response PayRequest(@RequestBody CheckoutPaymentRequestForm form) {
		try {
			return linePayService.RequestService(form);
			
		} catch (Exception e) {
			System.out.println(e);
			
			Response response = new Response();
			response.setReturnCode("-1");
			response.setReturnMesg("請求失敗");
			return response;
		}
	}
	
	@GetMapping("/confirm")
	public void PayConfirm(@RequestParam String transactionId, 
			@RequestParam String orderId, HttpServletResponse res) {
		try {
			Response result = linePayService.ConfirmService(transactionId, orderId);
			if (result.getReturnCode().equals("0000")) {
				res.sendRedirect("http://localhost:8080/Payment.html");
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
}
