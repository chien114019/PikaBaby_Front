package com.example.demo.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.BankNo;
import com.example.demo.model.Consignment;
import com.example.demo.model.Customer;
import com.example.demo.model.Response;
import com.example.demo.model.Withdrawal;
import com.example.demo.repository.*;

@Service
public class WithdrawalService {

    private final SalesOrderDetailRepository salesOrderDetailRepository;

	@Autowired
	private WithdrawalRepository repository;
	
	@Autowired
	private CustomerRepository cRepository;
	
	@Autowired
	private ConsignmentRepository coRepository;
	
	@Autowired
	private BankRepository bRepository;
	
	Customer cust;

    WithdrawalService(SalesOrderDetailRepository salesOrderDetailRepository) {
        this.salesOrderDetailRepository = salesOrderDetailRepository;
    }

//	===========前台API===========
//	getWithdrawsByCustId
	public List<Withdrawal> getWithdrawsByCustId(String custId) {
		cust = cRepository.findById((int) Long.parseLong(custId)).orElse(null);
		System.out.println(repository.findAllByCustomer(cust).get(0).getBankNo().getbCode());
		return repository.findAllByCustomer(cust);
	}
	
//	getAllByCustAndDateBetweenAndWithdraw
	public List<Withdrawal> getAllByCustAndDateBetweenAndWithdraw(String custId, String start, String end, String withdraw) throws Exception {
		cust = cRepository.findById((int) Long.parseLong(custId)).orElse(null);
		return repository.findAllByCustomerAndApplyDateBetweenAndWithdraw(cust, formatter(start), formatter(end), 
				Integer.parseInt(withdraw));
	}
	
//	getAllByCustAndDateBetween
	public List<Withdrawal> getAllByCustAndDateBetween(String custId, String start, String end) throws Exception {
		cust = cRepository.findById((int) Long.parseLong(custId)).orElse(null);
		return repository.findAllByCustomerAndApplyDateBetween(cust, formatter(start), formatter(end));
	}
	
//	getAllByCustAndDateAfterAndWithdraw
	public List<Withdrawal> getAllByCustAndDateAfterAndWithdraw(String custId, String start, String withdraw) throws Exception {
		cust = cRepository.findById((int) Long.parseLong(custId)).orElse(null);
		return repository.findAllByCustomerAndApplyDateGreaterThanEqualAndWithdraw(cust, formatter(start), Integer.parseInt(withdraw));
	}
	
//	getAllByCustAndDateAfter
	public List<Withdrawal> getAllByCustAndDateAfter(String custId, String start) throws Exception {
		cust = cRepository.findById((int) Long.parseLong(custId)).orElse(null);
		return repository.findAllByCustomerAndApplyDateGreaterThanEqual(cust, formatter(start));
	}

//	getAllByCustAndDateBeforeAndWithdraw
	public List<Withdrawal> getAllByCustAndDateBeforeAndWithdraw(String custId, String end, String withdraw) throws Exception {
		cust = cRepository.findById((int) Long.parseLong(custId)).orElse(null);
		return repository.findAllByCustomerAndApplyDateLessThanEqualAndWithdraw(cust, formatter(end), Integer.parseInt(withdraw));
	}

//	getAllByCustAndDateBefore
	public List<Withdrawal> getAllByCustAndDateBefore(String custId, String end) throws Exception {
		cust = cRepository.findById((int) Long.parseLong(custId)).orElse(null);
		return repository.findAllByCustomerAndApplyDateLessThanEqual(cust, formatter(end));
	}
	
//	getAllByCustAndWithdraw
	public List<Withdrawal> getAllByCustAndWithdraw(String custId, String withdraw) throws Exception {
		cust = cRepository.findById((int) Long.parseLong(custId)).orElse(null);
		return repository.findAllByCustomerAndWithdraw(cust, Integer.parseInt(withdraw));
	}
	
//	deleteWithdrawById
	public Response deleteWithdrawById(String id) {
		Response response = new Response();
		Withdrawal withdraw = repository.findById(Integer.parseInt(id)).orElse(null);
		if (withdraw != null) {
			try {
				List<Consignment> consigns = coRepository.findAllByWithdrawal(withdraw);
				for (Consignment consign : consigns) {
					consign.setWithdrawal(null);
					coRepository.save(consign);
				}
				repository.deleteById(Integer.parseInt(id));
				response.setSuccess(true);
			} catch (Exception e) {
				System.out.println(e);
				response.setSuccess(false);
			}
			
		} else {
			response.setSuccess(false);
		}
		
		return response;	
	}
	
//	getStorageByCust
	public Map<String, Object> getStorageByCust(String custId) {
		Map<String, Object> map = new HashMap();
		List<Consignment> storages = coRepository.getStorageByCust(Long.parseLong(custId));
		Integer total = 0;
		List<String> idList = new ArrayList();

		if(storages.size() > 0) {			
			for (Consignment storage : storages) {
				total += storage.getPrice();
				idList.add(storage.getId().toString());
			}			
		}
		
		map.put("total", total.toString());
		map.put("ids", idList);
		return map;
	}
	
//	createWithdraw
	public Response createWithdraw(Map<String, Object> body, String custId) {
		Response response = new Response();
//		String custId = body.get("custId").toString();
		String amount = body.get("amount").toString();
		String bankId = body.get("bankId").toString();
		String bankAccount = body.get("bankAccount").toString();
		
		Customer cust = cRepository.findById((int) Long.parseLong(custId)).orElse(null);
		BankNo bank = bRepository.findById(Integer.parseInt(bankId)).orElse(null);
		
		if(cust != null && bank != null) {
			Withdrawal withdraw = new Withdrawal();
			withdraw.setCustomer(cust);
			withdraw.setAmount(Integer.parseInt(amount));
			withdraw.setBankNo(bank);
			withdraw.setBankAccount(bankAccount);
			withdraw.setApplyDate(new Date());
			withdraw.setWithdraw(0);
			
			Withdrawal newWithdraw = repository.save(withdraw);
			
			List<String> consignIds = (List<String>) body.get("ids");
			for (String id : consignIds) {
//				System.out.println(id.getClass());
				Consignment target = coRepository.findById(Integer.parseInt(id)).orElse(null);
				target.setWithdrawal(newWithdraw);
				coRepository.save(target);
			}
			
			response.setSuccess(true);
		}
		else {
			response.setSuccess(false);
			response.setMesg(cust == null? "查無此顧客":"" + bank == null? "查無此銀行":"");
		}
		return response;
	}
	
//	===========後台API===========
	public List<Withdrawal> getAll() {
		return repository.findAll();
	}
	
