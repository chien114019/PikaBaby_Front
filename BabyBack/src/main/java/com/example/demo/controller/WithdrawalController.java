package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.Consignment;
import com.example.demo.model.Response;
import com.example.demo.model.Withdrawal;
import com.example.demo.service.WithdrawalService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
@RequestMapping("/secondhand")
public class WithdrawalController {
	
	@Autowired
	private WithdrawalService service;
	

	@GetMapping("/withdrawals")
	public String getWithdrawals(Model model, @RequestParam(required = false) String applySDate, 
			@RequestParam(required = false) String applyEDate, @RequestParam(required = false) String withdrawSDate, 
			@RequestParam(required = false) String withdrawEDate, @RequestParam(required = false) String withdraw, 
			@RequestParam(required = false) String custName) {
		
		List<Withdrawal> withdrawals = null;
		
		try {
			if (applySDate != null && applyEDate != null && applySDate != "" && applyEDate != "") {
				if (withdrawSDate != null && withdrawEDate != null && withdrawSDate != "" && withdrawEDate != "") {
					if (withdraw != null && withdraw != "") {
						if (custName != null && custName != "") {
							withdrawals = service.getAllByDatesAndWithdrawAndCustName(
									applySDate, applyEDate, withdrawSDate, withdrawEDate, withdraw, custName);
						} else {
							withdrawals = service.getAllByDatesAndWithdraw(applySDate, applyEDate, withdrawSDate, 
									withdrawEDate, withdraw);
						}
					} else {
						if (custName != null && custName != "") {
							withdrawals = service.getAllByDatesAndCustName(applySDate, applyEDate, withdrawSDate, 
									withdrawEDate, custName);
						} else {
							withdrawals = service.getAllByDates(applySDate, applyEDate, withdrawSDate, withdrawEDate);
						}					
					}
				} else {
					if (withdraw != null && withdraw != "") {
						if (custName != null && custName != "") {
							withdrawals = service.getAllByApplyDateAndWithdrawAndCustName(applySDate, applyEDate, withdraw, custName);
						} else {
							withdrawals = service.getAllByApplyDateAndWithdraw(applySDate, applyEDate, withdraw);
						}
						
					} else {
						if (custName != null && custName != "") {
							withdrawals = service.getAllByApplyDateAndCustName(applySDate, applyEDate, custName);
						} else {
							withdrawals = service.getAllByApplyDate(applySDate, applyEDate);
						}
					}
				}
			} else {
				if (withdrawSDate != null && withdrawEDate != null && withdrawSDate != "" && withdrawEDate != "") {
					if (withdraw != null && withdraw != "") {
						if (custName != null && custName != "") {
							withdrawals = service.getAllByWithdrawDatesAndWithdrawAndCustName(withdrawSDate, withdrawEDate, withdraw, custName);
						} else {
							withdrawals = service.getAllByWithdrawDatesAndWithdraw(withdrawSDate, withdrawEDate, withdraw);
						}
						
					} else {
						if (custName != null && custName != "") {
							withdrawals = service.getAllByWithdrawDatesAndCustName(withdrawSDate, withdrawEDate, custName);
						} else {
							withdrawals = service.getAllByWithdrawDates(withdrawSDate, withdrawEDate);
						}
					}
				} else {
					if (withdraw != null && withdraw != "") {
						if (custName != null && custName != "") {
							withdrawals = service.getAllByWithdrawAndCustName(withdraw, custName);
						} else {
							withdrawals = service.getAllByWithdraw(withdraw);
						}
					} else {
						if (custName != null && custName != "") {
							withdrawals = service.getAllByCustName(custName);
						} else {
							withdrawals = service.getAll();
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		
		
		model.addAttribute("applySDate", applySDate);
		model.addAttribute("applyEDate", applyEDate);
		model.addAttribute("withdrawSDate", withdrawSDate);
		model.addAttribute("withdrawEDate", withdrawEDate);
		model.addAttribute("withdraw", withdraw);
		model.addAttribute("custName", custName);
		model.addAttribute("withdrawals", withdrawals);
		return "secondhand/withdrawals";
	}
	
	@GetMapping("/withdraw/{id}")
	public ResponseEntity<Withdrawal> getWithdrawById(@PathVariable String id) {
		return ResponseEntity.ok(service.getById(id));
	}
	
	@PostMapping("/withdraw/edit/{id}")
	public ResponseEntity<Response> editWithdraw(@RequestBody Map<String, String> body, @PathVariable String id) {
		Response response;
		String withdraw = body.get("withdraw");
		String withdrawDate = body.get("withdrawDate");
		
		try {
			response = service.editWithdraw(id, withdraw, withdrawDate);
		} catch (Exception e) {
			System.out.println(e);
			response = new Response();
			response.setSuccess(false);
			response.setMesg("日期錯誤");
		}
		
		return ResponseEntity.ok(response);
	}
	
	

}
