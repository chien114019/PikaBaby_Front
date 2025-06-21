package com.example.demo.controller;

import com.example.demo.dto.ProductDto;
import com.example.demo.model.Product;
import com.example.demo.model.ProductImage;
import com.example.demo.repository.ProductImageRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.ProductService;
import com.example.demo.service.SupplierProductService;
import com.example.demo.service.SupplierService;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductService service;
    
    @Autowired
    private SupplierService supplierService;
    
    @Autowired
    private SupplierProductService supplierProductService;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ProductImageRepository imageRepository;
    


    //0611喬新增
    @GetMapping
    public String list(@RequestParam(required = false) Boolean showDeleted, Model model) {
    	 List<Product> products;

    	    if (Boolean.TRUE.equals(showDeleted)) {
    	        products = service.findAll(); // 包含 deleted = true
    	    } else {
    	        products = service.findActive(); // 只取 deleted = false
    	    }

    	    model.addAttribute("products", products);
    	    model.addAttribute("showDeleted", showDeleted != null && showDeleted);
    	    return "product/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("suppliers", supplierService.listAll());
        model.addAttribute("supplierProducts", supplierProductService.listAll());
        return "product/form";
    }

    //原本
//    @PostMapping("/save")
//    public String save(@ModelAttribute Product product,
//                       @RequestParam("image") MultipartFile imageFile) throws IOException {
//
//        if (imageFile != null && !imageFile.isEmpty()) {
//            String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
//            Path uploadPath = Paths.get("src/main/resources/static/uploads/");
//            
//            // 建立資料夾（如果不存在）
//            if (!Files.exists(uploadPath)) {
//                Files.createDirectories(uploadPath);
//            }
//
//            // 儲存圖片
//            Path filePath = uploadPath.resolve(fileName);
//            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
//
//            // 儲存圖片路徑到產品
//            product.setImageUrl("/static/" + fileName);
//        }
//
//        service.save(product);
//        return "redirect:/products";
//    }
    
    //0621更改存多圖片
    @PostMapping("/save")
    public String save(@ModelAttribute Product product,
                       @RequestParam("imageFiles") MultipartFile[] imageFiles) throws IOException {
        service.save(product, imageFiles);  // 呼叫 Service 處理商品+圖片儲存
        return "redirect:/products";
    }



    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Integer id, Model model) {
    	 Product product = service.getById(id);
        model.addAttribute("product", product);
        model.addAttribute("suppliers", supplierService.listAll());
        model.addAttribute("images", product.getImages()); // 讓 HTML 可顯示圖片
        return "product/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
    	 try {
    	        service.delete(id);
    	        redirectAttributes.addFlashAttribute("message", "刪除成功");
    	    } catch (IllegalStateException e) {
    	        redirectAttributes.addFlashAttribute("error", e.getMessage());
    	    }
    	    return "redirect:/products";
    }
    
    @PostMapping("/restore/{id}")
    public String restoreProduct(@PathVariable Integer id) {
        Product product = service.getById(id);
        product.setDeleted(false);
        service.save(product);
        return "redirect:/products?showDeleted=true";
    }
  
    @GetMapping("/view/{id}")
    public String viewDetail(@PathVariable Integer id, Model model) {
        Product product = service.getById(id);
        model.addAttribute("product", product);
        return "product/view";
    }
    
    @GetMapping("/publish")
    public String publishList(Model model) {
        List<Product> products = service.listAll();
        Map<Integer, Integer> stockMap = new HashMap<>();

        for (Product p : products) {
            int stock = (int) service.calculateStock(p.getId());
            stockMap.put(p.getId(), stock);
        }

        model.addAttribute("products", products);
        model.addAttribute("stockMap", stockMap);
        return "product/publish";
    }


    @PostMapping("/publish/update")
    public String updatePublishStatus(@RequestParam("productIds") List<Integer> productIds,
    								  @RequestParam("prices") List<BigDecimal> prices,
                                      @RequestParam(value = "publishedIds", required = false) List<Integer> publishedIds) {
    	for (int i = 0; i < productIds.size(); i++) {
    	    Integer id = productIds.get(i);
    	    BigDecimal price = prices.get(i);

    	    Product p = service.getById(id);
    	    p.setPrice(price);
    	    p.setPublished(publishedIds != null && publishedIds.contains(id));
    	    service.save(p);
    	}

        return "redirect:/product/publish";
    }
    
//    ============= 前台API ==============
    
    @GetMapping("/front/images/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> serveImage(@PathVariable Integer id) {
        ProductImage image = imageRepository.findById(id).orElse(null);
        if (image == null || image.getImageData() == null) {
            return ResponseEntity.notFound().build();
        }

        String contentType = URLConnection.guessContentTypeFromName(image.getImagePath());
        return ResponseEntity.ok()
            .header("Content-Type", contentType != null ? contentType : "application/octet-stream")
            .body(image.getImageData());
    }
    
    @DeleteMapping("/front/images/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteImage(@PathVariable Integer id) {
        ProductImage image = imageRepository.findById(id).orElse(null);
        if (image == null) {
            return ResponseEntity.notFound().build();
        }

        imageRepository.deleteById(id);
        return ResponseEntity.ok("deleted");
    }
    
    @GetMapping("/front/published")
    @ResponseBody
    public List<ProductDto> getPublishedProducts() {
        return productRepository.findByPublishedTrue()
            .stream()
            .map(p -> new ProductDto(p.getId(), p.getName(), p.getImageUrl(), p.getDescription(), p.getPrice()))
            .toList();
    }



}
