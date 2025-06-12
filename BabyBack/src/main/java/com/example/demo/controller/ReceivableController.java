package com.example.demo.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.Receivable;
import com.example.demo.repository.ReceivableRepository;
import com.example.demo.service.ReceivableService;

@Controller
@RequestMapping("/receivables")
public class ReceivableController {

    @Autowired
    private ReceivableRepository receivableRepository;
    //0609喬新增    
    @Autowired
    private ReceivableService receivableService;


    @GetMapping("/list")
    public String listReceivables(Model model) {
        List<Receivable> receivables = receivableRepository.findAll();
        model.addAttribute("receivables", receivables);
        return "receivables/list";
    }
    
    //0609喬新增
    //可以查詢廠商關鍵字、日期區間，但一定要輸入關鍵字
    @GetMapping("/search")
    public String searchReceivables(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
        Model model
    ) {
        List<Receivable> results = receivableService.search(keyword, startDate, endDate);
        model.addAttribute("receivables", results);
        return "receivables/list"; 
    }
}

