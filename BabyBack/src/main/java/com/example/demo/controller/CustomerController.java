package com.example.demo.controller;

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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.dto.LoginRequestDTO;
import com.example.demo.model.Customer;
import com.example.demo.model.CustomerAddress;
import com.example.demo.model.CustomerFavorites;
import com.example.demo.model.Product;
import com.example.demo.model.Response;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.service.AddressService;
import com.example.demo.service.CustomerFavoritesService;
import com.example.demo.service.CustomerService;

import jakarta.servlet.http.HttpSession;

@CrossOrigin(origins = "http://localhost:5501", allowCredentials = "true")
@Controller
@RequestMapping("/customers") // 所有路徑都會以 /customers 開頭
public class CustomerController {
	@Autowired
	private CustomerService service;

	// 常用地址
	@Autowired
	private AddressService addressService;

	@Autowired
	private CustomerRepository customerRepo;

	// 顯示客戶清單
	// 路徑：/customers，方法：GET
	@GetMapping
	public String list(Model model, @RequestParam(required = false) String createDate,
			@RequestParam(required = false) String area, @RequestParam(required = false) String hasPoint,
			@RequestParam(required = false) String keyword) {
		List<Customer> customers;

		try {
			if (createDate != null && createDate != "") {
				if (area != null && area != "") {
					if (hasPoint != null && hasPoint != "") {
						if (keyword != null && keyword != "") {
							customers = service.getAllByCreateDateAndAreaAndPointAndKeyword(createDate, area, hasPoint,
									keyword);
						} else {
							customers = service.getAllByCreateDateAndAreaAndPoint(createDate, area, hasPoint);
						}
					} else {
						if (keyword != null && keyword != "") {
							customers = service.getAllByCreateDateAndAreaAndKeyword(createDate, area, keyword);
						} else {
							customers = service.getAllByCreateDateAndArea(createDate, area);
						}
					}
				} else {
					if (hasPoint != null && hasPoint != "") {
						if (keyword != null && keyword != "") {
							customers = service.getAllByCreateDateAndPointAndKeyword(createDate, hasPoint, keyword);
						} else {
							customers = service.getAllByCreateDateAndPoint(createDate, hasPoint);
						}
					} else {
						if (keyword != null && keyword != "") {
							customers = service.getAllByCreateDateAndKeyword(createDate, keyword);
						} else {
							customers = service.getAllByCreateDate(createDate);
						}
					}
				}
			} else {
				if (area != null && area != "") {
					if (hasPoint != null && hasPoint != "") {
						if (keyword != null && keyword != "") {
							customers = service.getAllByAreaAndPointAndKeyword(area, hasPoint, keyword);
						} else {
							customers = service.getAllByAreaAndPoint(area, hasPoint);
						}
					} else {
						if (keyword != null && keyword != "") {
							customers = service.getAllByAreaAndKeyword(area, keyword);
						} else {
							customers = service.getAllByArea(area);
						}
					}
				} else {
					if (hasPoint != null && hasPoint != "") {
						if (keyword != null && keyword != "") {
							customers = service.getAllByPointAndKeyword(hasPoint, keyword);
						} else {
							customers = service.getAllByPoint(hasPoint);
						}
					} else {
						if (keyword != null && keyword != "") {
							customers = service.getAllByKeyword(keyword);
						} else {
							customers = service.listAll();
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e);
			customers = new ArrayList<Customer>();
		}

		model.addAttribute("createDate", createDate);
		model.addAttribute("area", area);
		model.addAttribute("hasPoint", hasPoint);
		model.addAttribute("keyword", keyword);
		model.addAttribute("customers", customers); // 將所有客戶資料加入 model
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
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Response> delete(@PathVariable Integer id) {
		Response response = service.delete(id);// 呼叫服務層刪除該客戶
		if (response.getSuccess()) {
			response.setMesg("刪除成功");
		} else {
			response.setMesg("刪除失敗");
		}
		return ResponseEntity.ok(response);
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
		Integer consumption = service.getConsumption(customer);
		Integer orderTotal = service.getOrderTotal(customer);
		Integer consignTotal = service.getConsignTotal(customer);
		Integer points = customer.getPoints();
		CustomerAddress homeAddress = addressService.getHomeAddress(customer);
		CustomerAddress deliverAddress = addressService.getDeliverAddress(customer);

		if (homeAddress == null) {
			homeAddress = new CustomerAddress();
			homeAddress.setCity("");
			homeAddress.setDistrict("");
			homeAddress.setStreet("");
		}

		if (deliverAddress == null) {
			deliverAddress = new CustomerAddress();
			deliverAddress.setCity("");
			deliverAddress.setDistrict("");
			deliverAddress.setStreet("");
		}

		model.addAttribute("consumption", consumption);
		model.addAttribute("orderTotal", orderTotal);
		model.addAttribute("consignTotal", consignTotal);
		model.addAttribute("points", points == null ? 0 : points);
		model.addAttribute("homeAddress", homeAddress);
		model.addAttribute("deliverAddress", deliverAddress);
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

	// 登入抓會員資料
	@GetMapping("/front/me")
	@ResponseBody
	public ResponseEntity<?> getCurrentMember(HttpSession session) {
		Object idObj = session.getAttribute("customerId");
		System.out.println("讀取 /me 的 Session ID: " + session.getId());
		System.out.println("Session 中的 customerId: " + session.getAttribute("customerId"));

		if (idObj == null) {
			System.out.println("idObj == null");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "未登入"));
		}

		Integer id = (Integer) idObj;
		Optional<Customer> optional = service.findById(id);

		if (optional.isEmpty()) {
			System.out.println("optional.isEmpty()");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "會員不存在"));
		}

		Customer customer = optional.get();
		CustomerAddress address = addressService.getHomeAddress(customer);

		Map<String, Object> member = new HashMap<>();
		member.put("id", customer.getId());
		member.put("name", customer.getName());
		member.put("email", customer.getEmail());
		member.put("phone", customer.getPhone());
		member.put("birthday", customer.getBirthday());
		member.put("createdAt", customer.getCreatedAt());
		member.put("baby1Birthday", customer.getBaby1Birthday());
		member.put("baby2Birthday", customer.getBaby2Birthday());
//		member.put("address", customer.getAddress());
		member.put("address", address.getCity() + address.getDistrict() + address.getStreet());

		return ResponseEntity.ok(member);
	}

	// 檢查會員登入狀態 API
	@GetMapping("/check-login")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> checkLoginStatus(HttpSession session) {
		Object customerId = session.getAttribute("customerId");
		Map<String, Object> response = new HashMap<>();

		if (customerId != null) {
			response.put("isLoggedIn", true);
			response.put("customerId", customerId);
			response.put("customerName", session.getAttribute("customerName"));
		} else {
			response.put("isLoggedIn", false);
		}

		return ResponseEntity.ok(response);
	}

	// 取得會員資料 API (別名)
	@GetMapping("/profile")
	@ResponseBody
	public ResponseEntity<?> getMemberProfile(HttpSession session) {
		return getCurrentMember(session);
	}

	// 取得會員點數 API
	@GetMapping("/points")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getMemberPoints(HttpSession session) {
		Object idObj = session.getAttribute("customerId");
		Map<String, Object> response = new HashMap<>();

		if (idObj == null) {
			response.put("success", false);
			response.put("message", "未登入");
			response.put("points", 0);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
		}

		Integer id = (Integer) idObj;
		Optional<Customer> optional = service.findById(id);

		if (optional.isEmpty()) {
			response.put("success", false);
			response.put("message", "會員不存在");
			response.put("points", 0);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}

		Customer customer = optional.get();
		Integer points = customer.getPoints() != null ? customer.getPoints() : 0;

		response.put("success", true);
		response.put("points", points);
		response.put("customerId", customer.getId());
		response.put("customerName", customer.getName());

		return ResponseEntity.ok(response);
	}

	// 取得登入會員的所有地址
	@GetMapping("/front/address")
	@ResponseBody
	public List<CustomerAddress> getAddresses(HttpSession session) {
		Integer customerId = (Integer) session.getAttribute("customerId");
		if (customerId == null)
			throw new RuntimeException("未登入");

		Customer customer = customerRepo.findById(customerId).orElseThrow();
		return addressService.getAllByCustomer(customer);
	}

	// 新增地址（使用 session 取得會員，處理預設地址唯一性）
	@PostMapping("/front/address")
	@ResponseBody
	public CustomerAddress addAddress(@RequestBody CustomerAddress address, HttpSession session) {
		Integer customerId = (Integer) session.getAttribute("customerId");
		if (customerId == null)
			throw new RuntimeException("未登入");

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
	@DeleteMapping("/front/address/{id}")
	@ResponseBody
	public ResponseEntity<?> deleteAddress(@PathVariable Integer id, HttpSession session) {
		Integer customerId = (Integer) session.getAttribute("customerId");
		if (customerId == null)
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("未登入");

		CustomerAddress address = addressService.findById(id);
		if (!address.getCustomer().getId().equals(customerId)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("無權限刪除");
		}

		addressService.deleteById(id);
		return ResponseEntity.ok("刪除成功");
	}

	// 編輯地址

	@PutMapping("/front/address/{id}")
	@ResponseBody
	public ResponseEntity<?> updateAddress(@PathVariable Integer id, @RequestBody CustomerAddress updated,
			HttpSession session) {
		Integer customerId = (Integer) session.getAttribute("customerId");
		if (customerId == null)
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("未登入");

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

	// ============= 收藏清單 API =================
	@Autowired
	private CustomerFavoritesService favoritesService;

	@GetMapping("/front/favorites")
	@ResponseBody
	public ResponseEntity<?> getFavorites(HttpSession session) {
		Integer customerId = (Integer) session.getAttribute("customerId");
		if (customerId == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("未登入");
		}

		Customer customer = customerRepo.findById(customerId).orElseThrow();
		List<CustomerFavorites> favorites = favoritesService.getFavoritesByCustomer(customer);

		List<Map<String, Object>> list = favorites.stream().map(fav -> {
			Product p = fav.getProduct();
			Map<String, Object> m = new HashMap<>();
			m.put("id", p.getId());
			m.put("name", p.getName());
			m.put("price", p.getPrice());
			m.put("imageUrl", p.getImageUrl());
			m.put("color", p.getColor()); // ✅ 新增
			m.put("specification", p.getSpecification()); // ✅ 新增
			return m;
		}).toList();

		return ResponseEntity.ok(list);
	}

	@DeleteMapping("/front/favorites/{productId}")
	@ResponseBody
	public ResponseEntity<String> removeFavorite(@PathVariable Integer productId, HttpSession session) {
		Integer customerId = (Integer) session.getAttribute("customerId");
		if (customerId == null)
			return ResponseEntity.status(401).body("未登入");

		Customer customer = customerRepo.findById(customerId).orElseThrow();
		Product product = new Product();
		product.setId(productId);
		favoritesService.removeFavorite(customer, product);

		return ResponseEntity.ok("已移除收藏");
	}

	@PostMapping("/front/favorites")
	@ResponseBody
	public ResponseEntity<String> addFavorite(@RequestBody Map<String, Integer> body, HttpSession session) {
		Integer customerId = (Integer) session.getAttribute("customerId");
		if (customerId == null)
			return ResponseEntity.status(401).body("未登入");

		Integer productId = body.get("productId");
		Customer customer = customerRepo.findById(customerId).orElseThrow();
		Product product = new Product();
		product.setId(productId);

		favoritesService.addFavorite(customer, product);
		return ResponseEntity.ok("已加入收藏");
	}
}