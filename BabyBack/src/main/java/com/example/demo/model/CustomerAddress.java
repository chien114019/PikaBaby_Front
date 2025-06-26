package com.example.demo.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "customer_address")
public class CustomerAddress {
	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer id;

	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "customer_id", nullable = false)
	    @JsonIgnore
	    private Customer customer;

	    private String name;        // 收件人姓名
	    private String city;
	    private String district;
	    private String zipcode;
	    private String street;
	    private String phone;

	    @Column(name = "is_default_order")
	    private Boolean isDefaultOrder = false;

	    @Column(name = "is_default_shipping")
	    private Boolean isDefaultShipping = false;

	    @Column(name = "created_at")
	    private LocalDateTime createdAt = LocalDateTime.now();

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

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getDistrict() {
			return district;
		}

		public void setDistrict(String district) {
			this.district = district;
		}

		public String getZipcode() {
			return zipcode;
		}

		public void setZipcode(String zipcode) {
			this.zipcode = zipcode;
		}

		public String getStreet() {
			return street;
		}

		public void setStreet(String street) {
			this.street = street;
		}

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}

		public Boolean getIsDefaultOrder() {
			return isDefaultOrder;
		}

		public void setIsDefaultOrder(Boolean isDefaultOrder) {
			this.isDefaultOrder = isDefaultOrder;
		}

		public Boolean getIsDefaultShipping() {
			return isDefaultShipping;
		}

		public void setIsDefaultShipping(Boolean isDefaultShipping) {
			this.isDefaultShipping = isDefaultShipping;
		}

		public LocalDateTime getCreatedAt() {
			return createdAt;
		}

		public void setCreatedAt(LocalDateTime createdAt) {
			this.createdAt = createdAt;
		}

}
