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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.model.BankNo;
import com.example.demo.model.Consignment;
import com.example.demo.model.Response;
import com.example.demo.model.Withdrawal;
import com.example.demo.repository.BankRepository;
import com.example.demo.service.WithdrawalService;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@CrossOrigin(originPatterns = "*", allowCredentials = "true")
@Controller
@RequestMapping("/secondhand")
public class WithdrawalController {

	@Autowired
	private WithdrawalService service;

	@Autowired
	private BankRepository bRepository;

//	================ 前台API =================
	@GetMapping("/front/withdraw/cust")
	@ResponseBody
	public List<Withdrawal> getWithdrawsByCustId(@RequestParam(required = false) String start, 
			@RequestParam(required = false) String end, @RequestParam(required = false) String withdraw, HttpSession session) {
		String custId = session.getAttribute("customerId").toString();
		List<Withdrawal> withdrawals = new ArrayList<Withdrawal>();

		try {
			if (start != null && start != "") {
				if (end != null && end != "") {
					if (withdraw != null && withdraw != "") {
						withdrawals = service.getAllByCustAndDateBetweenAndWithdraw(custId, start, end, withdraw);
					} else {
						withdrawals = service.getAllByCustAndDateBetween(custId, start, end);
					}
				} else {
					if (withdraw != null && withdraw != "") {
						withdrawals = service.getAllByCustAndDateAfterAndWithdraw(custId, start, withdraw);
					} else {
						withdrawals = service.getAllByCustAndDateAfter(custId, start);
					}
				}
			} else {
				if (end != null && end != "") {
					if (withdraw != null && withdraw != "") {
						withdrawals = service.getAllByCustAndDateBeforeAndWithdraw(custId, end, withdraw);
					} else {
						withdrawals = service.getAllByCustAndDateBefore(custId, end);
					}
				} else {
					if (withdraw != null && withdraw != "") {
						withdrawals = service.getAllByCustAndWithdraw(custId, withdraw);
					} else {
						withdrawals = service.getWithdrawsByCustId(custId);
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}

		return withdrawals;
	}

	@DeleteMapping("/front/withdraw/delete/id/{id}")
	@ResponseBody
	public ResponseEntity<Response> cancelWithdrawApply(@PathVariable String id) {
		Response response;
		Withdrawal target = service.getById(id);

		if (target != null) {
			response = service.deleteWithdrawById(id);
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

	@GetMapping("/front/withdraw/banks")
	@ResponseBody
	public List<BankNo> getBanks() {
		return bRepository.findAll();
	}

	@GetMapping("/front/withdraw/total/custId")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getTotal(HttpSession session) {
		String custId = session.getAttribute("customerId").toString();
		Map<String, Object> storage = service.getStorageByCust(custId);
		return ResponseEntity.ok(storage);
	}

	@PostMapping("/front/withdraw/create")
	@ResponseBody
	public ResponseEntity<Response> createWithdraw(@RequestBody Map<String, Object> body, HttpSession session) {
		/* { amount: "", bankId: "", bankAccount: "", ids: [] } */ 
		String custId = session.getAttribute("customerId").toString();
		Response response = service.createWithdraw(body, custId);
		if (response.getSuccess()) {
			response.setMesg("提款申請成功");
		} else {
			response.setMesg("提款申請成功");
		}
		return ResponseEntity.ok(response);
	}

//	================ 後台API =================

	@GetMapping("/withdrawals")
	public String getWithdrawals(Model model, @RequestParam(required = false) String applySDate,
			@RequestParam(required = false) String applyEDate, @RequestParam(required = false) String withdrawSDate,
			@RequestParam(required = false) String withdrawEDate, @RequestParam(required = false) String withdraw,
			@RequestParam(required = false) String custName) {

		List<Withdrawal> withdrawals = new ArrayList();

		try {
			if (applySDate != null && applyEDate != null && applySDate != "" && applyEDate != "") {
				if (withdrawSDate != null && withdrawEDate != null && withdrawSDate != "" && withdrawEDate != "") {
					if (withdraw != null && withdraw != "") {
						if (custName != null && custName != "") {
							withdrawals = service.getAllByDatesAndWithdrawAndCustName(applySDate, applyEDate,
									withdrawSDate, withdrawEDate, withdraw, custName);
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
							withdrawals = service.getAllByApplyDateAndWithdrawAndCustName(applySDate, applyEDate,
									withdraw, custName);
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
							withdrawals = service.getAllByWithdrawDatesAndWithdrawAndCustName(withdrawSDate,
									withdrawEDate, withdraw, custName);
						} else {
							withdrawals = service.getAllByWithdrawDatesAndWithdraw(withdrawSDate, withdrawEDate,
									withdraw);
						}

					} else {
						if (custName != null && custName != "") {
							withdrawals = service.getAllByWithdrawDatesAndCustName(withdrawSDate, withdrawEDate,
									custName);
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
