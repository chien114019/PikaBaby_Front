package com.example.demo.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
public class SalesOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private Customer customer;

    @Temporal(TemporalType.DATE)
    private Date orderDate;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SalesOrderDetail> details;
    
    @OneToMany(mappedBy = "salesOrder", cascade = CascadeType.ALL)
    private List<ShippingOrder> shippingOrders;

    @Column(columnDefinition = "integer default 0")
    private Integer status = 0;  // 0:已成立, 1:已完成, -1:已取消

    @Column(columnDefinition = "integer default 0")
    private Integer payStatus = 0;  // 0:未付款, 1:已付款, 2:付款失敗ㄝ, 3:已退款
    
    @Column(name = "order_number")
    private String orderNumber;
    
    // 對應實際資料表中的前台訂單欄位
    @Column(name = "recipient_name")
    private String recipientName;
    
    @Column(name = "recipient_phone")
    private String recipientPhone;
    
    @Column(name = "recipient_email")
    private String recipientEmail;
    
    @Column(name = "shipping_address")
    private String shippingAddress;
    
    @Column(name = "payment_method")
    private String paymentMethod;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public List<SalesOrderDetail> getDetails() {
		return details;
	}

	public void setDetails(List<SalesOrderDetail> details) {
		this.details = details;
	}	
	
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(Integer payStatus) {
		this.payStatus = payStatus;
	}

	public String getRecipientName() {
		return recipientName;
	}

	public void setRecipientName(String recipientName) {
		this.recipientName = recipientName;
	}

	public String getRecipientPhone() {
		return recipientPhone;
	}

	public void setRecipientPhone(String recipientPhone) {
		this.recipientPhone = recipientPhone;
	}

	public String getRecipientEmail() {
		return recipientEmail;
	}

	public void setRecipientEmail(String recipientEmail) {
		this.recipientEmail = recipientEmail;
	}

	public String getShippingAddress() {
		return shippingAddress;
	}

	public void setShippingAddress(String shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	
	public double getTotalAmount() {
	    if (details == null || details.isEmpty()) {
	        return 0.0;
	    }
	    return details.stream()
	        .mapToDouble(d -> {
	            Double unitPrice = d.getUnitPrice();
	            Long quantity = d.getQuantity();
	            if (unitPrice == null || quantity == null) {
	                return 0.0;
	            }
	            return unitPrice * quantity.doubleValue();
	        })
	        .sum();
	}

	public List<ShippingOrder> getShippingOrders() {
		return shippingOrders;
	}

	public void setShippingOrders(List<ShippingOrder> shippingOrders) {
		this.shippingOrders = shippingOrders;
	}
	
	public String getOrderNumber() {
	    return orderNumber;
	}
	
	public void setOrderNumber(String orderNumber) {
	    this.orderNumber = orderNumber;
	}
    

}
