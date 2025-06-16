package com.example.demo.model;

import org.apache.commons.codec.net.BCodec;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

//	銀行代碼
@Entity
@Table(name = "BankNo")
public class BankNo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String bCode;
	private String bName;

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getbCode() {
		return bCode;
	}
	
	public void setbCode(String bCode) {
		this.bCode = bCode;
	}
	
	public String getbName() {
		return bName;
	}
	
	public void setbName(String bName) {
		this.bName = bName;
	}
	
	
}
