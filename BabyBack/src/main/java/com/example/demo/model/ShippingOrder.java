package com.example.demo.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity
public class ShippingOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "sales_order_id")
    private SalesOrder salesOrder;

    private LocalDate shippingDate;

    private String status; // 例："待出貨"、"已出貨"

    @OneToMany(mappedBy = "shippingOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShippingOrderDetail> details;

    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public SalesOrder getSalesOrder() { return salesOrder; }
    public void setSalesOrder(SalesOrder salesOrder) { this.salesOrder = salesOrder; }

    public LocalDate getShippingDate() { return shippingDate; }
    public void setShippingDate(LocalDate localDate) { this.shippingDate = localDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<ShippingOrderDetail> getDetails() { return details; }
    public void setDetails(List<ShippingOrderDetail> details) { this.details = details; }
}
