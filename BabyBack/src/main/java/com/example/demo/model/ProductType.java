package com.example.demo.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

//	商品種類
@Entity
@Table(name = "ProductType")
public class ProductType {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String typeName;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	
//	-----------------------------------
	
	@OneToMany(mappedBy = "productType")
	@JsonBackReference
	private List<Consignment> consignments;

	public List<Consignment> getConsignments() {
		return consignments;
	}
	public void setConsignments(List<Consignment> consignments) {
		this.consignments = consignments;
	}
	
	
}
