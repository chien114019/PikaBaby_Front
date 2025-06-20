package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.dto.SalesStatsDTO;
import com.example.demo.repository.PurchaseOrderRepository;
import com.example.demo.repository.SalesOrderRepository;


@RequestMapping("/dashboard")
@Controller
public class DashboardController {
    @Autowired
    private SalesOrderRepository orderRepository;
    
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @GetMapping("/sales")
    public String showDashboard(Model model) {
        List<Object[]> rawStats = orderRepository.getMonthlySalesRaw();

        List<String> months = new ArrayList<>();
        List<Double> totals = new ArrayList<>();

        for (Object[] row : rawStats) {
            months.add((String) row[0]);
            totals.add(((Number) row[1]).doubleValue());
        }

        model.addAttribute("months", months);
        model.addAttribute("totals", totals);
        return "dashboard/sales";
    }
    
    @GetMapping("/purchase")
    public String showPurchaseChart(Model model) {
        List<Object[]> rawStats = purchaseOrderRepository.getMonthlyPurchaseCostRaw();
        List<String> months = new ArrayList<>();
        List<Double> totals = new ArrayList<>();

        for (Object[] row : rawStats) {
            months.add((String) row[0]);
            totals.add(((Number) row[1]).doubleValue());
        }

        model.addAttribute("months", months);
        model.addAttribute("totals", totals);
        return "dashboard/purchase";
    }

}

