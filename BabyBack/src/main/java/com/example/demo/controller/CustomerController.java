package com.example.demo.controller;

import com.example.demo.dto.LoginRequestDTO;
import com.example.demo.model.Customer;
import com.example.demo.model.Response;
import com.example.demo.service.CustomerService;

import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@Controller
@RequestMapping("/customers") // 所有路徑都會以 /customers 開頭
public class CustomerController {
	@Autowired
	private CustomerService service;

	// 顯示客戶清單
	// 路徑：/customers，方法：GET
	@GetMapping
	public String list(Model model) {
		model.addAttribute("customers", service.listAll()); // 將所有客戶資料加入 model
		return "customer/list"; // 回傳顯示清單的 Thymeleaf 頁面
	}

	// 顯示新增客戶的表單
	// 路徑：/customers/new，方法：GET
	@GetMapping("/new")
	public String createForm(Model model) {
		model.addAttribute("customer", new Customer());// 建立一個空的 Customer 物件供表單綁定使用
		return "customer/form"; // 回傳顯示新增／編輯表單的頁面
	}

	// 儲存客戶資料（新增或更新）
	// 路徑：/customers/save，方法：POST
	@PostMapping("/save")
	public String save(@ModelAttribute Customer customer) {
		service.save(customer); // 呼叫服務層儲存資料
		return "redirect:/customers";// 儲存後重新導向到客戶清單頁
	}

	// 顯示編輯特定客戶的表單
	// 路徑：/customers/edit/{id}，方法：GET
	@GetMapping("/edit/{id}")
	public String editForm(@PathVariable Long id, Model model) {
		model.addAttribute("customer", service.getById(id));// 根據 ID 查詢客戶並加入 model
		return "customer/form";// 使用相同的表單頁面進行編輯
	}

	// 刪除指定 ID 的客戶資料
	// 路徑：/customers/delete/{id}，方法：GET
	@GetMapping("/delete/{id}")
	public String delete(@PathVariable Long id) {
		service.delete(id);// 呼叫服務層刪除該客戶
		return "redirect:/customers";// 刪除後回到清單頁面
	}

	// 0610喬新增
	// 純 看客戶詳細資料
	@GetMapping("/detail")
	public String showDetail() {
		return "customer/detail"; // 對應 templates/customer/detail.html
	}

	// 對應指定客戶ID跳出詳細資料
	@GetMapping("/detail/{id}")
	public String showCustomerDetail(@PathVariable Long id, Model model) {
		Customer customer = service.getById(id); // 這邊用 service.getById 方法
		model.addAttribute("customer", customer);
		return "customer/detail";
	}

//    ============= 前台API =================
//    根據id取得顧客資料
	@ResponseBody
	@GetMapping("/info/{id}")
	public ResponseEntity<Map<String, Object>> getCustomerInfo(@PathVariable String id) {
		Customer cust = service.getById(Long.parseLong(id));
		Response response = new Response();

		Map<String, Object> map = new HashMap();

		if (cust == null) {
			response.setSuccess(false);
			response.setMesg("查無此顧客");
			map.put("customer", new HashMap());
			map.put("response", response);
		} else {
			response.setSuccess(true);
			response.setMesg("查詢成功");
			map.put("customer", cust);
			map.put("response", response);
		}
		return ResponseEntity.ok(map);
	}

//    ============= 前台會員API =================
// 註冊   
//	@PostMapping("/register")
//	@ResponseBody
//
//	public ResponseEntity<String> register(@RequestBody Customer customer) {
//		try {
//			service.register(customer);
//			return ResponseEntity.ok("註冊成功！");
//		} catch (Exception e) {
//			return ResponseEntity.badRequest().body("註冊失敗：" + e.getMessage());
//		}
//	}
	
	@PostMapping("/register")
	@ResponseBody
	public ResponseEntity<Map<String, String>> register(@RequestBody Customer customer) {
	    Map<String, String> response = new HashMap<>();
	    try {
	        service.register(customer);
	        response.put("message", "註冊成功！");
	        return ResponseEntity.ok(response);
	    } catch (Exception e) {
	        e.printStackTrace();
	        response.put("message", "註冊失敗：" + e.getMessage());
	        return ResponseEntity.badRequest().body(response);
	    }
	}

	
	
	
	
	
	
	
	
	
//	登入API
	@PostMapping("/login")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequestDTO request, HttpSession session) {
	    Optional<Customer> optional = service.findByEmail(request.getEmail());

	    Map<String, Object> response = new HashMap<>();

	    if (optional.isEmpty()) {
	        response.put("success", false);
	        response.put("mesg", "帳號不存在");
	        return ResponseEntity.ok(response);
	    }

	    Customer customer = optional.get();

	    if (!customer.getPassword().equals(request.getPassword())) {
	        response.put("success", false);
	        response.put("mesg", "密碼錯誤");
	        return ResponseEntity.ok(response);
	    }

	    // 登入成功
	    session.setAttribute("customerId", customer.getId());
	    session.setAttribute("customerName", customer.getName());

	    response.put("success", true);
	    response.put("mesg", "登入成功");
	    return ResponseEntity.ok(response);
	    

	}

//	登出
	@PostMapping("/logout")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
	    session.invalidate();
	    Map<String, Object> response = new HashMap<>();
	    response.put("success", true);
	    response.put("mesg", "已成功登出");
	    return ResponseEntity.ok(response);
	}

	
}
