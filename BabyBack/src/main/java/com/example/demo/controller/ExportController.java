package com.example.demo.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Customer;
import com.example.demo.model.SalesOrder;
import com.example.demo.service.CustomerService;
import com.example.demo.service.SalesOrderService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
public class ExportController {

    @Autowired
    private SalesOrderService orderService;
    
    @Autowired
    private CustomerService customerService;

    @GetMapping("/orders/export")
    public void exportOrdersExcel(HttpServletResponse response) throws IOException {
        List<SalesOrder> orders = orderService.listAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("銷貨訂單");

        // 標題列
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("訂單編號");
        header.createCell(1).setCellValue("客戶名稱");
        header.createCell(2).setCellValue("訂單日期");
        header.createCell(3).setCellValue("商品明細");

        int rowIdx = 1;
        for (SalesOrder order : orders) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(order.getId());
            row.createCell(1).setCellValue(order.getCustomer().getName());
            row.createCell(2).setCellValue(order.getOrderDate().toString());

            StringBuilder details = new StringBuilder();
            order.getDetails().forEach(d -> {
                details.append(d.getProduct().getName())
                        .append(" x").append(d.getQuantity())
                        .append(" ($").append(d.getUnitPrice()).append(")\n");
            });
            row.createCell(3).setCellValue(details.toString());
        }

        // 設定 HTTP 回應為下載檔案
        String filename = URLEncoder.encode("銷貨訂單.xlsx", "UTF-8");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + filename);

        workbook.write(response.getOutputStream());
        workbook.close();
    }
    
    private String LocalDateFormatter(LocalDate date) {
    	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    	if (date != null) {
    		return date.format(dtf);			
		} else {
			return "";
		}
    }
    
    private String LocalDateTimeFormatter(LocalDateTime dateTime) {
    	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    	if (dateTime != null) {		
    		return dateTime.format(dtf);
		} else {
			return "";
		}
    }
}
