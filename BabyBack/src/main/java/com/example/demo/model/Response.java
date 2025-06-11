package com.example.demo.model;

import jakarta.persistence.Entity;

public class Response {

	private boolean success;
	private String mesg;
	
	public boolean getSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMesg() {
		return mesg;
	}
	public void setMesg(String mesg) {
		this.mesg = mesg;
	}
	
	
}
