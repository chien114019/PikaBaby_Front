package com.example.demo.dto;

public class SalesStatsDTO {
    private String month;
    private Double totalSales;
    
    
    public SalesStatsDTO() {}
    

    public SalesStatsDTO(String month, Double totalSales) {
        this.month = month;
        this.totalSales = totalSales;
    }
     

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public Double getTotalSales() {
		return totalSales;
	}

	public void setTotalSales(Double totalSales) {
		this.totalSales = totalSales;
	}

   
}

