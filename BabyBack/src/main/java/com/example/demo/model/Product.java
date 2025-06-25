package com.example.demo.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    
    // 如果資料庫沒有這些欄位，JPA會忽略它們
    @Column(name = "specification")
    private String specification;
    
    @Column(name = "color")
    private String color;
    
    @Column(name = "note")
    private String note;
    
    @Column(name = "image_url")
    private String imageUrl;
    

    
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;
    
    @Column(name = "is_published")
    private Boolean published = false;
    
    // 年齡範圍欄位（如果資料庫有的話）
    @Column(name = "age1")
    private Boolean age1 = false;	// 0-3M
    
    @Column(name = "age2")
    private Boolean age2 = false;	// 3-6M
    
    @Column(name = "age3")
    private Boolean age3 = false;	// 6-12M
    
    @Column(name = "age4")
    private Boolean age4 = false;	// 2-3Y
    
    // 關聯表（如果存在的話）
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<SupplierProduct> supplierProducts = new ArrayList<>();
    
    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<ProductImage> images = new ArrayList<>();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type")
    private ProductType productType;
    
    // 商品價格
    @Column(name = "price")
    private Double price;
    
    // 商品實際庫存欄位（存資料庫）
    @Column(name = "stock")
    private Long stock;
    
    // 動態計算庫存（不存資料庫）
    @Transient
    private Long calculatedStock;

    // Getters and Setters
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public Boolean getAge1() {
        return age1;
    }

    public void setAge1(Boolean age1) {
        this.age1 = age1;
    }

    public Boolean getAge2() {
        return age2;
    }

    public void setAge2(Boolean age2) {
        this.age2 = age2;
    }

    public Boolean getAge3() {
        return age3;
    }

    public void setAge3(Boolean age3) {
        this.age3 = age3;
    }

    public Boolean getAge4() {
        return age4;
    }

    public void setAge4(Boolean age4) {
        this.age4 = age4;
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

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public Long getCalculatedStock() {
        return calculatedStock;
    }

    public void setCalculatedStock(Long calculatedStock) {
        this.calculatedStock = calculatedStock;
    }
    
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
    
    public BigDecimal getPriceAsBigDecimal() {
        return price != null ? BigDecimal.valueOf(price) : BigDecimal.valueOf(100.0);
    }

    // 實用方法
    public String getPrimaryImageUrl() {
        // 優先返回 images 集合中的第一張圖片
        if (images != null && !images.isEmpty()) {
            return "/products/front/images/" + images.get(0).getId();
        }
        // 其次返回 imageUrl 欄位
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            return imageUrl;
        }
        // 最後返回預設圖片
        return "/images/default.jpg";
    }

    public List<String> getAllImageUrls() {
        List<String> urls = new ArrayList<>();
        if (images != null) {
            for (ProductImage img : images) {
                urls.add("/products/front/images/" + img.getId());
            }
        }
        return urls;
    }

    // 年齡範圍列表
    public List<String> getAgeRanges() {
        List<String> ranges = new ArrayList<>();
        if (Boolean.TRUE.equals(age1)) ranges.add("0-3M");
        if (Boolean.TRUE.equals(age2)) ranges.add("3-6M");
        if (Boolean.TRUE.equals(age3)) ranges.add("6-12M");
        if (Boolean.TRUE.equals(age4)) ranges.add("2-3Y");
        return ranges;
    }
    
    
    // 資料庫庫存欄位的getter/setter
    public Long getStock() {
        return stock != null ? stock : 0L;
    }
    
    public void setStock(Long stock) {
        this.stock = stock;
    }
    
    // 計算庫存的getter/setter（用於顯示）
    public Long getDisplayStock() {
        return calculatedStock != null ? calculatedStock : getStock();
    }
}
