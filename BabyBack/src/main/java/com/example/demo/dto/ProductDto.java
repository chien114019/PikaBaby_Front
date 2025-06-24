package com.example.demo.dto;

import java.math.BigDecimal;

public record ProductDto(
		Integer id, 
		String name,
		String imageUrl,
		String primaryImageUrl,
		String description, 
		String productTypeName,
		Integer productTypeId) {
	
	// 新的建構子（不包含price和stock）
	public ProductDto(Integer id, String name, String imageUrl, String description) {
		this(id, name, imageUrl, imageUrl, description, null, null);
	}
	
	// 向後兼容的建構子（包含price和stock，但會忽略這些參數）
	@Deprecated
	public ProductDto(Integer id, String name, String imageUrl, String description, BigDecimal price, Long stock) {
		this(id, name, imageUrl, imageUrl, description, null, null);
	}
	
	// 向後兼容的建構子（包含productType、price和stock，但會忽略price和stock）
	@Deprecated
	public ProductDto(Integer id, String name, String imageUrl, String primaryImageUrl, String description, BigDecimal price, Long stock, String productTypeName, Integer productTypeId) {
		this(id, name, imageUrl, primaryImageUrl, description, productTypeName, productTypeId);
	}
	
	// 向後兼容的方法 - 返回null或預設值，提醒開發者需要從Product實體直接獲取
	@Deprecated
	public BigDecimal price() {
		return null; // 需要從Product實體的getLatestPrice()方法獲取
	}
	
	@Deprecated
	public Long stock() {
		return null; // 需要從Product實體的getCalculatedStock()方法獲取
	}
}
