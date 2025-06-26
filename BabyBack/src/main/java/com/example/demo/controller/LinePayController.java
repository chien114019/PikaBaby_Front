package com.example.demo.controller;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
	
	private Map<String, String> payStatus =  new ConcurrentHashMap<>();	

	
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
						   @RequestParam String orderId, 
						   @RequestParam Integer amount,
						   HttpServletResponse res) {
		try {
			System.out.println("PayConfirm()");
			Response result = linePayService.ConfirmService(transactionId, orderId,amount);
			
			 if ("0000".equals(result.getReturnCode())) {
		            payStatus.put(orderId, "1");
		        } else {
		            payStatus.put(orderId, "-1");
		        }
			 
			res.sendRedirect("http://localhost:8080/Shopping/linepay-confirm.html?orderId=" + orderId + "&transactionId=" + transactionId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@GetMapping("/checkStatus")
	public ResponseEntity<Response> getPayStatus(@RequestParam String orderId) {
		Response response = new Response();
		String status = payStatus.get(orderId);
		

	    if (status != null) {
	        response.setReturnCode(status);
	        payStatus.remove(orderId); // ✅ 用完後就移除
	    } else {
	        response.setReturnCode("0"); // 或其他預設狀態
	    }

	    return ResponseEntity.ok(response);
	}
	
	
}
