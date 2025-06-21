package com.example.demo.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Integer id, Model model) {
    	Receivable receivable = receivableRepository.findById(id).orElse(null);
    	model.addAttribute("receivable", receivable);
    	return "receivables/edit";
    }
    
    @PostMapping("/edit")
    public String update(@ModelAttribute Receivable receivableForm) {
    	Receivable r = receivableRepository.findById(receivableForm.getId()).orElse(null);
    	if(r != null) {
    		r.setStatus(receivableForm.getStatus());
    		r.setPaidDate(receivableForm.getPaidDate());
    		r.setNote(receivableForm.getNote());
    		receivableRepository.save(r);
    	}
    	return "redirect:/receivables/list";
    }
    
    @GetMapping("/receivables/new")
    public String newReceivableForm(Model model) {
        model.addAttribute("receivable", new Receivable()); // ✅ 空表單預填用
        return "receivables/edit";
    }

}

