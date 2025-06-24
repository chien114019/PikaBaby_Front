package com.example.demo.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;


import com.example.demo.model.Product;
import com.example.demo.model.ReturnOrder;
import com.example.demo.model.ReturnOrderDetail;
import com.example.demo.model.SalesOrder;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.ReturnOrderDetailRepository;
import com.example.demo.repository.ReturnOrderRepository;
import com.example.demo.repository.SalesOrderRepository;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;


@Controller
@RequestMapping("/returns")
public class ReturnOrderController {

    @Autowired
    private SalesOrderRepository salesOrderRepository;

    @Autowired
    private ReturnOrderRepository returnOrderRepository;

    @Autowired
    private ReturnOrderDetailRepository returnOrderDetailRepository;

    @Autowired
    private ProductRepository productRepository;

    // 顯示建立退貨單頁面
    @GetMapping("/create")
    public String showReturnForm(@RequestParam(required = false) Integer orderId, Model model) {
        List<SalesOrder> orders = salesOrderRepository.findAll();
        model.addAttribute("orders", orders);

        if (orderId != null) {
            SalesOrder selectedOrder = salesOrderRepository.findById(orderId).orElse(null);
            model.addAttribute("selectedOrder", selectedOrder);
        }

        return "returns/create"; 
    }

    // 處理退貨單提交
    @PostMapping("/create")
    public String processReturn(
            @RequestParam Integer orderId,
            @RequestParam List<Integer> productId,
            @RequestParam List<Integer> qty,
            @RequestParam List<Double> unitPrice,
            @RequestParam(required = false) String reason
    ) {
        // 建立主檔
        ReturnOrder ro = new ReturnOrder();
        ro.setReturnNo("RT" + System.currentTimeMillis());
        ro.setReturnDate(LocalDate.now());
        ro.setSalesOrder(salesOrderRepository.findById(orderId).orElse(null));
        ro.setReason(reason);

        List<ReturnOrderDetail> detailList = new ArrayList<>();

        for (int i = 0; i < productId.size(); i++) {
            if (qty.get(i) > 0) {
                Product p = productRepository.findById(productId.get(i)).orElse(null);
                ReturnOrderDetail d = new ReturnOrderDetail();
                d.setProduct(p);
                d.setQty(qty.get(i));
                d.setUnitPrice(unitPrice.get(i));
                d.setTotal(qty.get(i) * unitPrice.get(i));
                d.setReturnOrder(ro);
                detailList.add(d);

                // 注意：庫存現在通過動態計算，不需要手動調整
                // 退貨會自動體現在庫存計算中（進貨-銷售+退貨）
            }
        }

        ro.setDetails(detailList);
        returnOrderRepository.save(ro); // 連同 detail cascade 一起存

        return "redirect:/returns/list";
    }
    
    @GetMapping("/list")
    public String showReturnList(
        @RequestParam(required = false) String customerName,
        @RequestParam(required = false) String startDate,
        @RequestParam(required = false) String endDate,
        @RequestParam(required = false) Integer orderId,
        Model model) {

        List<ReturnOrder> returnOrders = returnOrderRepository.findAll(); // 可改為客製查詢

        // 過濾條件
        if (customerName != null && !customerName.isBlank()) {
            returnOrders = returnOrders.stream()
                .filter(ro -> ro.getSalesOrder().getCustomer().getName().contains(customerName))
                .collect(Collectors.toList());
        }

        if (orderId != null) {
            returnOrders = returnOrders.stream()
                .filter(ro -> ro.getSalesOrder().getId().equals(orderId))
                .collect(Collectors.toList());
        }

        if (startDate != null && endDate != null) {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            returnOrders = returnOrders.stream()
                .filter(ro -> !ro.getReturnDate().isBefore(start) && !ro.getReturnDate().isAfter(end))
                .collect(Collectors.toList());
        }

        model.addAttribute("returnOrders", returnOrders);
        return "returns/list";
    }

    
    @GetMapping("/view/{id}")
    public String viewReturnOrder(@PathVariable Integer id, Model model) {
        ReturnOrder returnOrder = returnOrderRepository.findById(id).orElse(null);
        model.addAttribute("returnOrder", returnOrder);
        return "returns/view";
    }
    
