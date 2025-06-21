package com.example.demo.model;

import java.util.List;

import jakarta.persistence.*;

@Entity
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    
    private String phone;
    
    private String email;
    
    private String address;
    
    @OneToMany(mappedBy = "supplier")
    private List<SupplierProduct> supplierProducts;

    
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
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public List<SupplierProduct> getSupplierProducts() {
		return supplierProducts;
	}
	public void setSupplierProducts(List<SupplierProduct> supplierProducts) {
		this.supplierProducts = supplierProducts;
	}
	
	

   
}
