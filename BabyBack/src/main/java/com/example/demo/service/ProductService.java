package com.example.demo.service;

import com.example.demo.model.Product;
import com.example.demo.model.ProductImage;
import com.example.demo.model.ProductType;
import com.example.demo.model.PurchaseOrderDetail;
import com.example.demo.repository.ProductImageRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.ProductTypeRepository;
import com.example.demo.repository.PurchaseOrderDetailRepository;
import com.example.demo.repository.SalesOrderDetailRepository;
import com.example.demo.repository.SupplierProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository repository;

    @Autowired
    private PurchaseOrderDetailRepository purchaseDetailRepository;

    @Autowired
    private SalesOrderDetailRepository salesOrderDetailRepository;

    @Autowired
    private ProductImageRepository imageRepository;
    
    @Autowired 
    private SupplierProductRepository supplierProductRepository;
    
    @Autowired
    private ProductTypeRepository ptRepository;

    public List<Product> listAll() {
    	return repository.findByDeletedFalse();
    }

    public Product getById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    // 安全版本：處理重複記錄問題
    public Product getByIdSafe(Integer id) {
        try {
            Optional<Product> productOpt = repository.findById(id);
            return productOpt.orElse(null);
        } catch (Exception e) {
            // 如果遇到重複記錄錯誤，嘗試使用原生查詢
            System.err.println("getById遇到錯誤，嘗試使用原生查詢: " + e.getMessage());
            try {
                List<Product> products = repository.findAll();
                return products.stream()
                    .filter(p -> p.getId().equals(id))
                    .findFirst()
                    .orElse(null);
            } catch (Exception e2) {
                System.err.println("原生查詢也失敗: " + e2.getMessage());
                return null;
            }
        }
    }

    public void save(Product product) {
        repository.save(product);
    }

    public void delete(Integer id) {
        Product product = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("商品不存在"));
        
        product.setDeleted(true);
        repository.save(product);
    }


    public List<Product> getAllProducts() {
        return repository.findByDeletedFalse();
    }

    public List<Product> searchByName(String keyword) {
        return repository.searchAvailable(keyword);
    }

    // 前台電商用：快速庫存查詢
    public List<Product> getAllProductsWithStock() {
        // 直接返回資料庫中的商品，使用stock欄位
        return repository.findAll();
    }
    
    // 後台ERP用：動態計算庫存
    public List<Product> getAllProductsWithCalculatedStock() {
        List<Product> products = repository.findAll();
        
        for (Product p : products) {
            Long calculatedStock = getCurrentCalculatedStock(p.getId());
            p.setCalculatedStock(calculatedStock);
        }
        
        return products;
    }

    // 前台電商用：快速庫存查詢（改為使用計算庫存）
    public Long getCurrentStock(Integer productId) {
        return getCurrentCalculatedStock(productId);
    }
    
    // 後台ERP用：動態計算庫存
    public Long getCurrentCalculatedStock(Integer productId) {
        // 移除初始庫存的計算，只計算進貨-銷售
        // 這樣確保只有真正進貨的商品才會有庫存
        
        Integer totalIn = purchaseDetailRepository.sumQuantityBySupplierProduct_Product_Id(productId);
        Long totalOut = salesOrderDetailRepository.sumQuantityByProductId(productId);
             
        // 詳細查詢銷售記錄
        try {
            java.util.List<com.example.demo.model.SalesOrderDetail> salesDetails = 
                salesOrderDetailRepository.findByProductId(productId);
            
            for (com.example.demo.model.SalesOrderDetail detail : salesDetails) {
          
            }
        } catch (Exception e) {
            
        }
        
        // 修正計算公式：進貨 - 銷售（不再使用初始庫存）
        Long result = (totalIn != null ? totalIn : 0L) - (totalOut != null ? totalOut : 0L);       
        
        return Math.max(0L, result); // 確保庫存不會是負數
    }
    
    public long calculateStock(Integer integer) {
    	Integer totalIn = purchaseDetailRepository.sumQuantityBySupplierProduct_Product_Id(integer);
        return totalIn != null ? totalIn : 0L;
    }

    public void save(Product product, MultipartFile[] imageFiles, Integer type) throws IOException {
    	ProductType pt = ptRepository.findById(type).orElse(null);
    	product.setProductType(pt);
        Product savedProduct = repository.save(product);

        List<ProductImage> imageList = new ArrayList<>();

        if (imageFiles != null) {
            for (MultipartFile file : imageFiles) {
                if (!file.isEmpty()) {
                    ProductImage img = new ProductImage();
                    img.setProduct(savedProduct);
                    img.setImagePath(file.getOriginalFilename());
                    img.setImageData(file.getBytes());
                    imageList.add(img);
                }
            }
        }

        imageRepository.saveAll(imageList);
    }
    
    public List<Product> findPublishedProducts() {
        return repository.findByPublishedTrue();
    }
    
    public List<Product> findActive() {
        return repository.findByDeletedFalse();
    }

    public List<Product> findAll() {
        return repository.findAll(); // 不過濾
    }

    // 防超賣庫存扣除方法（使用計算庫存）
    // @Transactional
    public void deductStock(Integer productId, Long quantity) {
        Product product = repository.findById(productId).orElse(null);
        if (product == null) {
            throw new RuntimeException("商品不存在或已下架");
        }
        
        // 檢查商品是否已發布且未刪除
        if (product.getDeleted() != null && product.getDeleted()) {
            throw new RuntimeException("商品已下架，無法購買");
        }
        
        if (product.getPublished() == null || !product.getPublished()) {
            throw new RuntimeException("商品未發布，無法購買");
        }
        
        Long currentStock = getCurrentCalculatedStock(productId);
        if (currentStock < quantity) {
            throw new RuntimeException("商品庫存不足，請稍後再試或選擇其他商品");
        }
        
        // 注意：這裡不再直接修改Product的stock欄位
        // 庫存扣減是通過創建SalesOrderDetail來實現的
        // 這個方法主要用於檢查庫存是否足夠
        

    }
    
    // 更新商品的計算庫存（設定到@Transient字段）
    public void updateCalculatedStock(Integer productId) {
        Long calculatedStock = getCurrentCalculatedStock(productId);
        Product product = repository.findById(productId).orElse(null);
        if (product != null) {
            product.setCalculatedStock(calculatedStock);
        }
    }
    
    // 批量更新所有商品的計算庫存
    public void updateAllCalculatedStock() {
        List<Product> allProducts = repository.findAll();
        for (Product product : allProducts) {
            updateCalculatedStock(product.getId());
        }
    }

    // 新增：獲取商品總銷售數量（排除已取消的訂單）
    public Long getTotalSalesQuantity(Integer productId) {
        try {
            // 使用現有的方法，排除已取消的訂單
            Long totalSales = salesOrderDetailRepository.sumQuantityByProductIdExcludeCancelled(productId);
            return totalSales != null ? totalSales : 0L;
        } catch (Exception e) {
            System.err.println("計算商品銷售數量時發生錯誤 - 商品ID: " + productId + ", 錯誤: " + e.getMessage());
            return 0L;
        }
    }

    // 檢查商品是否可以上架（必須有進貨記錄）
    public boolean canProductBePublished(Integer productId) {
        // 只檢查是否有進貨記錄，確保業務流程正確
        Integer totalPurchased = purchaseDetailRepository.sumQuantityBySupplierProduct_Product_Id(productId);
        
        // 必須要有進貨記錄才能上架
        boolean hasPurchaseRecord = totalPurchased != null && totalPurchased > 0;
        
        System.out.println("=== 商品上架檢查 ===");
        System.out.println("商品ID: " + productId);
        System.out.println("進貨總數: " + (totalPurchased != null ? totalPurchased : 0));
        System.out.println("可以上架: " + hasPurchaseRecord);
        
        return hasPurchaseRecord;
    }
    
    // 獲取商品狀態詳情（用於除錯）
    public Map<String, Object> getProductStatus(Integer productId) {
        Product product = repository.findById(productId).orElse(null);
        if (product == null) {
            return Map.of("error", "商品不存在");
        }
        
        Integer totalPurchased = purchaseDetailRepository.sumQuantityBySupplierProduct_Product_Id(productId);
        Long totalSold = salesOrderDetailRepository.sumQuantityByProductId(productId);
        Long calculatedStock = getCurrentCalculatedStock(productId);
        
        Map<String, Object> status = new HashMap<>();
        status.put("productId", productId);
        status.put("productName", product.getName());
        status.put("totalPurchased", totalPurchased != null ? totalPurchased : 0);
        status.put("totalSold", totalSold != null ? totalSold : 0);
        status.put("calculatedStock", calculatedStock);
        status.put("isPublished", product.getPublished());
        status.put("canBePublished", canProductBePublished(productId));
        status.put("hasNegativeStock", calculatedStock != null && calculatedStock < 0);
        
        return status;
    }

}

