package com.example.demo.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    
    private BigDecimal price;
    
    private String specification;
    
    private String color;
    
    private String note;
    
    private String imageUrl;
    
    private String description;
    
    @Column(nullable = false)
    private Boolean deleted = false;
    
    @Column(name = "is_published")
    private Boolean published;
    
    private boolean age1;	// 0-3M
    private boolean age2;	// 3-6M
    private boolean age3;	// 6-12M
    private boolean age4;	// 2-3Y
    
    @OneToMany(mappedBy = "product")
    private List<SupplierProduct> supplierProducts;
    
    @OneToMany(mappedBy = "product")
    private List<ProductImage> images = new ArrayList<>();
    
	@ManyToOne
	@JoinColumn(name = "type")
	private ProductType productType;
   
    @Transient
    private Long stock;
   
	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
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


	public Boolean isPublished() {
		return published;
	}


	public void setPublished(Boolean published) {
		this.published = published;
	}


	public String getImageUrl() {
		return imageUrl;
	}


	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public Boolean getDeleted() {
		return deleted;
	}


	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}


	public Boolean getPublished() {
		return published;
	}


	public boolean isAge1() {
		return age1;
	}


	public void setAge1(boolean age1) {
		this.age1 = age1;
	}


	public boolean isAge2() {
		return age2;
	}


	public void setAge2(boolean age2) {
		this.age2 = age2;
	}


	public boolean isAge3() {
		return age3;
	}


	public void setAge3(boolean age3) {
		this.age3 = age3;
	}


	public boolean isAge4() {
		return age4;
	}


	public void setAge4(boolean age4) {
		this.age4 = age4;
	}


	public ProductType getProductType() {
		return productType;
	}


	public void setProductType(ProductType productType) {
		this.productType = productType;
	}
	
	
	
	
	
	
}
