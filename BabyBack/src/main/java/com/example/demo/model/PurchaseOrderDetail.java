package com.example.demo.model;

import java.math.BigDecimal;

import jakarta.persistence.*;

@Entity
public class PurchaseOrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "purchase_order_id")
    private PurchaseOrder order;

    @ManyToOne
    @JoinColumn(name = "supplier_product_id")
    private SupplierProduct supplierProduct;
    
    private Long quantity;
    
    
    private BigDecimal unitPrice;
    
    
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public PurchaseOrder getOrder() {
		return order;
	}
	public void setOrder(PurchaseOrder order) {
		this.order = order;
	}
	
	public Long getQuantity() {
		return quantity;
	}
	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}
	public BigDecimal getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}
	public SupplierProduct getSupplierProduct() {
		return supplierProduct;
	}
	public void setSupplierProduct(SupplierProduct supplierProduct) {
		this.supplierProduct = supplierProduct;
	}
   
}
