package com.example.demo.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.Customer;
import com.example.demo.model.Withdrawal;

public interface WithdrawalRepository extends JpaRepository<Withdrawal, Integer> {

//	============前台API=============
	List<Withdrawal> findAllByCustomer(Customer cust);

	List<Withdrawal> findAllByCustomerAndApplyDateBetweenAndWithdraw(Customer cust, Date start, Date end,
			Integer withdraw);

	List<Withdrawal> findAllByCustomerAndApplyDateBetween(Customer cust, Date start, Date end);

	List<Withdrawal> findAllByCustomerAndApplyDateGreaterThanEqualAndWithdraw(Customer cust, Date start,
			Integer withdraw);

	List<Withdrawal> findAllByCustomerAndApplyDateGreaterThanEqual(Customer cust, Date start);

	List<Withdrawal> findAllByCustomerAndApplyDateLessThanEqualAndWithdraw(Customer cust, Date end, Integer withdraw);

	List<Withdrawal> findAllByCustomerAndApplyDateLessThanEqual(Customer cust, Date end);

	List<Withdrawal> findAllByCustomerAndWithdraw(Customer cust, Integer withdraw);

//	============後台API=============
	@Query("""
			SELECT w FROM Withdrawal w
			WHERE applyDate BETWEEN :applyStart AND :applyEnd
			AND
			withdrawDate BETWEEN :withdrawStart AND :withdrawEnd
			AND
			withdraw = :withdraw
			AND
			w.customer.name LIKE :custName
			""")
	List<Withdrawal> findAllByDatesAndWithdrawAndCustName(@Param("applyStart") Date applyStart,
			@Param("applyEnd") Date applyEnd, @Param("withdrawStart") Date withdrawStart,
			@Param("withdrawEnd") Date withdrawEnd, @Param("withdraw") Integer withdraw,
			@Param("custName") String custName);

	List<Withdrawal> findAllByApplyDateBetweenAndWithdrawDateBetweenAndWithdraw(Date applyStart, Date applyEnd,
			Date withdrawStart, Date withdrawEnd, Integer withdraw);

	@Query("""
			SELECT w FROM Withdrawal w
			WHERE applyDate BETWEEN :applyStart AND :applyEnd
			AND
			withdrawDate BETWEEN :withdrawStart AND :withdrawEnd
			AND
			w.customer.name LIKE :custName
			""")
	List<Withdrawal> findAllByDatesAndCustName(@Param("applyStart") Date applyStart, @Param("applyEnd") Date applyEnd,
			@Param("withdrawStart") Date withdrawStart, @Param("withdrawEnd") Date withdrawEnd,
			@Param("custName") String custName);

	List<Withdrawal> findAllByApplyDateBetweenAndWithdrawDateBetween(Date applyStart, Date applyEnd, Date withdrawStart,
			Date withdrawEnd);

	@Query("""
			SELECT w FROM Withdrawal w
			WHERE applyDate BETWEEN :applyStart AND :applyEnd
			AND
			withdraw = :withdraw
			AND
			w.customer.name LIKE :custName
			""")
	List<Withdrawal> findAllByApplyDateAndWithdrawAndCustName(@Param("applyStart") Date applyStart,
			@Param("applyEnd") Date applyEnd, @Param("withdraw") Integer withdraw, @Param("custName") String custName);

	List<Withdrawal> findAllByApplyDateBetweenAndWithdraw(Date applyStart, Date applyEnd, Integer withdraw);

	@Query("""
			SELECT w FROM Withdrawal w
			WHERE applyDate BETWEEN :applyStart AND :applyEnd
			AND
			w.customer.name LIKE :custName
			""")
	List<Withdrawal> findAllByApplyDateAndCustName(@Param("applyStart") Date applyStart,
			@Param("applyEnd") Date applyEnd, @Param("custName") String custName);

	List<Withdrawal> findAllByApplyDateBetween(Date applyStart, Date applyEnd);

	@Query("""
			SELECT w FROM Withdrawal w
			WHERE withdrawDate BETWEEN :withdrawStart AND :withdrawEnd
			AND
			withdraw = :withdraw
			AND
			w.customer.name LIKE :custName
			""")
	List<Withdrawal> findAllByWithdrawDateAndWithdrawAndCustName(@Param("withdrawStart") Date withdrawStart,
			@Param("withdrawEnd") Date withdrawEnd, @Param("withdraw") Integer withdraw,
			@Param("custName") String custName);

	List<Withdrawal> findAllByWithdrawDateBetweenAndWithdraw(Date withdrawSDate, Date withdrawEDate, Integer withdraw);

	@Query("""
			SELECT w FROM Withdrawal w
			WHERE withdrawDate BETWEEN :withdrawStart AND :withdrawEnd
			AND
			w.customer.name LIKE :custName
			""")
	List<Withdrawal> findAllByWithdrawDateAndCustName(@Param("withdrawStart") Date withdrawStart,
			@Param("withdrawEnd") Date withdrawEnd, @Param("custName") String custName);

	List<Withdrawal> findAllByWithdrawDateBetween(Date withdrawSDate, Date withdrawEDate);

	@Query("""
			SELECT w FROM Withdrawal w
			WHERE withdraw = :withdraw
			AND
			w.customer.name LIKE :custName
			""")
	List<Withdrawal> findAllByWithdrawAndCustName(@Param("withdraw") Integer withdraw,
			@Param("custName") String custName);

	List<Withdrawal> findAllByWithdraw(Integer withdraw);

	@Query("""
			SELECT w FROM Withdrawal w
			WHERE w.customer.name LIKE :custName
			""")
	List<Withdrawal> findAllByCustName(@Param("custName") String custName);
}
