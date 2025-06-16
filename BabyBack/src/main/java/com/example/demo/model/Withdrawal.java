package com.example.demo.model;

import java.util.Date;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.Column;

//import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

//	提款申請
@Entity
@Table(name = "WithDrawal")
public class Withdrawal {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private Integer amount;
	
	@Temporal(TemporalType.DATE)
	private Date applyDate;
	private Integer withdraw;

	@Temporal(TemporalType.DATE)
	private Date withdrawDate;
	private String bankAccount;
	
	public Integer getId() {
		return id;
	}	
	public void setId(Integer id) {
		this.id = id;
	}	
	public Integer getAmount() {
		return amount;
	}	
	public void setAmount(Integer amount) {
		this.amount = amount;
	}	
	public Date getApplyDate() {
		return applyDate;
	}	
	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}	
	public Integer getWithdraw() {
		return withdraw;
	}	
	public void setWithdraw(Integer withdraw) {
		this.withdraw = withdraw;
	}	
	public Date getWithdrawDate() {
		return withdrawDate;
	}
	public void setWithdrawDate(Date withdrawDate) {
		this.withdrawDate = withdrawDate;
	}	
	public String getBankAccount() {
		return bankAccount;
	}	
	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}
	
//	------------------------------------
	
	@ManyToOne
	@JoinColumn(name = "custId")
	private Customer customer;

	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
//	-----------------------------------
	
	@ManyToOne
	@JoinColumn(name = "bankId")
	private BankNo bankNo;

	public BankNo getBankNo() {
		return bankNo;
	}
	public void setBankNo(BankNo bankNo) {
		this.bankNo = bankNo;
	}
	
	
}
