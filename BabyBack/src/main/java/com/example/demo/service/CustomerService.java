package com.example.demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.hibernate.type.descriptor.java.LocalDateTimeJavaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.controller.LinePayController;
import com.example.demo.model.Consignment;
import com.example.demo.model.Customer;
import com.example.demo.model.CustomerAddress;
import com.example.demo.model.CustomerFavorites;
import com.example.demo.model.Receivable;
import com.example.demo.model.Response;
import com.example.demo.model.SalesOrder;
import com.example.demo.model.Withdrawal;
import com.example.demo.repository.AddressRepository;
import com.example.demo.repository.ConsignmentRepository;
import com.example.demo.repository.CustomerFavoritesRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.ReceivableRepository;
import com.example.demo.repository.SalesOrderRepository;
import com.example.demo.repository.WithdrawalRepository;
import com.example.demo.util.SendMailUtils;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import jakarta.servlet.http.HttpSession;

@Service
public class CustomerService {

	private final SecurityFilterChain filterChain;
	@Autowired
    private PasswordEncoder passwordEncoder; // 自動注入
	@Autowired
	private CustomerRepository repository;

	@Autowired
	private SalesOrderRepository soRepository;

	@Autowired
	private ConsignmentRepository cRepository;

	@Autowired
	private ReceivableRepository rRepository;

	@Autowired
	private WithdrawalRepository wRepository;

	@Autowired
	private CustomerFavoritesRepository cfRepository;

	@Autowired
	private AddressRepository aRepository;
	
	private final String GOOGLE_CLIENT_ID = "616366543206-n1lcq6hgflspq29lv00088nu19n0ohcu.apps.googleusercontent.com";

	CustomerService(SecurityFilterChain filterChain) {
		this.filterChain = filterChain;
	}

	public List<Customer> listAll() {
		return repository.findAll();
	}

	public List<Customer> getAllByCreateDateAndAreaAndPointAndKeyword(String createDate, String area, String hasPoint,
			String keyword) {
		List<Customer> customers = new ArrayList<Customer>();
		keyword = "%" + keyword + "%";
		LocalDateTime start = getLocalDate(createDate).atStartOfDay();
		LocalDateTime end = getLocalDate(createDate).plusDays(1).atStartOfDay();

		if (Integer.parseInt(hasPoint) > 0) {
			customers = repository.findAllByDateAndAreaAndHasPointAndKeyword(start, end, area, keyword);
		} else {
			customers = repository.findAllByDateAndAreaAndNoPointAndKeyword(start, end, area, keyword);
		}
		return customers;
	}

	public List<Customer> getAllByCreateDateAndAreaAndPoint(String createDate, String area, String hasPoint) {
		List<Customer> customers = new ArrayList<Customer>();
		LocalDateTime start = getLocalDate(createDate).atStartOfDay();
		LocalDateTime end = getLocalDate(createDate).plusDays(1).atStartOfDay();

		if (Integer.parseInt(hasPoint) > 0) {
			customers = repository.findAllByDateAndAreaAndHasPoint(start, end, area);
		} else {
			customers = repository.findAllByDateAndAreaAndNoPoint(start, end, area);
		}
		return customers;
	}

	public List<Customer> getAllByCreateDateAndAreaAndKeyword(String createDate, String area, String keyword) {
		keyword = "%" + keyword + "%";
		LocalDateTime start = getLocalDate(createDate).atStartOfDay();
		LocalDateTime end = getLocalDate(createDate).plusDays(1).atStartOfDay();
		return repository.findAllByDateAndAreaAndKeyword(start, end, area, keyword);
	}

	public List<Customer> getAllByCreateDateAndArea(String createDate, String area) {
		LocalDateTime start = getLocalDate(createDate).atStartOfDay();
		LocalDateTime end = getLocalDate(createDate).plusDays(1).atStartOfDay();
		return repository.findAllByDateAndArea(start, end, area);
	}

	public List<Customer> getAllByCreateDateAndPointAndKeyword(String createDate, String hasPoint, String keyword) {
		keyword = "%" + keyword + "%";
		LocalDateTime start = getLocalDate(createDate).atStartOfDay();
		LocalDateTime end = getLocalDate(createDate).plusDays(1).atStartOfDay();

		if (Integer.parseInt(hasPoint) > 0) {
			return repository.findAllByDateAndHasPointAndKeyword(start, end, keyword);
		} else {
			return repository.findAllByDateAndNoPointAndKeyword(start, end, keyword);
		}
	}

