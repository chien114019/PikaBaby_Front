package com.example.demo.model.Linepay;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductPackageForm {
	public ProductPackageForm() {}
	
//	套裝產品ID
	private String id;
	
//	套裝產品的​​名稱或配送的合作分店名稱
	private String name;
	
//	套裝產品總購買金額（不同產品購買金額總和）
	private BigDecimal amount;
	
//	套裝產品的產品訊息
	private List<ProductForm> products;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public BigDecimal getAmount() {
		return amount;
	}
	
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public List<ProductForm> getProducts() {
		return products;
	}

	public void setProducts(List<ProductForm> products) {
		this.products = products;
	}
	
	
}
