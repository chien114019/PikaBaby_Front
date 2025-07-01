package com.example.demo.controller;

import java.math.BigDecimal;
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
import com.example.demo.repository.SalesOrderDetailRepository;
import com.example.demo.service.LinePayService;
import com.example.demo.service.SalesOrderService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/linepay")
public class LinePayController {

	@Autowired
	LinePayService linePayService;

	@Autowired
	private SalesOrderService salesOrderService;

	private Map<String, String> payStatus = new ConcurrentHashMap<>();

	@PostMapping("/request")
	public ResponseEntity<Response> PayRequest(@RequestBody CheckoutPaymentRequestForm form) {
		try {
			System.out.println("PayRequest()");
//			System.out.println("PayRequest() amount = " + amount);

			return linePayService.RequestService(form);

		} catch (Exception e) {
			System.out.println(e);

			Response response = new Response();
			response.setReturnCode("-1");
			return ResponseEntity.ok(response);
		}
	}

	@GetMapping("/confirm")
	public void PayConfirm(@RequestParam String transactionId, @RequestParam String orderId, HttpServletResponse res) {
		try {
			System.out.println("PayConfirm()");

//			orderId = orderId.split("_")[0] + orderId.split("_")[1];
			System.out.println("orderId:" + orderId);

//			BigDecimal amount = salesOrderService.findAmountByOrderId(orderId);

			Response result = linePayService.ConfirmService(transactionId, orderId);

			if ("0000".equals(result.getReturnCode())) {
				payStatus.put(orderId, "1");
			} else {
				System.out.println("Confirm error: " + result.getReturnCode());
				payStatus.put(orderId, "-1");
			}

			res.sendRedirect("http://localhost:5501/Shopping/linepay-confirm.html?orderId=" + orderId
					+ "&transactionId=" + transactionId);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@GetMapping("/checkStatus")
	public ResponseEntity<Response> getPayStatus(@RequestParam String orderId) {
		Response response = new Response();
		String status = payStatus.get(orderId);
		System.out.println("getPayStatus orderId = " + orderId);
		System.out.println("getPayStatus status = " + status);

		if (status != null) {
			response.setReturnCode(status);
			payStatus.remove(orderId); // ✅ 用完後就移除
		} else {
			response.setReturnCode("0"); // 或其他預設狀態
		}

		return ResponseEntity.ok(response);
	}

}