	public Withdrawal getById(String id) {
		return repository.findById(Integer.parseInt(id)).orElse(null);
	}
	
//	getAllByDatesAndwithdrawAndCustName
	public List<Withdrawal> getAllByDatesAndWithdrawAndCustName(String applySDate, String applyEDate, String withdrawSDate, 
			String withdrawEDate, String withdraw, String custName) throws Exception {
		
		return repository.findAllByDatesAndWithdrawAndCustName(formatter(applySDate), formatter(applyEDate), formatter(withdrawSDate), 
				formatter(withdrawEDate), Integer.parseInt(withdraw), "%" + custName + "%");
	}
	
//	getAllByDatesAndWithdraw
	public List<Withdrawal> getAllByDatesAndWithdraw(String applySDate, String applyEDate, String withdrawSDate, 
			String withdrawEDate, String withdraw) throws Exception {

		return repository.findAllByApplyDateBetweenAndWithdrawDateBetweenAndWithdraw(formatter(applySDate), formatter(applyEDate), 
				formatter(withdrawSDate), formatter(withdrawEDate), Integer.parseInt(withdraw));
	}
	
//	getAllByDatesAndCustName
	public List<Withdrawal> getAllByDatesAndCustName(String applySDate, String applyEDate, String withdrawSDate, 
			String withdrawEDate, String custName) throws Exception {
		
		return repository.findAllByDatesAndCustName(formatter(applySDate), formatter(applyEDate), formatter(withdrawSDate), 
				formatter(withdrawEDate), "%" + custName + "%");
	}
	
//	getAllByDates
	public List<Withdrawal> getAllByDates(String applySDate, String applyEDate, String withdrawSDate, String withdrawEDate) 
			throws Exception {
		
		return repository.findAllByApplyDateBetweenAndWithdrawDateBetween(formatter(applySDate), formatter(applyEDate), 
				formatter(withdrawSDate), formatter(withdrawEDate));
	}
	
//	getAllByApplyDateAndWithdrawAndCustName
	public List<Withdrawal> getAllByApplyDateAndWithdrawAndCustName(String applySDate, String applyEDate, String withdraw, 
			String custName) throws Exception {
		
		return repository.findAllByApplyDateAndWithdrawAndCustName(formatter(applySDate), formatter(applyEDate), 
				Integer.parseInt(withdraw), "%" + custName + "%");
	}
	
//	getAllByApplyDateAndWithdraw
	public List<Withdrawal> getAllByApplyDateAndWithdraw(String applySDate, String applyEDate, String withdraw) 
			throws Exception {
		
		return repository.findAllByApplyDateBetweenAndWithdraw(formatter(applySDate), formatter(applyEDate), 
				Integer.parseInt(withdraw));
	}
	
//	getAllByApplyDateAndCustName
	public List<Withdrawal> getAllByApplyDateAndCustName(String applySDate, String applyEDate, String custName) 
			throws Exception {
		return repository.findAllByApplyDateAndCustName(formatter(applySDate), formatter(applyEDate), "%" + custName + "%");
	}
	
//	getAllByApplyDate
	public List<Withdrawal> getAllByApplyDate(String applySDate, String applyEDate) 
			throws Exception {
		return repository.findAllByApplyDateBetween(formatter(applySDate), formatter(applyEDate));
	}
	
//	getAllByWithdrawDatesAndWithdrawAndCustName
	public List<Withdrawal> getAllByWithdrawDatesAndWithdrawAndCustName(String withdrawSDate, String withdrawEDate, String withdraw, 
			String custName) 
			throws Exception {
		return repository.findAllByWithdrawDateAndWithdrawAndCustName(formatter(withdrawSDate), formatter(withdrawEDate), 
				Integer.parseInt(withdraw), "%" + custName + "%");
	}
	
//	getAllByWithdrawDatesAndWithdraw
	public List<Withdrawal> getAllByWithdrawDatesAndWithdraw(String withdrawSDate, String withdrawEDate, String withdraw) 
			throws Exception {
		return repository.findAllByWithdrawDateBetweenAndWithdraw(formatter(withdrawSDate), formatter(withdrawEDate), 
				Integer.parseInt(withdraw));
	}
	
//	getAllByWithdrawDatesAndCustName
	public List<Withdrawal> getAllByWithdrawDatesAndCustName(String withdrawSDate, String withdrawEDate, String custName) 
			throws Exception {
		return repository.findAllByWithdrawDateAndCustName(formatter(withdrawSDate), formatter(withdrawEDate), "%" + custName + "%");
	}
	
//	getAllByWithdrawDates
	public List<Withdrawal> getAllByWithdrawDates(String withdrawSDate, String withdrawEDate) 
			throws Exception {
		return repository.findAllByWithdrawDateBetween(formatter(withdrawSDate), formatter(withdrawEDate));
	}
	
//	getAllByWithdrawAndCustName
	public List<Withdrawal> getAllByWithdrawAndCustName(String withdraw, String custName) 
			throws Exception {
		return repository.findAllByWithdrawAndCustName(Integer.parseInt(withdraw), "%" + custName + "%");
	}
	
//	getAllByWithdraw
	public List<Withdrawal> getAllByWithdraw(String withdraw) 
			throws Exception {
		return repository.findAllByWithdraw(Integer.parseInt(withdraw));
	}
	
//	getAllByCustName
	public List<Withdrawal> getAllByCustName(String custName) 
			throws Exception {
		return repository.findAllByCustName("%" + custName + "%");
	}	
	
//	editWithdraw
	public Response editWithdraw(String id, String withdraw, String withdrawDate) throws Exception {
		Response response = new Response();
		Withdrawal target = repository.findById(Integer.parseInt(id)).orElse(null);

		if (target != null) {
			target.setWithdraw(Integer.parseInt(withdraw));
			target.setWithdrawDate(formatter(withdrawDate));
			repository.save(target);

			response.setSuccess(true);
			response.setMesg("匯款登陸成功");
			
		} else {
			response.setSuccess(true);
			response.setMesg("查無紀錄，匯款登陸失敗");
		}
		
		return response;
	}
	
	private Date formatter(String dateStr) throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.parse(dateStr);
	}
}
