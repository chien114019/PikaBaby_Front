package com.example.demo.model.Linepay;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RedirectUrls {
	public RedirectUrls() {}
	
//	顧客完成 LINE Pay 認證後重新導向的URL
	private String confirmUrl;
	
//	顧客在LINE應用程式的支付畫面取消付款時重新導向的URL
	private String cancelUrl;

	public String getConfirmUrl() {
		return confirmUrl;
	}

	public void setConfirmUrl(String confirmUrl) {
		this.confirmUrl = confirmUrl;
	}

	public String getCancelUrl() {
		return cancelUrl;
	}

	public void setCancelUrl(String cancelUrl) {
		this.cancelUrl = cancelUrl;
	}
	
	
}
