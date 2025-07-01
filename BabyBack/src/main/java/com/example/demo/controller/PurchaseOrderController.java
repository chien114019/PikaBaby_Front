package com.example.demo.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;

import com.example.demo.model.PurchaseOrder;
import com.example.demo.model.PurchaseOrderDetail;
import com.example.demo.model.SupplierProduct;
import com.example.demo.service.ProductService;
import com.example.demo.service.PurchaseOrderService;
import com.example.demo.service.SupplierProductService;
import com.example.demo.service.SupplierService;

@Controller
@RequestMapping("/purchases")
public class PurchaseOrderController {

    @Autowired private SupplierService supplierService;
    @Autowired private ProductService productService;
    @Autowired private PurchaseOrderService orderService;
    @Autowired private SupplierProductService supplierProductService;

    // 顯示新增進貨單頁面
    @GetMapping("/new")
    public String createForm(Model model) {
        PurchaseOrder order = new PurchaseOrder();

        // 產生自動單號，例如 PO202406180001
        String generatedOrderNumber = orderService.generateOrderNumber();
        order.setOrderNumber(generatedOrderNumber);

        model.addAttribute("order", order);
        model.addAttribute("suppliers", supplierService.listAll());
        model.addAttribute("products", productService.listAll());
        model.addAttribute("supplierProducts", supplierProductService.findAllValidForPurchase());
        return "purchase/form";
    }

    // 儲存進貨單
    @PostMapping("/save")
    public String saveOrder(
            @RequestParam(value = "id", required = false) Integer orderId,
            @RequestParam("supplierProductIds") Integer[] supplierProductIds,
            @RequestParam("quantities") Long[] quantities,
            @RequestParam("unitPrice") BigDecimal[] unitPrice,
            @RequestParam("orderDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date orderDate) {

        PurchaseOrder order;
        if (orderId != null) {
            // 編輯流程
            order = orderService.getById(orderId);
            order.setOrderDate(orderDate);

            // 只「清空」原有明細（保留 reference）
            order.getDetails().clear();

            // 新增新明細（務必 setOrder(order)）
            for (int i = 0; i < supplierProductIds.length; i++) {
                SupplierProduct sp = supplierProductService.getById(supplierProductIds[i]);
                PurchaseOrderDetail detail = new PurchaseOrderDetail();
                detail.setOrder(order);
                detail.setSupplierProduct(sp);
                detail.setQuantity(quantities[i]);
                detail.setUnitPrice(unitPrice[i]);
                order.getDetails().add(detail);
            }

            if (!order.getDetails().isEmpty()) {
                order.setSupplier(order.getDetails().get(0).getSupplierProduct().getSupplier());
            }
        } else {
            // 新增流程
            order = new PurchaseOrder();
            order.setOrderDate(orderDate);

            List<PurchaseOrderDetail> detailList = new ArrayList<>();
            for (int i = 0; i < supplierProductIds.length; i++) {
                SupplierProduct sp = supplierProductService.getById(supplierProductIds[i]);
                PurchaseOrderDetail detail = new PurchaseOrderDetail();
                detail.setOrder(order);
                detail.setSupplierProduct(sp);
                detail.setQuantity(quantities[i]);
                detail.setUnitPrice(unitPrice[i]);
                detailList.add(detail);
            }

            if (!detailList.isEmpty()) {
                order.setSupplier(detailList.get(0).getSupplierProduct().getSupplier());
            }
            order.setDetails(detailList);
        }

        orderService.save(order);
        return "redirect:/purchases";
    }


    // 顯示進貨單清單
    @GetMapping
    public String list(Model model) {
        List<PurchaseOrder> orders = orderService.listAll();

        // 強制初始化嵌套關聯資料，避免 lazy loading 問題
        for (PurchaseOrder order : orders) {
            order.getDetails().forEach(d -> {
                if (d.getSupplierProduct() != null) {
                    if (d.getSupplierProduct().getProduct() != null) {
                        d.getSupplierProduct().getProduct().getName(); // 強制初始化
                    }
                    if (d.getSupplierProduct().getSupplier() != null) {
                        d.getSupplierProduct().getSupplier().getName(); // 強制初始化
                    }
                }
            });
        }

        model.addAttribute("orders", orders);
        return "purchase/list";
    }

    // 刪除進貨單
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        orderService.deleteById(id);
        return "redirect:/purchases";
    }

    // 顯示編輯畫面
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Integer id, Model model) {
        PurchaseOrder order = orderService.getById(id);
        if (order == null) {
            return "redirect:/purchases";
        }

        model.addAttribute("order", order);
        model.addAttribute("orderDetail", order.getDetails());
        model.addAttribute("suppliers", supplierService.listAll());
        model.addAttribute("products", productService.listAll());
        model.addAttribute("supplierProducts", supplierProductService.listAll());
        return "purchase/form";
    }
}