	public List<Customer> getAllByCreateDateAndPoint(String createDate, String hasPoint) {
		LocalDateTime start = getLocalDate(createDate).atStartOfDay();
		LocalDateTime end = getLocalDate(createDate).plusDays(1).atStartOfDay();

		if (Integer.parseInt(hasPoint) > 0) {
			return repository.findAllByDateAndHasPoint(start, end);
		} else {
			return repository.findAllByDateAndNoPoint(start, end);
		}
	}

	public List<Customer> getAllByCreateDateAndKeyword(String createDate, String keyword) {
		keyword = "%" + keyword + "%";
		LocalDateTime start = getLocalDate(createDate).atStartOfDay();
		LocalDateTime end = getLocalDate(createDate).plusDays(1).atStartOfDay();
		return repository.findAllByCreateDateAndKeyword(start, end, keyword);
	}

	public List<Customer> getAllByCreateDate(String createDate) {
		LocalDateTime start = getLocalDate(createDate).atStartOfDay();
		LocalDateTime end = getLocalDate(createDate).plusDays(1).atStartOfDay();
		return repository.findAllByCreateDate(start, end);
	}

	public List<Customer> getAllByAreaAndPointAndKeyword(String area, String hasPoint, String keyword) {
		keyword = "%" + keyword + "%";
		if (Integer.parseInt(hasPoint) > 0) {
			return repository.findAllByAddressAndHasPointsAndKeyword(area, keyword);
		} else {
			return repository.findAllByAddressAndNoPointsAndKeyword(area, keyword);
		}
	}

	public List<Customer> getAllByAreaAndPoint(String area, String hasPoint) {
		if (Integer.parseInt(hasPoint) > 0) {
			return repository.findAllByAddressLikeAndPointsGreaterThan(area, 0);
		} else {
			return repository.findAllByAddressLikeAndPointsLessThanEqual(area, 0);
		}
	}

	public List<Customer> getAllByAreaAndKeyword(String area, String keyword) {
		keyword = "%" + keyword + "%";
		return repository.findAllByAddressAndKeyword(area, keyword);
	}

	public List<Customer> getAllByArea(String area) {
		return repository.findAllByAddressLike(area);
	}

	public List<Customer> getAllByPointAndKeyword(String hasPoint, String keyword) {
		keyword = "%" + keyword + "%";
		if (Integer.parseInt(hasPoint) > 0) {
			return repository.findAllByHasPointsAndKeyword(keyword);
		} else {
			return repository.findAllByNoPointsAndKeyword(keyword);
		}
	}

	public List<Customer> getAllByPoint(String hasPoint) {
		if (Integer.parseInt(hasPoint) > 0) {
			return repository.findAllByPointsGreaterThan(0);
		} else {
			return repository.findAllByPointsLessThanEqual(0);
		}
	}

	public List<Customer> getAllByKeyword(String keyword) {
		keyword = "%" + keyword + "%";
		return repository.findAllByKeyword(keyword);
	}

	public Customer getById(Integer id) {
		return repository.findById(id).orElse(null);
	}

	public Integer getConsumption(Customer cust) {
		Integer consumption = soRepository.getConsumptionByCustomer(cust);
		return consumption == null ? 0 : consumption;
	}

	public Integer getOrderTotal(Customer cust) {
		Integer orderTotal = soRepository.getOrderTotalByCustomer(cust);
		return orderTotal == null ? 0 : orderTotal;
	}

	public Integer getConsignTotal(Customer cust) {
		Integer consignTotal = cRepository.getConsignTotalByCustomer(cust);
		return consignTotal == null ? 0 : consignTotal;
	}

	public void save(Customer customer) {
		repository.save(customer);
	}

//    public void delete(Integer id) {
//        repository.deleteById(id);
//    }
	
