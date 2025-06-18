package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.model.ProductImage;
import com.example.demo.model.Supplier;
import com.example.demo.model.SupplierProduct;
import com.example.demo.repository.ProductImageRepository;
import com.example.demo.service.ProductService;
import com.example.demo.service.SupplierProductService;
import com.example.demo.service.SupplierService;

import java.io.IOException;
import java.net.URLConnection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductService service;
    
    @Autowired
    private SupplierService supplierService;
    
    @Autowired
    private SupplierProductService supplierProductService;


    //0611喬新增
    @GetMapping
    public String list(Model model) {
        model.addAttribute("products", service.getAllProductsWithStock());
        return "product/list";
    }
    
    
//    @GetMapping
//    public String list(Model model) {
//        model.addAttribute("products", service.listAll());
//        return "product/list";
//    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("suppliers", supplierService.listAll());
        model.addAttribute("supplierProducts", supplierProductService.listAll());
        return "product/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Product product,
    				   @RequestParam("supplierId") Long supplierId ) throws IOException { //接收 <input type="file" name="images" multiple> 的所有上傳圖
    	 Supplier supplier = supplierService.getById(supplierId);
    	 product.setSupplier(supplier);
    	
        service.save(product);
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
    	 Product product = service.getById(id);
        model.addAttribute("product", product);
        model.addAttribute("suppliers", supplierService.listAll());
        model.addAttribute("images", product.getImages()); // 讓 HTML 可顯示圖片
        return "product/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    	 try {
    	        service.delete(id);
    	        redirectAttributes.addFlashAttribute("message", "刪除成功");
    	    } catch (IllegalStateException e) {
    	        redirectAttributes.addFlashAttribute("error", e.getMessage());
    	    }
    	    return "redirect:/products";
    }
    
    
    @Autowired
    private ProductImageRepository imageRepository;
    
    @GetMapping("/images/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> serveImage(@PathVariable Long id) {
        ProductImage image = imageRepository.findById(id).orElse(null);
        if (image == null || image.getImageData() == null) {
            return ResponseEntity.notFound().build();
        }

        String contentType = URLConnection.guessContentTypeFromName(image.getImagePath());
        return ResponseEntity.ok()
            .header("Content-Type", contentType != null ? contentType : "application/octet-stream")
            .body(image.getImageData());
    }
    
    @DeleteMapping("/images/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteImage(@PathVariable Long id) {
        ProductImage image = imageRepository.findById(id).orElse(null);
        if (image == null) {
            return ResponseEntity.notFound().build();
        }

        imageRepository.deleteById(id);
        return ResponseEntity.ok("deleted");
    }
    
    @GetMapping("/view/{id}")
    public String viewDetail(@PathVariable Long id, Model model) {
        Product product = service.getById(id);
        model.addAttribute("product", product);
        return "product/view";
    }

}
