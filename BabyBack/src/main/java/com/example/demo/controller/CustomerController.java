package com.example.demo.controller;

import com.example.demo.dto.LoginRequestDTO;
import com.example.demo.model.Customer;
import com.example.demo.model.CustomerAddress;
import com.example.demo.model.Response;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.service.AddressService;
import com.example.demo.service.CustomerService;

import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5501", allowCredentials = "true")
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
	public String editForm(@PathVariable Integer id, Model model) {
		model.addAttribute("customer", service.getById(id));// 根據 ID 查詢客戶並加入 model
		return "customer/form";// 使用相同的表單頁面進行編輯
	}

	// 刪除指定 ID 的客戶資料
	// 路徑：/customers/delete/{id}，方法：GET
	@GetMapping("/delete/{id}")
	public String delete(@PathVariable Integer id) {
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
	public String showCustomerDetail(@PathVariable Integer id, Model model) {
		Customer customer = service.getById(id); // 這邊用 service.getById 方法
		model.addAttribute("customer", customer);
		return "customer/detail";
	}

//    ============= 前台API =================
//    根據id取得顧客資料
	@ResponseBody
	@GetMapping("/info/{id}")
	public ResponseEntity<Map<String, Object>> getCustomerInfo(@PathVariable String id) {
		Customer cust = service.getById((int) Long.parseLong(id));
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
	@PostMapping("/front/register")
	@ResponseBody
	public ResponseEntity<Map<String, String>> register(@RequestBody Customer customer) {
	    try {
	        service.register(customer);

	        Map<String, String> response = new HashMap<>();
	        response.put("message", "註冊成功！");
	        return ResponseEntity.ok(response);

	    } catch (Exception e) {
	        Map<String, String> errorResponse = new HashMap<>();
	        errorResponse.put("message", "註冊失敗：" + e.getMessage());
	        return ResponseEntity.badRequest().body(errorResponse);
	    }
	}
//	登入API
	@PostMapping("/front/login")
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
	    System.out.println("已登入，Session ID: " + session.getId());
	    System.out.println("已設 customerId: " + session.getAttribute("customerId"));
	    response.put("success", true);
	    response.put("mesg", "登入成功");
	    return ResponseEntity.ok(response);
	    

	}

	@PostMapping("/front/logout")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
	    session.invalidate();
	    Map<String, Object> response = new HashMap<>();
	    response.put("success", true);
	    response.put("mesg", "已成功登出");
	    return ResponseEntity.ok(response);
	}

	//登入抓會員資料
	@GetMapping("/front/me")
	@ResponseBody
	public ResponseEntity<?> getCurrentMember(HttpSession session) {
	    Object idObj = session.getAttribute("customerId");
	    System.out.println("讀取 /me 的 Session ID: " + session.getId());
	    System.out.println("Session 中的 customerId: " + session.getAttribute("customerId"));

	    
	    

	    if (idObj == null) {
	    	System.out.println("idObj == null");
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                             .body(Map.of("error", "未登入"));
	    }

	    Integer id = (Integer) idObj;
	    Optional<Customer> optional = service.findById(id);

	    if (optional.isEmpty()) {
	    	System.out.println("optional.isEmpty()");
	    	return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                             .body(Map.of("error", "會員不存在"));
	    }

	    Customer customer = optional.get();

	    Map<String, Object> member = new HashMap<>();
	    member.put("id", customer.getId());
	    member.put("name", customer.getName());
	    member.put("email", customer.getEmail());
	    member.put("phone", customer.getPhone());
	    member.put("birthday", customer.getBirthday());
	    member.put("createdAt", customer.getCreatedAt());
	    member.put("baby1Birthday", customer.getBaby1Birthday());
	    member.put("baby2Birthday", customer.getBaby2Birthday());
	    member.put("address", customer.getAddress());

	    return ResponseEntity.ok(member);
	}

	//常用地址
	 @Autowired
	    private AddressService addressService;

	    @Autowired
	    private CustomerRepository customerRepo;

	 // 取得登入會員的所有地址
    @GetMapping("/front/address")
    public List<CustomerAddress> getAddresses(HttpSession session) {
    	Integer customerId = (Integer) session.getAttribute("customerId");
        if (customerId == null) throw new RuntimeException("未登入");
        
        Customer customer = customerRepo.findById(customerId).orElseThrow();
        return addressService.getAllByCustomer(customer);
    }

    // 新增地址（使用 session 取得會員，處理預設地址唯一性）
    @PostMapping
    public CustomerAddress addAddress(@RequestBody CustomerAddress address, HttpSession session) {
    	 Integer customerId = (Integer) session.getAttribute("customerId");
         if (customerId == null) throw new RuntimeException("未登入");

         Customer customer = customerRepo.findById(customerId).orElseThrow();
         address.setCustomer(customer);
    	
      // 若要設為預設地址 → 清除其他預設
         if (Boolean.TRUE.equals(address.getIsDefaultOrder())) {
             addressService.clearDefaultOrder(customer);
         }
         if (Boolean.TRUE.equals(address.getIsDefaultShipping())) {
             addressService.clearDefaultShipping(customer);
         }
    	
    	return addressService.save(address);
    }

    // 刪除地址
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAddress(@PathVariable Integer id, HttpSession session) {
        Integer customerId = (Integer) session.getAttribute("customerId");
        if (customerId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("未登入");

        CustomerAddress address = addressService.findById(id);
        if (!address.getCustomer().getId().equals(customerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("無權限刪除");
        }

        addressService.deleteById(id);
        return ResponseEntity.ok("刪除成功");
    }

	
    //編輯地址
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAddress(@PathVariable Integer id,
                                           @RequestBody CustomerAddress updated,
                                           HttpSession session) {
        Integer customerId = (Integer) session.getAttribute("customerId");
        if (customerId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("未登入");

        CustomerAddress original = addressService.findById(id);
        if (!original.getCustomer().getId().equals(customerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("無權限修改");
        }

        if (Boolean.TRUE.equals(updated.getIsDefaultOrder())) {
            addressService.clearDefaultOrder(original.getCustomer());
        }
        if (Boolean.TRUE.equals(updated.getIsDefaultShipping())) {
            addressService.clearDefaultShipping(original.getCustomer());
        }

        original.setName(updated.getName());
        original.setCity(updated.getCity());
        original.setDistrict(updated.getDistrict());
        original.setZipcode(updated.getZipcode());
        original.setStreet(updated.getStreet());
        original.setPhone(updated.getPhone());
        original.setIsDefaultOrder(updated.getIsDefaultOrder());
        original.setIsDefaultShipping(updated.getIsDefaultShipping());

        addressService.save(original);
        return ResponseEntity.ok("編輯成功");
    }
 
    
    
    
    
	
}

