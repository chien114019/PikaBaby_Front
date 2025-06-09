package com.example.demo.model;

import jakarta.persistence.*;
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
	
	public double getTotalAmount() {
	    return details.stream()
	        .mapToDouble(d -> d.getQuantity() * d.getUnitPrice())
	        .sum();
	}
    
}
