package com.example.demo.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
import com.example.demo.util.SendMailUtils;

import jakarta.servlet.http.HttpServletRequest;
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

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

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

		List<Map<String, Object>> custList = new ArrayList<Map<String, Object>>();
		for (Customer cust : customers) {
			Map<String, Object> map = new HashMap<String, Object>();
			cust.setPhone(cust.getPhone() == null ? "無" : cust.getPhone());
			map.put("customer", cust);
			CustomerAddress address = addressService.getHomeAddress(cust);
			if (address == null) {
				address = new CustomerAddress();
				address.setCity("無");
				address.setDistrict("");
				address.setStreet("");
			}
			map.put("address", address);
			custList.add(map);
		}

		model.addAttribute("createDate", createDate);
		model.addAttribute("area", area);
		model.addAttribute("hasPoint", hasPoint);
		model.addAttribute("keyword", keyword);
//		model.addAttribute("customers", customers); // 將所有客戶資料加入 model
		model.addAttribute("customers", custList); // 將所有客戶資料及會員地址加入 model
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
		Customer target = service.getById(id); // 這邊用 service.getById 方法
		Map<String, Object> customer = new HashMap<String, Object>();

		Integer consumption = service.getConsumption(target);
		Integer orderTotal = service.getOrderTotal(target);
		Integer consignTotal = service.getConsignTotal(target);
		Integer points = target.getPoints();
		CustomerAddress homeAddress = addressService.getHomeAddress(target);
		CustomerAddress deliverAddress = addressService.getDeliverAddress(target);

		customer.put("id", target.getId());
		customer.put("consumption", consumption);
		customer.put("orderTotal", orderTotal);
		customer.put("consignTotal", consignTotal);
		customer.put("points", points == null ? 0 : points);
		customer.put("name", target.getName());
		customer.put("birthday", target.getBirthday() == null ? "未填寫" : target.getBirthday());
		customer.put("email", target.getEmail());
		customer.put("phone", target.getPhone() == null ? "未填寫" : target.getPhone());
		customer.put("homeAddress", homeAddress == null ? "未填寫"
				: homeAddress.getCity() + homeAddress.getDistrict() + homeAddress.getStreet());
		customer.put("deliverAddress", deliverAddress == null ? "未填寫"
				: deliverAddress.getCity() + deliverAddress.getDistrict() + deliverAddress.getStreet());

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String createdAt = dtf.format(target.getCreatedAt());
		String firstLoginAt = dtf.format(target.getFirstLoginAt());

		customer.put("createdAt", createdAt);
		customer.put("firstLoginAt", firstLoginAt);

		customer.put("baby1Birthday", target.getBaby1Birthday() == null ? "未填寫" : target.getBaby1Birthday());
		customer.put("baby2Birthday", target.getBaby2Birthday() == null ? "未填寫" : target.getBaby2Birthday());
		customer.put("baby3Birthday", target.getBaby3Birthday() == null ? "未填寫" : target.getBaby3Birthday());

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
//	檢查 email
	@PostMapping("/front/checkEmail")
	@ResponseBody
	public ResponseEntity<Response> checkEmail(@RequestBody Map<String, String> body, HttpSession session) {
		/*
		 * { email: "" }
		 */
		Response response = service.checkEmail(body.get("email"));
		if (response.getSuccess()) {
			session.setAttribute("name", body.get("name"));
			session.setAttribute("email", body.get("email"));
			session.setAttribute("password", body.get("password"));
			System.out.println("session id:" + session.getId());
			System.out.println("session email: " + session.getAttribute("email"));
		}
		return ResponseEntity.ok(response);
	}