	//刪除帳號
	@Transactional
	public Response delete(Integer id) {
		Response response = new Response();
		Customer cust = repository.findById(id).orElse(null);

		if (cust != null) {
//			採用硬刪除（相關資料紀錄全部刪除）
			try {
				List<CustomerAddress> addressList = aRepository.findByCustomer(cust);
				List<CustomerFavorites> favoriteList = cfRepository.findAllByCustomer(cust);
				List<Consignment> consignList = cRepository.findAllByCustomer(cust);
				List<Receivable> receivableList = rRepository.findAllByCustomer(cust);
				List<SalesOrder> salesOrderList = soRepository.findAllByCustomer(cust);
				List<Withdrawal> withdrawalList = wRepository.findAllByCustomer(cust);

//				先移除外鍵，再刪除
				if (addressList.size() > 0) {
					for (CustomerAddress item : addressList) {
						item.setCustomer(null);
						aRepository.delete(item);
					}
				}

				if (favoriteList.size() > 0) {
					for (CustomerFavorites item : favoriteList) {
						item.setCustomer(null);
						cfRepository.delete(item);
					}
				}

				if (consignList.size() > 0) {
					for (Consignment consign : consignList) {
						consign.setCustomer(null);
						consign.setWithdrawal(null);
						cRepository.delete(consign);
					}
				}

				if (receivableList.size() > 0) {
					for (Receivable receivable : receivableList) {
						receivable.setCustomer(null);
						receivable.setOrder(null);
						rRepository.delete(receivable);
					}
				}

				if (salesOrderList.size() > 0) {
					for (SalesOrder salesOrder : salesOrderList) {
						salesOrder.setCustomer(null);
						soRepository.delete(salesOrder);
					}
				}

				if (withdrawalList.size() > 0) {
					for (Withdrawal withdrawal : withdrawalList) {
						withdrawal.setCustomer(null);
						wRepository.delete(withdrawal);
					}
				}

				repository.deleteById(id);
				response.setSuccess(true);

			} catch (Exception e) {
				System.out.println(e);
				response.setSuccess(false);
			}

		} else {
			response.setSuccess(false);
			response.setMesg("查無此顧客，刪除失敗");
		}

		return response;
	}

//	檢查 email
	public Response checkEmail(String email) {
		Response response = new Response();
		if (repository.findByEmail(email).isPresent()) {
			response.setSuccess(false);
			response.setMesg("Email 已被註冊");
		} else {
			response.setSuccess(true);
		}
		return response;
	}

//	寄送驗證信
	public Response sendEmail(String email, String name) {
		Response response = new Response();
		System.out.println("sendEmail: " + email);
		try {
			SendMailUtils.sendVerifyEmail(email, name);
			response.setSuccess(true);
		} catch (Exception e) {
			System.out.println(e);
			response.setSuccess(false);
			response.setMesg("信件寄送有誤");
		}

		return response;

	}

	public Response verifyEmail(String code) {
		Response response = new Response();
		if (SendMailUtils.verifySuccess(code)) {
			response.setSuccess(true);
		} else {
			response.setSuccess(false);
			response.setMesg("驗證碼錯誤，請重新輸入");
		}
		return response;
	}

//    註冊抓註冊時間、給點數100點
	public Customer register(String name, String email, String password) {
		Customer customer = new Customer();
		customer.setName(name);
		customer.setEmail(email);
		
		 // 密碼加密（BCrypt）
	    String hashedPassword = passwordEncoder.encode(password);
	    customer.setPassword(hashedPassword);
	    
		customer.setCreatedAt(LocalDateTime.now());
		customer.setFirstLoginAt(LocalDateTime.now());
		customer.setPoints(100);

		return repository.save(customer);
	}

//    登入比對
	