    @GetMapping("/export")
    public void exportReturnOrdersToExcel(
        HttpServletResponse response,
        @RequestParam(required = false) String customerName,
        @RequestParam(required = false) String startDate,
        @RequestParam(required = false) String endDate,
        @RequestParam(required = false) Integer orderId
    ) throws IOException {

        List<ReturnOrder> returnOrders = returnOrderRepository.findAll();

        // 過濾條件（與查詢邏輯一致）
        if (customerName != null && !customerName.isBlank()) {
            returnOrders = returnOrders.stream()
                .filter(ro -> ro.getSalesOrder().getCustomer().getName().contains(customerName))
                .collect(Collectors.toList());
        }
        if (orderId != null) {
            returnOrders = returnOrders.stream()
                .filter(ro -> ro.getSalesOrder().getId().equals(orderId))
                .collect(Collectors.toList());
        }
        if (startDate != null && endDate != null) {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            returnOrders = returnOrders.stream()
                .filter(ro -> !ro.getReturnDate().isBefore(start) && !ro.getReturnDate().isAfter(end))
                .collect(Collectors.toList());
        }

        // 建立 Excel 檔案
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("退貨單列表");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("退貨單編號");
        header.createCell(1).setCellValue("退貨日期");
        header.createCell(2).setCellValue("原始訂單");
        header.createCell(3).setCellValue("客戶名稱");
        header.createCell(4).setCellValue("總金額");
        header.createCell(5).setCellValue("退貨原因");

        int rowNum = 1;
        for (ReturnOrder ro : returnOrders) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(ro.getReturnNo());
            row.createCell(1).setCellValue(ro.getReturnDate().toString());
            row.createCell(2).setCellValue(ro.getSalesOrder().getId());
            row.createCell(3).setCellValue(ro.getSalesOrder().getCustomer().getName());
            row.createCell(4).setCellValue(
                ro.getDetails().stream().mapToDouble(ReturnOrderDetail::getTotal).sum()
            );
            row.createCell(5).setCellValue(ro.getReason());
        }

        // 設定回應標頭
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=return_orders.xlsx");

        workbook.write(response.getOutputStream());
        workbook.close();
    }
    
    @PostMapping("/delete/{id}")
    public String deleteReturnOrder(@PathVariable Integer id, HttpSession session) {
        // 權限驗證（假設 session 中有角色屬性）
        String role = (String) session.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return "redirect:/access-denied"; // 未授權
        }

        ReturnOrder ro = returnOrderRepository.findById(id).orElse(null);
        if (ro != null) {
            // 注意：庫存現在通過動態計算，不需要手動調整
            // 刪除退貨記錄會自動體現在庫存計算中

            returnOrderRepository.delete(ro); // Cascade 一併刪明細
        }

        return "redirect:/returns/list";
    }
    
    @GetMapping("/dashboard")
    public String returnStats(Model model) {

        // 每月退貨總金額統計
        List<Object[]> monthlyStats = returnOrderRepository.getMonthlyTotal(); 
        // 每筆格式：Object[0] = 年月(yyyy-MM), Object[1] = 金額(Double)

        List<Object[]> topProducts = returnOrderDetailRepository.getTopReturnProducts();
        // 每筆格式：Object[0] = 商品名稱, Object[1] = 次數(Long)

        model.addAttribute("monthlyStats", monthlyStats != null ? monthlyStats : new ArrayList<>());
        model.addAttribute("topProducts", topProducts != null ? topProducts : new ArrayList<>());
        return "returns/dashboard";
    }
    
    @GetMapping("/pdf/{id}")
    public void exportReturnPdf(@PathVariable Integer id, HttpServletResponse response) throws IOException {
        ReturnOrder ro = returnOrderRepository.findById(id).orElse(null);
        if (ro == null) return;

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=return_" + ro.getReturnNo() + ".pdf");

        Document doc = new Document(PageSize.A4);
        PdfWriter.getInstance(doc, response.getOutputStream());
        doc.open();

        // 中文字型（避免亂碼）
        BaseFont bf = BaseFont.createFont("resources/fonts/NotoSansTC-Regular.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font font = new Font(bf, 12, Font.NORMAL);
        Font boldFont = new Font(bf, 14, Font.BOLD);

        doc.add(new Paragraph("退貨單明細", boldFont));
        doc.add(new Paragraph("退貨單編號: " + ro.getReturnNo(), font));
        doc.add(new Paragraph("退貨日期: " + ro.getReturnDate(), font));
        doc.add(new Paragraph("原始訂單: " + ro.getSalesOrder().getId(), font));
        doc.add(new Paragraph("客戶名稱: " + ro.getSalesOrder().getCustomer().getName(), font));
        doc.add(new Paragraph("退貨原因: " + ro.getReason(), font));
        doc.add(new Paragraph(" "));

        // 明細表格
        PdfPTable table = new PdfPTable(4);
        table.setWidths(new float[]{4, 2, 2, 2});
        table.setWidthPercentage(100);

        // 表頭
        table.addCell(new PdfPCell(new Phrase("商品名稱", font)));
        table.addCell(new PdfPCell(new Phrase("數量", font)));
        table.addCell(new PdfPCell(new Phrase("單價", font)));
        table.addCell(new PdfPCell(new Phrase("小計", font)));

        for (ReturnOrderDetail d : ro.getDetails()) {
            table.addCell(new PdfPCell(new Phrase(d.getProduct().getName(), font)));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(d.getQty()), font)));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(d.getUnitPrice()), font)));
            table.addCell(new PdfPCell(new Phrase(String.valueOf(d.getTotal()), font)));
        }

        doc.add(table);
        doc.close();
    }


    

}

