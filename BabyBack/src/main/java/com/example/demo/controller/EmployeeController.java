package com.example.demo.controller;

import com.example.demo.model.UserAccount;
import com.example.demo.repository.UserAccountRepository;
import com.example.demo.service.UserAccountService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class EmployeeController {

    @Autowired
    private UserAccountRepository userAccountRepository;
    
    @Autowired
    private UserAccountService userAccountService;

    
//    @GetMapping("/employee/list") // 當使用者打開 /employee/list 時，執行這個方法，從資料庫撈出員工清單，並帶到前端 Thymeleaf 頁面
//    public String showEmployeeList(@RequestParam(required = false) String keyword, Model model) {  //回傳型別是 String（會回傳要顯示的 Thymeleaf 頁面名稱）;Model 是 Spring MVC 提供的物件，用來把後端資料 帶給前端頁面用，就像是「裝資料的背包
//        List<UserAccount> employees;
//        if (keyword != null && !keyword.isEmpty()) {
//            employees = userAccountRepository.findByUsernameContainingIgnoreCase(keyword);
//        } else {
//            employees = userAccountRepository.findAll();
//        }
//        model.addAttribute("employees", employees);//把剛剛查詢出來的員工清單 employees 存進 model 中，並取名為 "employees" //這樣前端的 Thymeleaf 頁面才能用 th:each="emp : ${employees}" 來一筆筆渲染資料。
//        model.addAttribute("keyword", keyword); // 使用者看到「剛剛打了什麼搜尋關鍵字」
//        return "/employee/list";//回傳要顯示的頁面名稱（Thymeleaf 模板）
//    }
    
    //使用關鍵字查詢員工帳號
    @GetMapping("/employee/list")
    public String listEmployees(@RequestParam(value = "keyword", required = false) String keyword, Model model) { //required = false：這個參數「不是必填」
        List<UserAccount> employees;
        if (keyword != null && !keyword.isBlank()) {
            employees = userAccountRepository.findByUsernameContainingIgnoreCase(keyword);
        } else {
            employees = userAccountRepository.findAll();
        }
        model.addAttribute("employees", employees);
        return "employee/list"; // 對應你的 HTML 頁面
    }
    
   //帳號切換啟用停用
    @PostMapping("/employee/toggle")
    public String toggleAccountStatus(@RequestParam("id") Integer id) {
        UserAccount emp = userAccountRepository.findById(id).orElse(null);
        if (emp != null)  {
            Boolean current = emp.getEnabled();
            if (current == null) {
                current = false; // 預設值false停用
            }
            emp.setEnabled(!current);
            userAccountRepository.save(emp);
        }
        return "redirect:/employee/list"; // redirect: 是 Spring MVC 裡面專門用來「重新導向」頁面的指令，執行完回到員工清單
       

    }
  //帳號切換員工角色權限
    @PostMapping("/employee/role") //來自 <form method="post" action="/employee/role"> 的表單
    public String updateRole(@RequestParam Integer id, @RequestParam String role) { //Long id：從表單中取出名為 id 的欄位值（員工的 ID）;從表單中取出名為 role 的欄位值（新角色)。在 Modal 裡面用 <input name="id"> 和 <select name="role"> 
        UserAccount emp = userAccountRepository.findById(id).orElse(null); //透過主鍵 ID 查出這位員工資料，找不到該 ID就回傳 null
        if (emp != null) { //如果有找到ID 
            emp.setRole(role); //設定新角色
            userAccountRepository.save(emp); //把修改後的員工物件存回資料庫中（JPA 自動產生 UPDATE 語法）
        }
        return "redirect:/employee/list";
    }
    //新增帳號
    @GetMapping("/employee/new")
    public String newEmployee(Model model) {
        model.addAttribute("employee", new UserAccount());
        return "employee/form";
    }
    
    @PostMapping("/employee/save")
    public String saveEmployee(@ModelAttribute("employee") UserAccount userAccount) {
        userAccountService.save(userAccount); // Service 裡面已經會做「密碼加密」與「新增 vs 編輯」的判斷
        return "redirect:/employee/list";
    }
    
    @GetMapping("/employee/edit/{id}")
    public String editEmployee(@PathVariable Integer id, Model model) {
        UserAccount user = userAccountRepository.findById(id).orElse(null);
        model.addAttribute("employee", user);
        return "employee/form"; // 進入與新增共用的表單頁
    }


}
