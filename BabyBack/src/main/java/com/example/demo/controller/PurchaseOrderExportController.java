package com.example.demo.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.example.demo.model.PurchaseOrder;
import com.example.demo.service.PurchaseOrderService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
public class PurchaseOrderExportController {
	
	@Autowired
	private PurchaseOrderService purchaseOrderService;
	
	
	
@GetMapping("/purchases/export")
public void exportCostReport(HttpServletResponse response) throws IOException {
    List<PurchaseOrder> orders = purchaseOrderService.listAll();

    Workbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet("進貨成本報表");

    // 標題列
    Row header = sheet.createRow(0);
    header.createCell(0).setCellValue("進貨單號");
    header.createCell(1).setCellValue("供應商");
    header.createCell(2).setCellValue("進貨日期");
    header.createCell(3).setCellValue("商品名稱");
    header.createCell(4).setCellValue("數量");
    header.createCell(5).setCellValue("單價");
    header.createCell(6).setCellValue("小計");

    int rowIdx = 1;
    java.math.BigDecimal totalReportAmount = java.math.BigDecimal.ZERO;

    for (PurchaseOrder order : orders) {
        java.math.BigDecimal orderTotal = java.math.BigDecimal.ZERO;

        for (var detail : order.getDetails()) {
            java.math.BigDecimal subTotal = detail.getUnitPrice()
                    .multiply(java.math.BigDecimal.valueOf(detail.getQuantity()));

            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(order.getOrderNumber());
            row.createCell(1).setCellValue(order.getSupplier().getName());
            row.createCell(2).setCellValue(order.getOrderDate().toString());
            row.createCell(3).setCellValue(detail.getSupplierProduct().getProduct().getName());
            row.createCell(4).setCellValue(detail.getQuantity());
            row.createCell(5).setCellValue(detail.getUnitPrice().doubleValue());
            row.createCell(6).setCellValue(subTotal.doubleValue());

            orderTotal = orderTotal.add(subTotal);
        }

        // 每張訂單總計列（可加粗樣式）
        Row summaryRow = sheet.createRow(rowIdx++);
        Cell summaryLabel = summaryRow.createCell(5);
        summaryLabel.setCellValue("進貨單小計");
        Cell summaryValue = summaryRow.createCell(6);
        summaryValue.setCellValue(orderTotal.doubleValue());

        totalReportAmount = totalReportAmount.add(orderTotal);
    }

    // 整份報表總計列
    Row finalTotalRow = sheet.createRow(rowIdx++);
    Cell totalLabel = finalTotalRow.createCell(5);
    totalLabel.setCellValue("總進貨成本");
    Cell totalValue = finalTotalRow.createCell(6);
    totalValue.setCellValue(totalReportAmount.doubleValue());

    // 自動調整欄寬
    for (int i = 0; i <= 6; i++) {
        sheet.autoSizeColumn(i);
    }

    String filename = URLEncoder.encode("進貨成本報表.xlsx", "UTF-8");
    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + filename);

    workbook.write(response.getOutputStream());
    workbook.close();
	}
}
