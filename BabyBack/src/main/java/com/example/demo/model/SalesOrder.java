package com.example.demo.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
public class SalesOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Customer customer;

    @Temporal(TemporalType.DATE)
    private Date orderDate;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SalesOrderDetail> details;

    @Column(columnDefinition = "integer default 0")
    private Integer status = 0;  // 0:已成立, 1:已完成, -1:已取消

    @Column(columnDefinition = "integer default 0")
    private Integer payStatus = 0;  // 0:未付款, 1:已付款

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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
	
	public double getTotalAmount() {
	    return details.stream()
	        .mapToDouble(d -> d.getUnitPrice()
	        .multiply(BigDecimal.valueOf(d.getQuantity()))
	        .doubleValue())
	        .sum();
	}
    
}
