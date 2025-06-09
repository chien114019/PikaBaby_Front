package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.model.Receivable;
import com.example.demo.repository.ReceivableRepository;

@Controller
@RequestMapping("/receivables")
public class ReceivableController {

    @Autowired
    private ReceivableRepository receivableRepository;

    @GetMapping("/list")
    public String listReceivables(Model model) {
        List<Receivable> receivables = receivableRepository.findAll();
        model.addAttribute("receivables", receivables);
        return "receivables/list";
    }
}

