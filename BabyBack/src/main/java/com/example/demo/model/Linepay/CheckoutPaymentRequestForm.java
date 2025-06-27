package com.example.demo.model.Linepay;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CheckoutPaymentRequestForm {
	public CheckoutPaymentRequestForm() {}
	
//	整個套裝產品的購買金額
	private BigDecimal amount;
	
//	付款貨幣代碼
	private String currency;

//	訂單號碼
	private String orderId;
	
//	進行付款時跳轉的頁面
	private RedirectUrls redirectUrls;

//	套裝產品訊息
	private List<ProductPackageForm> packages;
	
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
	
	public String getOrderId() {
		return orderId;
	}
	
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	
	public List<ProductPackageForm> getPackages() {
		return packages;
	}
	
	public void setPackages(List<ProductPackageForm> packages) {
		this.packages = packages;
	}

	public RedirectUrls getRedirectUrls() {
		return redirectUrls;
	}

	public void setRedirectUrls(RedirectUrls redirectUrls) {
		this.redirectUrls = redirectUrls;
	}
	
	
}
