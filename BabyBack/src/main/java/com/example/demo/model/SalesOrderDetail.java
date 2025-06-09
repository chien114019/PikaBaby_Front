package com.example.demo.model;

import jakarta.persistence.*;

@Entity
public class SalesOrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private SalesOrder order;

    @ManyToOne
    private Product product;

    private Long quantity;
    private Double unitPrice;
    
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public SalesOrder getOrder() {
		return order;
	}
	public void setOrder(SalesOrder order) {
		this.order = order;
	}
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public Long getQuantity() {
		return quantity;
	}
	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}
	public Double getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}
	 public double getSubTotal() {
	        return quantity * unitPrice;
	    }
	 
   
}
