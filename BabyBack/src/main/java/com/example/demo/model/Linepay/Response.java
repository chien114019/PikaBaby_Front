package com.example.demo.model.Linepay;

public class Response {
	public Response() {}
	
	private String returnCode;
	private String returnMesg;
	private Info info;
	
	public String getReturnCode() {
		return returnCode;
	}
	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}
	public String getReturnMesg() {
		return returnMesg;
	}
	public void setReturnMesg(String returnMesg) {
		this.returnMesg = returnMesg;
	}
	public Info getInfo() {
		return info;
	}
	public void setInfo(Info info) {
		this.info = info;
	}
	
}