	public boolean checkLogin(String email, String rawPassword) {
	    Optional<Customer> optional = repository.findByEmail(email);
	    if (optional.isEmpty()) return false;

	    String hashedPassword = optional.get().getPassword();
	    return passwordEncoder.matches(rawPassword, hashedPassword);
	}
	public Optional<Customer> findByEmail(String email) {
		return repository.findByEmail(email);
	}
	public Optional<Customer> findById(Integer id) {
		return repository.findById(id);
	}

//	Google 登入
	public Response googleLogin(String token, HttpSession session) {
		Response response = new Response();
		System.out.println("token: " + token);

		GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), new JacksonFactory())
                .setAudience(Collections.singletonList(GOOGLE_CLIENT_ID)) // 你的 Google client id
                .build();

        GoogleIdToken idToken = null;
        try {
            idToken = verifier.verify(token);
            Payload payload = idToken.getPayload();
            
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String subId = payload.getSubject(); // Google 帳號唯一 id
            
            System.out.println("email: " + email);
            System.out.println("name: " + name);
            System.out.println("subId: " + subId);
            
            Customer cust = repository.findByGoogleId(subId).orElse(null);
            
            if (cust == null) {
				cust = findByEmail(email).orElse(null);
				if(cust == null) {
//					建立新的顧客資料
					cust = new Customer();
					cust.setEmail(email);
					cust.setName(name);
					cust.setGoogleId(subId);
					cust.setCreatedAt(LocalDateTime.now());
					cust.setFirstLoginAt(LocalDateTime.now());
					cust.setPoints(100);
					Integer custId = repository.save(cust).getId();
					session.setAttribute("customerId", custId);
					
	            	response.setMesg("登入成功，歡迎加入 PikaBaby 嬰幼兒商品專賣店會員。");            	
				}
				else {
//					將 googleId 存到對應的顧客資料
					cust.setGoogleId(subId);
					Integer custId = repository.save(cust).getId();
					session.setAttribute("customerId", custId);
					
	            	response.setMesg("登入成功");
				}
			}
            else {
    			Integer custId = cust.getId();
    			session.setAttribute("customerId", custId);
            	response.setMesg("登入成功");            	
            }

            response.setSuccess(true);
            
        } catch (Exception e) {
        	System.out.println(e);
        	response.setSuccess(false);
        	response.setMesg("token 驗證有誤");
        }
		return response;
	}
	
	// ===== 點數相關業務邏輯 =====

	/**
	 * 根據會員ID獲取點數
	 */
	public Customer getCustomerWithPoints(Integer customerId) {
		return repository.findById(customerId).orElse(null);
	}

	/**
	 * 根據姓名或Email查詢會員點數
	 */
	public Customer findCustomerByNameOrEmail(String name, String email) {
		Customer customer = null;

		// 優先用email查詢
		if (email != null && !email.trim().isEmpty()) {
			customer = findByEmail(email).orElse(null);
		}

		// 如果email查不到，再用name查詢
		if (customer == null && name != null && !name.trim().isEmpty()) {
			List<Customer> customers = repository.findByName(name);
			if (customers != null && !customers.isEmpty()) {
				customer = customers.get(0); // 取第一個找到的
			}
		}

		return customer;
	}

	/**
	 * 驗證點數是否足夠使用
	 */
	public boolean validatePointsUsage(Customer customer, Integer pointsToUse) {
		if (pointsToUse == null || pointsToUse <= 0) {
			return true; // 不使用點數，驗證通過
		}

		Integer currentPoints = customer.getPoints() != null ? customer.getPoints() : 0;
		return currentPoints >= pointsToUse;
	}

	/**
	 * 扣除會員點數
	 */
	public void deductPoints(Customer customer, Integer pointsToDeduct) {
		if (pointsToDeduct == null || pointsToDeduct <= 0) {
			return;
		}

		Integer currentPoints = customer.getPoints() != null ? customer.getPoints() : 0;
		if (currentPoints >= pointsToDeduct) {
			customer.setPoints(currentPoints - pointsToDeduct);
			repository.save(customer);
		} else {
			throw new RuntimeException("點數不足！目前有 " + currentPoints + " 點，但要使用 " + pointsToDeduct + " 點");
		}
	}

	/**
	 * 增加會員點數（購物回饋）
	 */
	public void addPoints(Customer customer, Integer pointsToAdd) {
		if (pointsToAdd == null || pointsToAdd <= 0) {
			return;
		}

		Integer currentPoints = customer.getPoints() != null ? customer.getPoints() : 0;
		customer.setPoints(currentPoints + pointsToAdd);
		repository.save(customer);
	}

	/**
	 * 計算購物回饋點數（每消費100元得1點）
	 */
	public Integer calculateEarnedPoints(double totalAmount) {
		return (int) Math.floor(totalAmount / 100);
	}

	/**
	 * 獲取或創建客戶（用於前台訂單）
	 */
	public Customer getOrCreateCustomer(String customerName, String phone, String email, String address) {
		List<Customer> existingCustomers = repository.findByName(customerName);
		Customer customer;
		System.out.println("address: " + address);
		CustomerAddress custAddress;
		String city = address.substring(0, 3);
		String district = address.substring(3, 6);
		String street = address.substring(6);

		if (existingCustomers != null && !existingCustomers.isEmpty()) {
			// 使用第一個找到的客戶，但更新其資訊
			customer = existingCustomers.get(0);
			customer.setPhone(phone);
			customer.setEmail(email);
//			customer.setAddress(address);
			repository.save(customer);

//			將地址修改到會員地址(訂單地址)
			custAddress = aRepository.findByCustomerAndIsDefaultOrderTrue(customer);
			custAddress.setCity(city);
			custAddress.setDistrict(district);
			custAddress.setStreet(street);
//			待處理：更新zipcode
//			custAddress.setZipcode();
			aRepository.save(custAddress);

		} else {
			// 創建新客戶
			customer = new Customer();
			customer.setName(customerName);
			customer.setPhone(phone);
			customer.setEmail(email);
//			customer.setAddress(address);
			customer.setPoints(100); // 新會員預設100點
			repository.save(customer);

//			將地址新增到會員地址(訂單地址)
			custAddress = new CustomerAddress();
			custAddress.setCustomer(customer);
			custAddress.setName(customerName);
			custAddress.setCity(city);
			custAddress.setDistrict(district);
			custAddress.setStreet(street);
			custAddress.setIsDefaultOrder(true);
//			待處理：更新zipcode
//			custAddress.setZipcode();
			aRepository.save(custAddress);

		}

		return customer;
	}

	private LocalDate getLocalDate(String date) {
		return LocalDate.of(Integer.parseInt(date.split("-")[0]), Integer.parseInt(date.split("-")[1]),
				Integer.parseInt(date.split("-")[2]));
	}
}
