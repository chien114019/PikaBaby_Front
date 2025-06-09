package com.example.demo.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.Payment;
import com.example.demo.model.PaymentOrder;
import com.example.demo.model.SalesOrder;
import com.example.demo.repository.PaymentOrderRepository;
import com.example.demo.repository.PaymentRepository;
import com.example.demo.repository.SalesOrderRepository;

@Controller
@RequestMapping("/payments")
public class PaymentController {

    @Autowired private SalesOrderRepository salesOrderRepository;
    @Autowired private PaymentRepository paymentRepository;
    @Autowired private PaymentOrderRepository paymentOrderRepository;

    // 顯示收款表單
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("payment", new Payment());

        // 查詢所有未收款或部分收款的訂單（這裡簡化為全部訂單）
        List<SalesOrder> unpaidOrders = salesOrderRepository.findAll();
        model.addAttribute("orders", unpaidOrders);

        return "payments/create";
    }

    // 提交收款表單
    @PostMapping("/create")
    public String savePayment(@RequestParam String payDate,
                              @RequestParam Double amount,
                              @RequestParam String method,
                              @RequestParam(required = false) String note,
                              @RequestParam(name = "orderId", required = false) List<Long> orderIds,
                              @RequestParam(name = "appliedAmount", required = false) List<Double> appliedAmounts) {

        Payment payment = new Payment();
        payment.setPayDate(LocalDate.parse(payDate));
        payment.setAmount(amount);
        payment.setMethod(method);
        payment.setNote(note);

        List<PaymentOrder> relations = new ArrayList<>();

        if (orderIds != null) {
            for (int i = 0; i < orderIds.size(); i++) {
                SalesOrder order = salesOrderRepository.findById(orderIds.get(i)).orElse(null);
                if (order != null && appliedAmounts.get(i) > 0) {
                    PaymentOrder po = new PaymentOrder();
                    po.setSalesOrder(order);
                    po.setAppliedAmount(appliedAmounts.get(i));
                    po.setPayment(payment);
                    relations.add(po);
                }
            }
        }

        payment.setOrders(relations);
        paymentRepository.save(payment); // cascade 一起存入 PaymentOrder

        return "redirect:/payments/list";
    }
    
    @GetMapping("/list")
    public String listPayments(Model model) {
        List<Payment> payments = paymentRepository.findAll();
        model.addAttribute("payments", payments);
        return "payments/list";
    }

}

