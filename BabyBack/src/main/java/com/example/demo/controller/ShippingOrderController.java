package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.SalesOrder;
import com.example.demo.model.ShippingOrder;
import com.example.demo.repository.SalesOrderRepository;
import com.example.demo.repository.ShippingOrderRepository;
import com.example.demo.service.ShippingOrderService;

@Controller
@RequestMapping("/shipping")
@CrossOrigin
public class ShippingOrderController {

    @Autowired
    private ShippingOrderService shippingOrderService;

    // ========== 後台畫面 ==========

    @GetMapping
    public String listAllShippingOrders(Model model) {
        List<ShippingOrder> shippingOrders = shippingOrderService.findAll();
        model.addAttribute("shippingOrders", shippingOrders);
        return "shipping/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        List<SalesOrder> availableSalesOrders = shippingOrderService.findSalesOrdersWithoutShipping();
        model.addAttribute("salesOrders", availableSalesOrders);
        return "shipping/create";
    }

    @PostMapping("/create")
    public String createShippingOrder(@RequestParam SalesOrder salesOrderId) {
        shippingOrderService.createFromSalesOrder(salesOrderId);
        return "redirect:/shipping";
    }
    
    @PostMapping("/{id}/ship")
    public String shipOrder(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        // 執行出貨邏輯
        shippingOrderService.markAsShipped(id);

        redirectAttributes.addFlashAttribute("successMessage", "銷貨單 #" + id + " 出貨成功！");
        return "redirect:/shipping";  // 回到訂單列表頁
    }
    
    @PostMapping("/{id}/unship")
    public String unshipOrder(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        shippingOrderService.markAsUnshipped(id); // 將狀態改回「待出貨」
        redirectAttributes.addFlashAttribute("successMessage", "訂單 #" + id + " 已還原為待出貨");
        return "redirect:/shipping";
    }


    // ========== API ==========

    @GetMapping("/api")
    @ResponseBody
    public List<ShippingOrder> apiListAll() {
        return shippingOrderService.findAll();
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> apiGetById(@PathVariable Integer id) {
        ShippingOrder order = shippingOrderService.findById(id);
        if (order == null) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "查無出貨單 ID: " + id
            ));
        }
        return ResponseEntity.ok(order);
    }

    @PutMapping("/api/{id}/ship")
    @ResponseBody
    public ResponseEntity<?> apiMarkAsShipped(@PathVariable Integer id) {
        try {
            shippingOrderService.markAsShipped(id);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "已標記為已出貨並建立應收帳款"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
}