//	傳送驗證信
	@GetMapping("/front/sendEmail")
	@ResponseBody
	public ResponseEntity<Response> sendEmail(HttpSession session) {
		String email = (String) session.getAttribute("email");
		String name = (String) session.getAttribute("name");
		System.out.println("send session id:" + session.getId());
		System.out.println("send session email: " + session.getAttribute("email"));
		Response response = service.sendEmail(email, name);
		return ResponseEntity.ok(response);
	}

	// 註冊
	@PostMapping("/front/register")
	@ResponseBody
	public ResponseEntity<Response> verifyEmail(@RequestBody Map<String, String> body, HttpSession session,
			HttpServletRequest request) {
		String code = body.get("code");
		Response response = service.verifyEmail(code);
		if (response.getSuccess()) {
			try {
				String name = (String) session.getAttribute("name");
				String email = (String) session.getAttribute("email");
				String password = (String) session.getAttribute("password");
				session.invalidate();

				Customer cust = service.register(name, email, password);
				session = request.getSession(true);
				session.setAttribute("customerId", cust.getId());
				response.setMesg("註冊成功！");
				return ResponseEntity.ok(response);

			} catch (Exception e) {
				response.setSuccess(false);
				response.setMesg("註冊失敗：" + e.getMessage());
				return ResponseEntity.badRequest().body(response);
			}
		}
		return ResponseEntity.ok(response);
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

		if (!passwordEncoder.matches(request.getPassword(), customer.getPassword())) {
			response.put("success", false);
			response.put("mesg", "密碼錯誤");
			return ResponseEntity.ok(response);
		}

		// 過濾已刪除帳號
		if (Boolean.TRUE.equals(customer.getIsDeleted())) {
			response.put("success", false);
			response.put("mesg", "帳號不存在");
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
		}

		// 登入成功
		session.setAttribute("customerId", customer.getId());
		session.setAttribute("customerName", customer.getName());
//		System.out.println("已登入，Session ID: " + session.getId());
//		System.out.println("已設 customerId: " + session.getAttribute("customerId"));
		response.put("success", true);
		response.put("mesg", "登入成功");
		return ResponseEntity.ok(response);

	}

//	Google登入
	@PostMapping("/front/googleLogin")
	@ResponseBody
	public ResponseEntity<Response> googleLogin(@RequestBody Map<String, String> body, HttpSession session) {
		String token = body.get("token");
		Response response = service.googleLogin(token, session);
		return ResponseEntity.ok(response);

	}

	@PostMapping("/front/logout")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
		System.out.println("登出sessionId: " + session.getId());
		session.invalidate();
		Map<String, Object> response = new HashMap<>();
		response.put("success", true);
		response.put("mesg", "已成功登出");
		return ResponseEntity.ok(response);
	}

	// 登入抓會員資料
	@GetMapping("/front/me")
	@ResponseBody
	public ResponseEntity<?> getCurrentMember(HttpSession session, HttpServletRequest request) {
		Object idObj = session.getAttribute("customerId");
//		System.out.println("讀取 /me 的 Session ID: " + session.getId());
//		System.out.println("Session 中的 customerId: " + session.getAttribute("customerId"));

		if (idObj == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "未登入"));
		}

		Integer id = (Integer) idObj;
		Optional<Customer> optional = service.findById(id);

		if (optional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "會員不存在"));
		}

		Customer customer = optional.get();
		CustomerAddress address = addressService.getHomeAddress(customer);

		Map<String, Object> member = new HashMap<>();
		member.put("id", customer.getId());
		member.put("name", customer.getName());
		member.put("email", customer.getEmail());
		member.put("gender", customer.getGender()); // ✅ 性別欄位
		member.put("phone", customer.getPhone() == null ? "未填寫" : customer.getPhone());
		member.put("birthday", customer.getBirthday() == null ? "未填寫" : customer.getBirthday());
		member.put("createdAt", customer.getCreatedAt());
		member.put("baby1Birthday", customer.getBaby1Birthday() == null ? "未填寫" : customer.getBaby1Birthday());
		member.put("baby2Birthday", customer.getBaby2Birthday() == null ? "未填寫" : customer.getBaby2Birthday());
		member.put("baby3Birthday", customer.getBaby3Birthday() == null ? "未填寫" : customer.getBaby3Birthday());
		member.put("address",
				address == null ? "未填寫" : address.getCity() + address.getDistrict() + address.getStreet());
		member.put("points", customer.getPoints());

		List<String> babyBirthdays = new ArrayList<>();
		if (customer.getBaby1Birthday() != null)
			babyBirthdays.add(customer.getBaby1Birthday().toString());
		if (customer.getBaby2Birthday() != null)
			babyBirthdays.add(customer.getBaby2Birthday().toString());
		if (customer.getBaby3Birthday() != null)
			babyBirthdays.add(customer.getBaby3Birthday().toString());

		member.put("babyBirthdays", babyBirthdays);

		// 地址
		if (address != null) {
			member.put("address", address.getCity() + address.getDistrict() + address.getStreet());
		} else {
			member.put("address", "尚未設定");
		}

		// ✅ 信用卡遮罩（只取最後四碼）
		if (customer.getCreditCard() != null && customer.getCreditCard().length() >= 4) {
			String last4 = customer.getCreditCard().substring(customer.getCreditCard().length() - 4);
			member.put("creditCardLast4", "**** **** **** " + last4);
		} else {
			member.put("creditCardLast4", "尚未綁定");
		}

		return ResponseEntity.ok(member);
	}

