package com.example.demo.dto;

import java.util.Date;

public class ConsignDTO {
	private String custId;
	private String pType;
	private String productName;
	private String pCondition;
	private Integer quantity;
	private Integer delivery;
	private Date deliveryDate;
	private String produceYear;
	
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	public String getpType() {
		return pType;
	}
	public void setpType(String pType) {
		this.pType = pType;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getpCondition() {
		return pCondition;
	}
	public void setpCondition(String pCondition) {
		this.pCondition = pCondition;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public Integer getDelivery() {
		return delivery;
	}
	public void setDelivery(Integer delivery) {
		this.delivery = delivery;
	}
	public Date getDeliveryDate() {
		return deliveryDate;
	}
	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}
	public String getProduceYear() {
		return produceYear;
	}
	public void setProduceYear(String produceYear) {
		this.produceYear = produceYear;
	}
	
}
