package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.model.AccountsPayable;
import com.example.demo.repository.AccountsPayableRepository;

@Controller
@RequestMapping("/payables")
public class AccountsPayableController {

    @Autowired
    private AccountsPayableRepository payableRepo;
    
    @GetMapping("")
    public String listPayables(Model model) {
        model.addAttribute("payables", payableRepo.findAll());
        return "payables/list";
    }

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("payables", payableRepo.findAll());
        return "payables/list";
    }

    @GetMapping("/pay/{id}")
    public String pay(@PathVariable Integer id) {
        AccountsPayable payable = payableRepo.findById(id).orElseThrow();
        payable.setStatus("已付款");
        payableRepo.save(payable);
        return "redirect:/payables";
    }
    
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Integer id, Model model) {
        AccountsPayable payable = payableRepo.findById(id).orElseThrow();
        model.addAttribute("payable", payable);
        return "payables/edit";
    }

    @PostMapping("/edit")
    public String update(@ModelAttribute AccountsPayable payable) {
        // 更新狀態、付款日、備註
        AccountsPayable existing = payableRepo.findById(payable.getId()).orElseThrow();
        existing.setStatus(payable.getStatus());
        existing.setEndDate(payable.getEndDate());
        existing.setNote(payable.getNote());
        payableRepo.save(existing);
        return "redirect:/payables";
    }

}

