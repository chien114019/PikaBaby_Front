package com.example.demo.model.Linepay;

import java.math.BigDecimal;

public class ConfirmData {
	public ConfirmData() {}
	
	private BigDecimal amount;
	private String currency;
	
	public BigDecimal getAmount() {
		return amount;
	}
	
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	public String getCurrency() {
		return currency;
	}
	
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	
}
