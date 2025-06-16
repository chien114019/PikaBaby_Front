package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.model.AccountsPayable;
import com.example.demo.repository.AccountsPayableRepository;

@Controller
@RequestMapping("/payables")
public class AccountsPayableController {

    @Autowired
    private AccountsPayableRepository payableRepo;

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("payables", payableRepo.findAll());
        return "payables/list";
    }

    @GetMapping("/pay/{id}")
    public String pay(@PathVariable Long id) {
        AccountsPayable payable = payableRepo.findById(id).orElseThrow();
        payable.setStatus("已付款");
        payableRepo.save(payable);
        return "redirect:/payables";
    }
}

