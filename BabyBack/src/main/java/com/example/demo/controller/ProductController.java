package com.example.demo.controller;

import com.example.demo.dto.ProductDto;
import com.example.demo.model.Product;
import com.example.demo.model.ProductImage;
import com.example.demo.model.ProductType;
import com.example.demo.repository.ProductImageRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.ProductTypeRepository;
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
import java.util.Base64;
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


@CrossOrigin(origins = {"http://localhost:5500","http://127.0.0.1:5500","http://localhost:5501", "http://127.0.0.1:5501", "http://localhost:5503", "http://127.0.0.1:5503"}, allowCredentials = "true")
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
    private ProductTypeRepository ptRepository;
    
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
        model.addAttribute("productType", ptRepository.findAll());
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
    public String save(@ModelAttribute Product product, @RequestParam("type") Integer type, 
                       @RequestParam("imageFiles") MultipartFile[] imageFiles) throws IOException {
    	productService.save(product, imageFiles, type);  // 呼叫 Service 處理商品+圖片儲存
    	productRepository.save(product); 
        return "redirect:/products";
    }



    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Integer id, Model model) {
    	 Product product = productService.getById(id);
        model.addAttribute("product", product);
        model.addAttribute("selectType", product.getProductType().getId());
        model.addAttribute("productType", ptRepository.findAll());
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
        Map<Integer, String> imageMap = new HashMap<>(); 

        for (Product p : products) {
            // ✅ 修正：使用正確的庫存計算方法（進貨 - 銷售）
            Long calculatedStock = productService.getCurrentCalculatedStock(p.getId());
            int stock = calculatedStock != null ? calculatedStock.intValue() : 0;
            stockMap.put(p.getId(), stock);
            
            List<ProductImage> images = imageRepository.findByProductId(p.getId());
            if (!images.isEmpty()) {
                byte[] firstImageData = images.get(0).getImageData(); // 只用第一張
                String base64 = Base64.getEncoder().encodeToString(firstImageData);
                imageMap.put(p.getId(), base64);
            }

        }

        model.addAttribute("products", products);
        model.addAttribute("stockMap", stockMap);
        model.addAttribute("imageMap", imageMap);
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
    	
    	List<String> warnings = new ArrayList<>();
    	
    	for (int i = 0; i < productIds.size(); i++) {
    	    Integer id = productIds.get(i);
    	    BigDecimal price = prices.get(i);

    	    Product p = productService.getById(id);
    	    
    	    // 根據是否在 publishedIds 中來設定發布狀態
    	    boolean shouldPublish = publishedIds != null && publishedIds.contains(id);
    	    
    	    // 如果要發布商品，檢查是否符合上架條件
    	    if (shouldPublish) {
    	        if (!productService.canProductBePublished(id)) {
    	            warnings.add("商品「" + p.getName() + "」沒有進貨記錄，無法上架");
    	            shouldPublish = false; // 強制不上架
    	        }
    	    }
    	    
    	    p.setPublished(shouldPublish);
    	    
    	    // 設定商品價格（無論是否發布都要設定價格）
    	    if (price != null && price.compareTo(BigDecimal.ZERO) > 0) {
    	        p.setPrice(price.doubleValue());
    	        System.out.println(String.format("商品 %d (%s): 設定價格 = %s", 
    	            id, p.getName(), price));
    	    } else {
    	        System.out.println(String.format("商品 %d (%s): 價格無效，跳過設定", 
    	            id, p.getName()));
    	    }
    	    
    	    System.out.println(String.format("商品 %d (%s): published = %b", 
    	        id, p.getName(), shouldPublish));
    	    
    	    productService.save(p);
    	}
    	
    	// 建立提示訊息
    	int publishedCount = publishedIds != null ? publishedIds.size() : 0;
    	int actualPublished = 0;
    	for (Integer id : productIds) {
    	    Product p = productService.getById(id);
    	    if (Boolean.TRUE.equals(p.getPublished())) {
    	        actualPublished++;
    	    }
    	}
    	
    	String message = String.format("商品上架狀態已更新！實際發布 %d 個商品", actualPublished);
    	if (!warnings.isEmpty()) {
    	    message += "\\n警告：" + String.join("；", warnings);
    	}
    	
    	redirectAttributes.addFlashAttribute("successMessage", message);
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

            
            List<Map<String, Object>> result = publishedProducts.stream()
                .filter(p -> {
                    // 先計算庫存，只顯示庫存大於0的商品
                    Long calculatedStock = productService.getCurrentCalculatedStock(p.getId());
                    boolean hasStock = calculatedStock != null && calculatedStock > 0;
                    if (!hasStock) {
                        System.out.println("過濾掉庫存為0的商品 - ID: " + p.getId() + ", 名稱: " + p.getName() + ", 庫存: " + calculatedStock);
                    }
                    return hasStock;
                })
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
                    
                    // 處理價格 - 確保不為null
                    Double price = p.getPrice();
                    if (price == null || price <= 0) {
                        price = 100.0; // 預設價格
                    }
                    
                    // 創建包含價格和庫存的Map
                    Map<String, Object> productMap = new HashMap<>();
                    productMap.put("id", p.getId());
                    productMap.put("name", p.getName());
                    productMap.put("imageUrl", imageUrl);
                    productMap.put("primaryImageUrl", imageUrl);
                    // description欄位已移除
                    productMap.put("productTypeName", productTypeName);
                    productMap.put("productTypeId", productTypeId);
                    productMap.put("price", price);
                    productMap.put("stock", calculatedStock); // 動態計算的庫存
                    
//                    System.out.println("已發布商品API返回 - ID: " + p.getId() + ", 名稱: " + p.getName() + ", 價格: " + price + ", 庫存: " + calculatedStock);
                    
                    return productMap;
                })
                .toList();
            
            System.out.println("最終返回 " + result.size() + " 個商品（已過濾庫存為0的商品）");
            return result;
        } catch (Exception e) {
            System.err.println("獲取已發布商品時發生錯誤: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("無法獲取商品列表", e);
        }
    }
    
    @GetMapping("/front/product-types")
    @ResponseBody
    public List<ProductType> getProductTypes() {
    	return ptRepository.findAll();
    }
    
    
    // 新增：獲取單一商品詳情
    @GetMapping("/front/detail/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getProductDetail(@PathVariable Integer id) {
        try {
            Product product = productService.getById(id);
            if (product == null || product.getDeleted() || !product.getPublished()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "商品不存在、已被刪除或尚未發布");
                return ResponseEntity.notFound().build();
            }
            
            // 動態計算庫存
            Long calculatedStock = productService.getCurrentCalculatedStock(product.getId());
            
            // 檢查庫存，庫存為0的商品不允許查看詳情
            if (calculatedStock == null || calculatedStock <= 0) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "商品庫存不足，暫時無法查看");
                return ResponseEntity.notFound().build();
            }
            
            product.setCalculatedStock(calculatedStock);
            
            // 處理圖片URL
            String imageUrl = product.getImageUrl();
            if (imageUrl == null || imageUrl.isBlank()) {
                if (product.getImages() != null && !product.getImages().isEmpty()) {
                    imageUrl = "/products/front/images/" + product.getImages().get(0).getId();
                } else {
                    imageUrl = "/images/default.jpg";
                }
            }
            
            // 處理價格 - 確保不為null，如果為null則使用預設價格
            Double price = product.getPrice();
            if (price == null || price <= 0) {
                price = 100.0; // 預設價格
                System.out.println("商品ID " + id + " 價格為null或0，使用預設價格: " + price);
            }
            
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
            // description欄位已移除
            response.put("note", product.getNote());
            response.put("price", price);
            response.put("stock", calculatedStock);
            response.put("primaryImageUrl", imageUrl);
            response.put("allImageUrls", allImageUrls);
            response.put("specification", product.getSpecification());
            response.put("color", product.getColor());
            
            System.out.println("商品詳情API返回 - ID: " + id + ", 價格: " + price + ", 庫存: " + calculatedStock);
            
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

    // 新增：獲取每月商品銷售排行榜 - 前三名
    @GetMapping("/front/rankings/monthly")
    @ResponseBody
    public List<Map<String, Object>> getMonthlySalesRanking() {
        try {
            // 獲取所有已發布且有銷售記錄的商品
            List<Product> publishedProducts = productRepository.findByPublishedTrue();
            List<Map<String, Object>> rankingList = new ArrayList<>();
            
            for (Product product : publishedProducts) {
                // 計算該商品的總銷售數量（排除已取消的訂單）
                Long totalSales = productService.getTotalSalesQuantity(product.getId());
                
                if (totalSales != null && totalSales > 0) {
                    // 處理圖片URL
                    String imageUrl = product.getPrimaryImageUrl();
                    
                    // 處理價格
                    Double price = product.getPrice();
                    if (price == null || price <= 0) {
                        price = 100.0; // 預設價格
                    }
                    
                    Map<String, Object> rankingItem = new HashMap<>();
                    rankingItem.put("id", product.getId());
                    rankingItem.put("name", product.getName());
                    rankingItem.put("image", imageUrl);
                    rankingItem.put("price", price.intValue());
                    rankingItem.put("sold", totalSales.intValue());
                    
                    rankingList.add(rankingItem);
                }
            }
            
            // 按銷售數量降序排序，取前3名
            rankingList.sort((a, b) -> {
                Integer soldA = (Integer) a.get("sold");
                Integer soldB = (Integer) b.get("sold");
                return soldB.compareTo(soldA);
            });
            
            // 只返回前3名
            List<Map<String, Object>> top3 = rankingList.stream()
                    .limit(3)
                    .toList();
            
            System.out.println("月銷售排行榜API返回 " + top3.size() + " 個商品");
            return top3;
            
        } catch (Exception e) {
            System.err.println("獲取月銷售排行榜時發生錯誤: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // 新增：獲取二手商品排行榜 - 前三名 (暫時返回空列表，後續可擴展)
    @GetMapping("/front/rankings/secondhand")
    @ResponseBody
    public List<Map<String, Object>> getSecondhandRanking() {
        try {
            // 目前先返回空列表，後續可根據二手託售數據實現
            System.out.println("二手商品排行榜API被調用 - 暫時返回空列表");
            return new ArrayList<>();
        } catch (Exception e) {
            System.err.println("獲取二手商品排行榜時發生錯誤: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // 新增：測試API - 檢查銷售數據和排行榜邏輯
    @GetMapping("/front/rankings/debug")
    @ResponseBody
    public Map<String, Object> debugRankings() {
        Map<String, Object> debug = new HashMap<>();
        
        try {
            // 1. 檢查已發布商品
            List<Product> publishedProducts = productRepository.findByPublishedTrue();
            debug.put("publishedProductsCount", publishedProducts.size());
            debug.put("publishedProducts", publishedProducts.stream()
                .map(p -> Map.of("id", p.getId(), "name", p.getName(), "published", p.getPublished()))
                .toList());
            
            // 2. 檢查銷售記錄
            List<Map<String, Object>> salesData = new ArrayList<>();
            for (Product product : publishedProducts) {
                Long totalSales = productService.getTotalSalesQuantity(product.getId());
                String imageUrl = product.getPrimaryImageUrl();
                Double price = product.getPrice();
                
                Map<String, Object> productSales = new HashMap<>();
                productSales.put("id", product.getId());
                productSales.put("name", product.getName());
                productSales.put("totalSales", totalSales);
                productSales.put("imageUrl", imageUrl);
                productSales.put("price", price);
                productSales.put("published", product.getPublished());
                
                salesData.add(productSales);
            }
            debug.put("salesData", salesData);
            
            // 3. 檢查所有銷售記錄（不限於已發布商品）
            List<Product> allProducts = productRepository.findAll();
            List<Map<String, Object>> allSalesData = new ArrayList<>();
            for (Product product : allProducts.stream().limit(10).toList()) { // 限制10個避免太多數據
                Long totalSales = productService.getTotalSalesQuantity(product.getId());
                if (totalSales > 0) {
                    Map<String, Object> productSales = new HashMap<>();
                    productSales.put("id", product.getId());
                    productSales.put("name", product.getName());
                    productSales.put("totalSales", totalSales);
                    productSales.put("published", product.getPublished());
                    allSalesData.add(productSales);
                }
            }
            debug.put("allProductsWithSales", allSalesData);
            
            return debug;
            
        } catch (Exception e) {
            debug.put("error", e.getMessage());
            e.printStackTrace();
            return debug;
        }
    }
    
    // 測試頁面
    @GetMapping("/test-flow")
    public String testFlow() {
        return "test-flow";
    }
    
    // 檢查商品狀態API（除錯用）
    @GetMapping("/api/stock/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getProductStock(@PathVariable Integer id) {
        try {
            Long calculatedStock = productService.getCurrentCalculatedStock(id);
            int stock = calculatedStock != null ? calculatedStock.intValue() : 0;
            
            Map<String, Object> response = new HashMap<>();
            response.put("productId", id);
            response.put("stock", stock);
            response.put("success", true);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("productId", id);
            errorResponse.put("stock", 0);
            errorResponse.put("success", false);
            errorResponse.put("message", "庫存查詢失敗: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/api/status/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getProductStatus(@PathVariable Integer id) {
        try {
            Map<String, Object> status = productService.getProductStatus(id);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "查詢商品狀態失敗: " + e.getMessage()
            ));
        }
    }

}
