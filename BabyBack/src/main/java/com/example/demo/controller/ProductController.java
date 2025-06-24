package com.example.demo.controller;

import com.example.demo.dto.ProductDto;
import com.example.demo.model.Product;
import com.example.demo.model.ProductImage;
import com.example.demo.repository.ProductImageRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.PurchaseOrderDetailRepository;
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
import java.util.ArrayList;
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
    private ProductService productService;
    
    @Autowired
    private SupplierService supplierService;
    
    @Autowired
    private SupplierProductService supplierProductService;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private ProductImageRepository imageRepository;
    
    @Autowired
    private PurchaseOrderDetailRepository purchaseOrderDetailRepository;
    


    //0611喬新增
    @GetMapping
    public String list(@RequestParam(required = false) Boolean showDeleted, Model model) {
    	 List<Product> products;

    	    if (Boolean.TRUE.equals(showDeleted)) {
    	        products = productService.findAll(); // 包含 deleted = true
    	    } else {
    	        products = productService.findActive(); // 只取 deleted = false
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
        model.addAttribute("allAgeRanges", List.of("嬰幼兒（0-3M）", "幼童（3-6M）", "兒童（6-12M）", "青少年（2-3y以上）"));
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
    	productService.save(product, imageFiles);  // 呼叫 Service 處理商品+圖片儲存
    	productRepository.save(product); 
        return "redirect:/products";
    }



    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Integer id, Model model) {
    	 Product product = productService.getById(id);
        model.addAttribute("product", product);
        model.addAttribute("suppliers", supplierService.listAll());
        model.addAttribute("images", product.getImages()); // 讓 HTML 可顯示圖片
        model.addAttribute("allAgeRanges", List.of("嬰幼兒（0-3M）", "幼童（3-6M）", "兒童（6-12M）", "青少年（2-3y以上）"));
        return "product/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
    	 try {
    	        productService.delete(id);
    	        redirectAttributes.addFlashAttribute("message", "刪除成功");
    	    } catch (IllegalStateException e) {
    	        redirectAttributes.addFlashAttribute("error", e.getMessage());
    	    }
    	    return "redirect:/products";
    }
    
    @PostMapping("/restore/{id}")
    public String restoreProduct(@PathVariable Integer id) {
        Product product = productService.getById(id);
        product.setDeleted(false);
        productService.save(product);
        return "redirect:/products?showDeleted=true";
    }
  
    @GetMapping("/view/{id}")
    public String viewDetail(@PathVariable Integer id, Model model) {
        Product product = productService.getById(id);
        model.addAttribute("product", product);
        return "product/view";
    }
    
    @GetMapping("/publish")
    public String publishList(Model model) {
        List<Product> products = purchaseOrderDetailRepository.findDistinctProductsInPurchaseHistory();
        Map<Integer, Integer> stockMap = new HashMap<>();

        for (Product p : products) {
            int stock = (int) productService.calculateStock(p.getId());
            stockMap.put(p.getId(), stock);
        }

        model.addAttribute("products", products);
        model.addAttribute("stockMap", stockMap);
        return "product/publish";
    }


    @PostMapping("/publish/update")
    public String updatePublishStatus(@RequestParam("productIds") List<Integer> productIds,
    								  @RequestParam("prices") List<BigDecimal> prices,
                                      @RequestParam(value = "publishedIds", required = false) List<Integer> publishedIds,
                                      RedirectAttributes redirectAttributes) {
    	
    	System.out.println("=== 商品發布狀態更新 ===");
    	System.out.println("productIds: " + productIds);
    	System.out.println("publishedIds: " + publishedIds);
    	
    	for (int i = 0; i < productIds.size(); i++) {
    	    Integer id = productIds.get(i);
    	    BigDecimal price = prices.get(i);

    	    Product p = productService.getById(id);
    	    // 價格現在通過SupplierProduct管理，不再直接設定到Product
    	    // TODO: 需要更新對應的SupplierProduct價格
    	    
    	    // 根據是否在 publishedIds 中來設定發布狀態
    	    // 這樣既能發布新商品，也能取消發布
    	    boolean shouldPublish = publishedIds != null && publishedIds.contains(id);
    	    p.setPublished(shouldPublish);
    	    
    	    System.out.println(String.format("商品 %d (%s): published = %b", 
    	        id, p.getName(), shouldPublish));
    	    
    	    productService.save(p);
    	}
    	
    	// 顯示發布狀態統計
    	int publishedCount = publishedIds != null ? publishedIds.size() : 0;
    	redirectAttributes.addFlashAttribute("successMessage", 
    	    String.format("商品上架狀態已更新！目前共 %d 個商品已發布", publishedCount));
        return "redirect:/products/publish";
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
    
    // 測試端點 - 檢查商品狀態
    @GetMapping("/front/test")
    @ResponseBody
    public Map<String, Object> testProducts() {
        List<Product> allProducts = productRepository.findAll();
        List<Product> publishedProducts = productRepository.findByPublishedTrue();
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalProducts", allProducts.size());
        result.put("publishedProducts", publishedProducts.size());
        result.put("allProductsStatus", allProducts.stream()
            .map(p -> Map.of("id", p.getId(), "name", p.getName(), "published", p.getPublished()))
            .toList());
        
        return result;
    }
    
    // 快速發布所有商品 - 僅用於測試
    @PostMapping("/front/quick-publish")
    @ResponseBody
    public Map<String, Object> quickPublishAllProducts() {
        try {
            List<Product> allProducts = productRepository.findAll();
            int publishedCount = 0;
            
            for (Product product : allProducts) {
                // 不再需要檢查價格，因為價格現在從SupplierProduct獲取
                product.setPublished(true);
                productService.save(product);
                publishedCount++;
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "成功發布 " + publishedCount + " 個商品");
            result.put("publishedCount", publishedCount);
            
            return result;
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "發布商品時發生錯誤: " + e.getMessage());
            return result;
        }
    }
    
    @GetMapping("/front/published")
    @ResponseBody
    public List<Map<String, Object>> getPublishedProducts() {
        try {
            List<Product> publishedProducts = productRepository.findByPublishedTrue();
            System.out.println("找到 " + publishedProducts.size() + " 個已發布的商品");
            
            return publishedProducts.stream()
                .map(p -> {
                    // 處理圖片URL
                    String imageUrl = p.getImageUrl();
                    if (imageUrl == null || imageUrl.isBlank()) {
                        // 如果沒有圖片URL，嘗試從product_image表獲取第一張圖片
                        if (p.getImages() != null && !p.getImages().isEmpty()) {
                            imageUrl = "/products/front/images/" + p.getImages().get(0).getId();
                        } else {
                            imageUrl = "/images/default.jpg";
                        }
                    }
                    
                    // 獲取ProductType資訊
                    String productTypeName = p.getProductType() != null ? p.getProductType().getTypeName() : null;
                    Integer productTypeId = p.getProductType() != null ? p.getProductType().getId() : null;
                    
                    // 動態計算庫存
                    Long calculatedStock = productService.getCurrentCalculatedStock(p.getId());
                    p.setCalculatedStock(calculatedStock);
                    
                    // 創建包含價格和庫存的Map
                    Map<String, Object> productMap = new HashMap<>();
                    productMap.put("id", p.getId());
                    productMap.put("name", p.getName());
                    productMap.put("imageUrl", imageUrl);
                    productMap.put("primaryImageUrl", imageUrl);
                    productMap.put("description", p.getDescription());
                    productMap.put("productTypeName", productTypeName);
                    productMap.put("productTypeId", productTypeId);
                    productMap.put("price", p.getPrice()); // 從SupplierProduct獲取的價格
                    productMap.put("stock", calculatedStock); // 動態計算的庫存
                    
                    return productMap;
                })
                .toList();
        } catch (Exception e) {
            System.err.println("獲取已發布商品時發生錯誤: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("無法獲取商品列表", e);
        }
    }
    
    // 新增：獲取所有有效商品（包括未發布的）
    @GetMapping("/front/all")
    @ResponseBody
    public List<Map<String, Object>> getAllActiveProducts() {
        try {
            // 獲取所有未被刪除的商品
            List<Product> activeProducts = productRepository.findByDeletedFalse();
            System.out.println("找到 " + activeProducts.size() + " 個有效商品");
            
            return activeProducts.stream()
                .map(p -> {
                    // 處理圖片URL
                    String imageUrl = p.getImageUrl();
                    if (imageUrl == null || imageUrl.isBlank()) {
                        // 如果沒有圖片URL，嘗試從product_image表獲取第一張圖片
                        if (p.getImages() != null && !p.getImages().isEmpty()) {
                            imageUrl = "/products/front/images/" + p.getImages().get(0).getId();
                        } else {
                            imageUrl = "/images/default.jpg";
                        }
                    }
                    
                    // 獲取ProductType資訊
                    String productTypeName = p.getProductType() != null ? p.getProductType().getTypeName() : null;
                    Integer productTypeId = p.getProductType() != null ? p.getProductType().getId() : null;
                    
                    // 動態計算庫存
                    Long calculatedStock = productService.getCurrentCalculatedStock(p.getId());
                    p.setCalculatedStock(calculatedStock);
                    
                    // 創建包含價格和庫存的Map
                    Map<String, Object> productMap = new HashMap<>();
                    productMap.put("id", p.getId());
                    productMap.put("name", p.getName());
                    productMap.put("imageUrl", imageUrl);
                    productMap.put("primaryImageUrl", imageUrl);
                    productMap.put("description", p.getDescription());
                    productMap.put("productTypeName", productTypeName);
                    productMap.put("productTypeId", productTypeId);
                    productMap.put("price", p.getPrice()); // 從SupplierProduct獲取的價格
                    productMap.put("stock", calculatedStock); // 動態計算的庫存
                    
                    return productMap;
                })
                .toList();
        } catch (Exception e) {
            System.err.println("獲取商品時發生錯誤: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("無法獲取商品列表", e);
        }
    }
    
    // 新增：獲取單一商品詳情
    @GetMapping("/front/detail/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getProductDetail(@PathVariable Integer id) {
        try {
            Product product = productService.getById(id);
            if (product == null || product.getDeleted()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "商品不存在或已被刪除");
                return ResponseEntity.notFound().build();
            }
            
            // 處理圖片URL
            String imageUrl = product.getImageUrl();
            if (imageUrl == null || imageUrl.isBlank()) {
                if (product.getImages() != null && !product.getImages().isEmpty()) {
                    imageUrl = "/products/front/images/" + product.getImages().get(0).getId();
                } else {
                    imageUrl = "/images/default.jpg";
                }
            }
            
            // 處理價格 - 從SupplierProduct獲取
            Double price = product.getPrice(); // 這現在會自動從SupplierProduct獲取最新價格
            
            // 獲取所有圖片URL
            List<String> allImageUrls = new ArrayList<>();
            if (product.getImages() != null && !product.getImages().isEmpty()) {
                for (ProductImage img : product.getImages()) {
                    allImageUrls.add("/products/front/images/" + img.getId());
                }
            }
            if (allImageUrls.isEmpty()) {
                allImageUrls.add("../images/baby.jpg");
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("id", product.getId());
            response.put("name", product.getName());
            response.put("description", product.getDescription());
            response.put("note", product.getNote());
            response.put("price", price);
            response.put("stock", product.getCalculatedStock());
            response.put("primaryImageUrl", imageUrl);
            response.put("allImageUrls", allImageUrls);
            response.put("specification", product.getSpecification());
            response.put("color", product.getColor());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("獲取商品詳情時發生錯誤: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "無法獲取商品詳情: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }



}
