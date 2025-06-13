package com.example.demo.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Customer;
import com.example.demo.model.Response;
import com.example.demo.model.Withdrawal;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.WithdrawalRepository;

@Service
public class WithdrawalService {

	@Autowired
	private WithdrawalRepository repository;
	
	@Autowired
	private CustomerRepository cRepository;
	
	Customer cust;

//	===========前台API===========
//	getWithdrawsByCustId
	public List<Withdrawal> getWithdrawsByCustId(String custId) {
		cust = cRepository.findById(Long.parseLong(custId)).orElse(null);
		return repository.findAllByCustomer(cust);
	}
	
//	getAllByCustAndDateBetweenAndWithdraw
	public List<Withdrawal> getAllByCustAndDateBetweenAndWithdraw(String custId, String start, String end, String withdraw) throws Exception {
		cust = cRepository.findById(Long.parseLong(custId)).orElse(null);
		return repository.findAllByCustomerAndApplyDateBetweenAndWithdraw(cust, formatter(start), formatter(end), 
				Integer.parseInt(withdraw));
	}
	
//	getAllByCustAndDateBetween
	public List<Withdrawal> getAllByCustAndDateBetween(String custId, String start, String end) throws Exception {
		cust = cRepository.findById(Long.parseLong(custId)).orElse(null);
		return repository.findAllByCustomerAndApplyDateBetween(cust, formatter(start), formatter(end));
	}
	
//	getAllByCustAndDateAfterAndWithdraw
	public List<Withdrawal> getAllByCustAndDateAfterAndWithdraw(String custId, String start, String withdraw) throws Exception {
		cust = cRepository.findById(Long.parseLong(custId)).orElse(null);
		return repository.findAllByCustomerAndApplyDateGreaterThanEqualAndWithdraw(cust, formatter(start), Integer.parseInt(withdraw));
	}
	
//	getAllByCustAndDateAfter
	public List<Withdrawal> getAllByCustAndDateAfter(String custId, String start) throws Exception {
		cust = cRepository.findById(Long.parseLong(custId)).orElse(null);
		return repository.findAllByCustomerAndApplyDateGreaterThanEqual(cust, formatter(start));
	}

//	getAllByCustAndDateBeforeAndWithdraw
	public List<Withdrawal> getAllByCustAndDateBeforeAndWithdraw(String custId, String end, String withdraw) throws Exception {
		cust = cRepository.findById(Long.parseLong(custId)).orElse(null);
		return repository.findAllByCustomerAndApplyDateLessThanEqualAndWithdraw(cust, formatter(end), Integer.parseInt(withdraw));
	}

//	getAllByCustAndDateBefore
	public List<Withdrawal> getAllByCustAndDateBefore(String custId, String end) throws Exception {
		cust = cRepository.findById(Long.parseLong(custId)).orElse(null);
		return repository.findAllByCustomerAndApplyDateLessThanEqual(cust, formatter(end));
	}
	
//	getAllByCustAndWithdraw
	public List<Withdrawal> getAllByCustAndWithdraw(String custId, String withdraw) throws Exception {
		cust = cRepository.findById(Long.parseLong(custId)).orElse(null);
		return repository.findAllByCustomerAndWithdraw(cust, Integer.parseInt(withdraw));
	}
	
//	deleteWithdrawById
	public void deleteWithdrawById(String id) {
		repository.deleteById(Integer.parseInt(id));
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
