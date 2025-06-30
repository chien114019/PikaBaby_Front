package com.example.demo.model;



import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.*;

@Entity
@Table(name="customer")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String phone;
    private String email;
    private String password;
    private String gender;
    private LocalDate birthday;
    
    private LocalDate baby1Birthday;
    private LocalDate baby2Birthday;
    private LocalDate baby3Birthday;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Taipei")
    private LocalDateTime createdAt;
    
    private LocalDateTime firstLoginAt;
    
    @Column(length = 30)
    private String creditCard;
    
    private Integer points;
    private String googleId;
  
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CustomerAddress> address;
    
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;
    
    private String resetToken;         // 唯一重設碼
    private LocalDateTime tokenExpiry;// 到期時間

    
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
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public LocalDate getBirthday() {
		return birthday;
	}
	public void setBirthday(LocalDate birthday) {
		this.birthday = birthday;
	}
	public LocalDate getBaby1Birthday() {
		return baby1Birthday;
	}
	public void setBaby1Birthday(LocalDate baby1Birthday) {
		this.baby1Birthday = baby1Birthday;
	}
	public LocalDate getBaby2Birthday() {
		return baby2Birthday;
	}
	public void setBaby2Birthday(LocalDate baby2Birthday) {
		this.baby2Birthday = baby2Birthday;
	}
	public LocalDate getBaby3Birthday() {
		return baby3Birthday;
	}
	public void setBaby3Birthday(LocalDate baby3Birthday) {
		this.baby3Birthday = baby3Birthday;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public LocalDateTime getFirstLoginAt() {
		return firstLoginAt;
	}
	public void setFirstLoginAt(LocalDateTime firstLoginAt) {
		this.firstLoginAt = firstLoginAt;
	}
	public String getCreditCard() {
		return creditCard;
	}
	public void setCreditCard(String creditCard) {
		this.creditCard = creditCard;
	}
	public Integer getPoints() {
		return points;
	}
	public void setPoints(Integer points) {
		this.points = points;
	}

	public List<CustomerAddress> getAddresses() {
		return address;
	}
	public void setAddresses(List<CustomerAddress> addresses) {
		this.address = addresses;
	}
	
	
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	
	public List<CustomerAddress> getAddress() {
		return address;
	}
	public void setAddress(List<CustomerAddress> address) {
		this.address = address;
	}
	public Boolean getIsDeleted() {
		return isDeleted;
	}
	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	public String getGoogleId() {
		return googleId;
	}
	public void setGoogleId(String googleId) {
		this.googleId = googleId;
	}
	
	
	public String getResetToken() {
		return resetToken;
	}
	public void setResetToken(String resetToken) {
		this.resetToken = resetToken;
	}
	public LocalDateTime getTokenExpiry() {
		return tokenExpiry;
	}
	public void setTokenExpiry(LocalDateTime tokenExpiry) {
		this.tokenExpiry = tokenExpiry;
	}
	// 空建構子（必要）
    public Customer() {}

    // 可根據需要加上建構子、toString、equals 等
//    @PrePersist
//    public void prePersist() {
//        this.createdAt = LocalDateTime.now();
//        if (this.points == null) {
//            this.points = 100;
//        }
//    }
}
