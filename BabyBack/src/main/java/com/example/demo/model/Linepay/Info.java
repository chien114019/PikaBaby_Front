package com.example.demo.model.Linepay;

public class Info {
	private PaymentUrl paymentUrl;
	private String transactionId;
	private String paymentAccessToken;
	private String orderId;
	
	
	public PaymentUrl getPaymentUrl() {
		return paymentUrl;
	}
	public void setPaymentUrl(PaymentUrl paymentUrl) {
		this.paymentUrl = paymentUrl;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public String getPaymentAccessToken() {
		return paymentAccessToken;
	}
	public void setPaymentAccessToken(String paymentAccessToken) {
		this.paymentAccessToken = paymentAccessToken;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
}