//編輯會員資料
	@PutMapping("/front/me/update")
	@ResponseBody
	public ResponseEntity<?> updateMember(@RequestBody Map<String, Object> payload, HttpSession session) {
		Integer customerId = (Integer) session.getAttribute("customerId");
		if (customerId == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("未登入");
		}

		Optional<Customer> optional = service.findById(customerId);
		if (optional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("會員不存在");
		}

		Customer customer = optional.get();

		// 取資料並更新
		String lastName = (String) payload.get("lastName");
		String firstName = (String) payload.get("firstName");
		customer.setName(lastName + firstName);
		customer.setPhone((String) payload.get("phone"));
		customer.setGender((String) payload.get("gender"));

		// 生日處理
		String birthdayStr = (String) payload.get("birthday");
		if (birthdayStr != null && !birthdayStr.equals("未填寫") && !birthdayStr.isBlank()) {
		    try {
		        LocalDate birthday = LocalDate.parse(birthdayStr);
		        customer.setBirthday(birthday);
		    } catch (DateTimeParseException e) {
		        return ResponseEntity.badRequest().body("生日無法顯示");
		    }
		}
		// 處理 babyBirthdays（List<String> → LocalDate）
		List<String> babyBirthdays = (List<String>) payload.get("babyBirthdays");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		// 先清空
		customer.setBaby1Birthday(null);
		customer.setBaby2Birthday(null);
		customer.setBaby3Birthday(null);
		if (babyBirthdays != null) {
			if (babyBirthdays.size() > 0)
				customer.setBaby1Birthday(LocalDate.parse(babyBirthdays.get(0) + "-01", formatter));
			if (babyBirthdays.size() > 1)
				customer.setBaby2Birthday(LocalDate.parse(babyBirthdays.get(1) + "-01", formatter));
			if (babyBirthdays.size() > 2)
				customer.setBaby3Birthday(LocalDate.parse(babyBirthdays.get(2) + "-01", formatter));
		}
		System.out.println("接收到 babyBirthdays：" + babyBirthdays);

		service.save(customer);
		return ResponseEntity.ok("會員資料更新成功");
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
	public ResponseEntity<?> getMemberProfile(HttpSession session, HttpServletRequest request) {
		return getCurrentMember(session, request);
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
			m.put("color", p.getColor()); // ✅ 新增
			m.put("specification", p.getSpecification()); // ✅ 新增
			
			// 加在這裡：處理圖片路徑
//	        String imageUrl = null;
//	        if (fav.getProductImage() != null) {
//	            imageUrl = fav.getProductImage().getImagePath();
//	        } else if (p.getImages() != null && !p.getImages().isEmpty()) {
//	            imageUrl = p.getImages().get(0).getImagePath();
//	        }
			String imageUrl = p.getImageUrl();
            if (imageUrl == null || imageUrl.isBlank()) {
                // 如果沒有圖片URL，嘗試從product_image表獲取第一張圖片
                if (p.getImages() != null && !p.getImages().isEmpty()) {
                    imageUrl = "/products/front/images/" + p.getImages().get(0).getId();
                } else {
                    imageUrl = "/images/default.jpg";
                }
            }
	        System.out.println("fav imageUrl: " + imageUrl);
	        m.put("imageUrl", imageUrl);
	        
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
	// 會員更換密碼

	@PostMapping("/front/changePassword")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> changePassword(@RequestBody Map<String, String> body,
			HttpSession session) {
		Integer id = (Integer) session.getAttribute("customerId");
		if (id == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "mesg", "尚未登入"));
		}

		String newPassword = body.get("newPassword");
		if (newPassword == null || newPassword.length() < 6) {
			return ResponseEntity.badRequest().body(Map.of("success", false, "mesg", "密碼長度需大於等於6"));
		}

		Optional<Customer> optional = customerRepo.findById(id);
		if (optional.isEmpty()) {
			return ResponseEntity.badRequest().body(Map.of("success", false, "mesg", "會員不存在"));
		}

		Customer customer = optional.get();
		customer.setPassword(passwordEncoder.encode(newPassword));
		customerRepo.save(customer);

		session.invalidate();

		return ResponseEntity.ok(Map.of("success", true, "mesg", "密碼已變更，請重新登入"));
	}

	// 前台刪除會員
	@DeleteMapping("/front/delete")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> deleteMember(@RequestBody Map<String, String> body,
			HttpSession session) {
		Map<String, Object> response = new HashMap<>();
		Integer customerId = (Integer) session.getAttribute("customerId");
		String inputPassword = body.get("password");

		if (customerId == null) {
			response.put("success", false);
			response.put("mesg", "未登入");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
		}

		Customer customer = customerRepo.findById(customerId).orElse(null);
		if (customer == null || Boolean.TRUE.equals(customer.getIsDeleted())) {
			response.put("success", false);
			response.put("mesg", "帳號不存在或已刪除");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}

		if (!passwordEncoder.matches(inputPassword, customer.getPassword())) {
			response.put("success", false);
			response.put("mesg", "密碼錯誤");
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
		}

		// 設定 isDeleted 與改信箱(採用軟刪除)
		customer.setIsDeleted(true);
		customer.setEmail(customer.getEmail() + "_deleted_" + customer.getId());

		customerRepo.save(customer);
		session.invalidate();

		response.put("success", true);
		response.put("mesg", "帳號已成功刪除");
		return ResponseEntity.ok(response);
	}
	
	//忘記密碼
	@PostMapping("/front/forgot-password")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> forgotPassword(@RequestBody Map<String, String> body) {
	    Map<String, Object> response = new HashMap<>();
	    String email = body.get("email");

	    Optional<Customer> optional = customerRepo.findByEmailAndIsDeletedFalse(email);
	    if (optional.isEmpty()) {
	        response.put("success", false);
	        response.put("mesg", "查無此信箱");
	        return ResponseEntity.status(404).body(response);
	    }

	    Customer customer = optional.get();
	    String token = UUID.randomUUID().toString(); // 產生唯一 token

	    customer.setResetToken(token);
	    customer.setTokenExpiry(LocalDateTime.now().plusMinutes(30)); // 30 分鐘內有效
	    customerRepo.save(customer);

	    // 寄信邏輯（請串接你現有的 EmailService）
	    String resetUrl = "http://localhost:5501/Member/reset-password.html?token=" + token;
	    String mailContent = String.format("親愛的 %s：\n\n您申請了重設密碼。\n請在 30 分鐘內點擊以下連結重設密碼：\n%s\n\n若您未申請，請忽略本信。", customer.getName(), resetUrl);

	    try {
			SendMailUtils.sendEmail(email, "PikaBaby 密碼重設連結", mailContent);
		} catch (Exception e) {
			e.printStackTrace();
		} 

	    response.put("success", true);
	    response.put("mesg", "已寄出重設密碼連結，請至信箱查看");
	    return ResponseEntity.ok(response);
	}

	//重設密碼
	@PostMapping("/front/reset-password")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> resetPassword(@RequestBody Map<String, String> body) {
	    String token = body.get("token");
	    String newPassword = body.get("newPassword");

	    Map<String, Object> response = new HashMap<>();
	    Optional<Customer> optional = customerRepo.findByResetToken(token);

	    if (optional.isEmpty()) {
	        response.put("success", false);
	        response.put("mesg", "此連結已失效，請重新申請重設密碼。");
	        return ResponseEntity.ok(response); 
	    }

	    Customer customer = optional.get();

	    if (customer.getTokenExpiry().isBefore(LocalDateTime.now())) {
	        response.put("success", false);
	        response.put("mesg", "Token 已過期");
	        return ResponseEntity.status(410).body(response);
	    }

	    // 更新密碼（記得加密）
	    customer.setPassword(passwordEncoder.encode(newPassword));
	    customer.setResetToken(null);
	    customer.setTokenExpiry(null);
	    customerRepo.save(customer);

	    response.put("success", true);
	    response.put("mesg", "密碼已更新，請重新登入");
	    return ResponseEntity.ok(response);
	}


}
