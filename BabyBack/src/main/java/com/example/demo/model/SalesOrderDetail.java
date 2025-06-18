package com.example.demo.model;

import java.math.BigDecimal;

import jakarta.persistence.*;

@Entity
public class SalesOrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private SalesOrder order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    
    @ManyToOne
    @JoinColumn(name = "supplier_product_id")
    private SupplierProduct supplierProduct;



    private Long quantity;
    private BigDecimal unitPrice;
    
    
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
	public BigDecimal getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(BigDecimal bigDecimal) {
		this.unitPrice = bigDecimal;
	}
	 public BigDecimal getSubTotal() {
	        return unitPrice.multiply(BigDecimal.valueOf(quantity));
	}
	public SupplierProduct getSupplierProduct() {
		return supplierProduct;
	}
	public void setSupplierProduct(SupplierProduct supplierProduct) {
		this.supplierProduct = supplierProduct;
	}
	
	
	 
   
}
