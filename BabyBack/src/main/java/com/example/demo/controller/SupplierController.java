package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.model.Supplier;
import com.example.demo.service.SupplierService; // 改用 Service
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


//這整支 Controller 的所有功能，網址前面都會是 /suppliers。例如：/suppliers/edit/3、/suppliers/delete/2
@Controller
@RequestMapping("/suppliers")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    //對應 Service 的：public List<Supplier> listAll();public List<Supplier> searchByName(String keyword)
    @GetMapping
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) Boolean showDeleted,
                       Model model) {
        List<Supplier> suppliers;

        if (keyword != null && !keyword.isEmpty()) {
            suppliers = supplierService.searchByName(keyword);
        } else if (Boolean.TRUE.equals(showDeleted)) {
            suppliers = supplierService.listAll();  // 全部包含已刪除
        } else {
            suppliers = supplierService.findAllNotDeleted();  // 僅顯示未刪除
        }

        model.addAttribute("suppliers", suppliers);
        model.addAttribute("keyword", keyword);
        model.addAttribute("showDeleted", showDeleted);
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
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
    	 try {
    		 supplierService.deleteById(id);
    	        redirectAttributes.addFlashAttribute("message", "刪除成功");
    	    } catch (IllegalStateException e) {
    	        redirectAttributes.addFlashAttribute("error", e.getMessage());
    	    }
    	    return "redirect:/suppliers";
    }
    
    @PostMapping("/restore/{id}")
    public String restoreSupplier(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            Supplier supplier = supplierService.getById(id);
            supplier.setDeleted(false);
            supplierService.save(supplier);
            redirectAttributes.addFlashAttribute("message", "供應商已還原");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "還原失敗：" + e.getMessage());
        }
        return "redirect:/suppliers?showDeleted=true";  // 確保 redirect 回供應商頁面
    }

}
