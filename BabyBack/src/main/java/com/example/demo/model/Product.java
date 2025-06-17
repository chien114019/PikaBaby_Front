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
//    private Long stock;
    
    //0611喬新增
    @OneToMany(mappedBy = "product")
    private List<PurchaseOrderDetail> purchaseDetails;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;


    // 加上一個 getter：計算目前庫存
    @Transient
    public long getCurrentStock() {
        if (purchaseDetails == null) return 0;
        return purchaseDetails.stream()
                .mapToLong(PurchaseOrderDetail::getQuantity)
                .sum();
    }
    
    @Transient
    private Long stock;

    
    //0614喬新增 圖片關聯欄位
    @OneToMany(mappedBy = "product")
    private List<ProductImage> images = new ArrayList<>();
    
    
    
    
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
//	public Long getStock() {
//		return stock;
//	}
//	public void setStock(Long stock) {
//		this.stock = stock;
//	}
	 public Long getStock() {
	        return stock;
	    }

	    public void setStock(Long stock) {
	        this.stock = stock;
	    }
	    
	    public List<ProductImage> getImages() {
	        return images;
	    }

	    public void setImages(List<ProductImage> images) {
	        this.images = images;
	    }
		public List<PurchaseOrderDetail> getPurchaseDetails() {
			return purchaseDetails;
		}
		
		public void setPurchaseDetails(List<PurchaseOrderDetail> purchaseDetails) {
			this.purchaseDetails = purchaseDetails;
		}
		
		public Supplier getSupplier() {
			return supplier;
		}
		
		public void setSupplier(Supplier supplier) {
			this.supplier = supplier;
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
		
		
		
		

}
