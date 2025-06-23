package com.example.demo.dto;

import java.math.BigDecimal;

public record ProductDto(
		Integer id, 
		String name,
		String imageUrl,
		String primaryImageUrl,
		String description, 
		BigDecimal price,
		Long stock,
		String productTypeName,
		Integer productTypeId) {
	
	// 向後兼容的建構子（不包含productType）
	public ProductDto(Integer id, String name, String imageUrl, String description, BigDecimal price, Long stock) {
		this(id, name, imageUrl, imageUrl, description, price, stock, null, null);
	}
	
	// 包含productType的建構子
	public ProductDto(Integer id, String name, String imageUrl, String primaryImageUrl, String description, BigDecimal price, Long stock, String productTypeName, Integer productTypeId) {
		this.id = id;
		this.name = name;
		this.imageUrl = imageUrl;
		this.primaryImageUrl = primaryImageUrl;
		this.description = description;
		this.price = price;
		this.stock = stock;
		this.productTypeName = productTypeName;
		this.productTypeId = productTypeId;
	}
}
