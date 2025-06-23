package com.example.demo.controller;

import com.example.demo.model.Supplier;
import com.example.demo.service.SupplierService; // 改用 Service
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


//這整支 Controller 的所有功能，網址前面都會是 /suppliers。例如：/suppliers/edit/3、/suppliers/delete/2
@Controller
@RequestMapping("/suppliers")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    //對應 Service 的：public List<Supplier> listAll();public List<Supplier> searchByName(String keyword)
    @GetMapping
    public String list(@RequestParam(required = false) String keyword, Model model) {
        List<Supplier> suppliers;
        if (keyword != null && !keyword.isEmpty()) {
            suppliers = supplierService.searchByName(keyword); // 透過 Service 做模糊搜尋
        } else {
            suppliers = supplierService.listAll(); // 沒有輸入就列出全部
        }
        model.addAttribute("suppliers", suppliers);//丟給 list.html 顯示清單
        model.addAttribute("keyword", keyword); // 回填搜尋框的內容
        return "supplier/list";
    }

    // 建一個空物件給 form 綁定用
    @GetMapping("/new")
    public String newSupplier(Model model) {
        model.addAttribute("supplier", new Supplier());
        return "supplier/form";
    }

    
    //編輯已有的供應商 對應 Service 的：public Supplier getById(Integer id)
    @GetMapping("/edit/{id}")
    public String editSupplier(@PathVariable Integer id, Model model) {
        Supplier supplier = supplierService.getById(id);
        model.addAttribute("supplier", supplier);
        return "supplier/form";
    }

    //儲存（新增或更新）對應 Service 的：public void save(Supplier supplier)
    @PostMapping("/save")
    public String saveSupplier(@ModelAttribute Supplier supplier) {
        supplierService.save(supplier);// 新增或更新供應商
        return "redirect:/suppliers";// 儲存後重新導回清單
    }

    //刪除供應商 對應 Service 的：public void deleteById(Integer id)
    @GetMapping("/delete/{id}")
    public String deleteSupplier(@PathVariable Integer id) {
        supplierService.deleteById(id);
        return "redirect:/suppliers";
    }
}
