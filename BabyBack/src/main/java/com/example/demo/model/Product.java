package com.example.demo.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    private BigDecimal price;
    
    private String specification;
    
    private String color;
    
    private String note;

    //0611喬新增
    @OneToMany(mappedBy = "product")
    private List<PurchaseOrderDetail> purchaseDetails;
    
    @OneToMany(mappedBy = "product")
    private List<SupplierProduct> supplierProducts;
    
    @OneToMany(mappedBy = "product")
    private List<ProductImage> images = new ArrayList<>();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;
    
    @Transient
    private Long stock;

    // 加上一個 getter：計算目前庫存
    @Transient
    public long getCurrentStock() {
        if (purchaseDetails == null) return 0;
        return purchaseDetails.stream()
                .mapToLong(PurchaseOrderDetail::getQuantity)
                .sum();
    }

	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public BigDecimal getPrice() {
		return price;
	}


	public void setPrice(BigDecimal price) {
		this.price = price;
	}


	public String getSpecification() {
		return specification;
	}


	public void setSpecification(String specification) {
		this.specification = specification;
	}


	public String getColor() {
		return color;
	}


	public void setColor(String color) {
		this.color = color;
	}


	public List<PurchaseOrderDetail> getPurchaseDetails() {
		return purchaseDetails;
	}


	public void setPurchaseDetails(List<PurchaseOrderDetail> purchaseDetails) {
		this.purchaseDetails = purchaseDetails;
	}


	public List<SupplierProduct> getSupplierProducts() {
		return supplierProducts;
	}


	public void setSupplierProducts(List<SupplierProduct> supplierProducts) {
		this.supplierProducts = supplierProducts;
	}


	public List<ProductImage> getImages() {
		return images;
	}


	public void setImages(List<ProductImage> images) {
		this.images = images;
	}


	public Supplier getSupplier() {
		return supplier;
	}


	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}


	public Long getStock() {
		return stock;
	}


	public void setStock(Long stock) {
		this.stock = stock;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
	
}
