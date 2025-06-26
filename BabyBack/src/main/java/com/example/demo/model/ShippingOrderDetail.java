package com.example.demo.model;

import jakarta.persistence.*;

@Entity
public class ShippingOrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "shipping_order_id")
    private ShippingOrder shippingOrder;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private Long quantity;

    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public ShippingOrder getShippingOrder() { return shippingOrder; }
    public void setShippingOrder(ShippingOrder shippingOrder) { this.shippingOrder = shippingOrder; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Long getQuantity() { return quantity; }
    public void setQuantity(Long quantity) { this.quantity = quantity; }
}
