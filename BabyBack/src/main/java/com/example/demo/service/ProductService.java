package com.example.demo.service;

import com.example.demo.model.Product;
import com.example.demo.model.ProductImage;
import com.example.demo.model.PurchaseOrderDetail;
import com.example.demo.repository.ProductImageRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.PurchaseOrderDetailRepository;
import com.example.demo.repository.SalesOrderDetailRepository;
import com.example.demo.repository.SupplierProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
        return repository.findAll();
    }

    public List<Product> searchByName(String keyword) {
        return repository.findByNameContainingIgnoreCase(keyword);
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
            Integer totalIn = purchaseDetailRepository.sumQuantityBySupplierProduct_Product_Id(p.getId());
            Long totalOut = salesOrderDetailRepository.sumQuantityByProductId(p.getId());
            Long calculatedStock = (totalIn != null ? totalIn : 0L) - (totalOut != null ? totalOut : 0L);
            p.setCalculatedStock(calculatedStock);
        }
        
        return products;
    }

    // 前台電商用：快速庫存查詢
    public Long getCurrentStock(Integer productId) {
        Product product = repository.findById(productId).orElse(null);
        return product != null ? (product.getStock() != null ? product.getStock() : 0L) : 0L;
    }
    
    // 後台ERP用：動態計算庫存
    public Long getCurrentCalculatedStock(Integer productId) {
        Integer totalIn = purchaseDetailRepository.sumQuantityBySupplierProduct_Product_Id(productId);
        Long totalOut = salesOrderDetailRepository.sumQuantityByProductId(productId);
        return (totalIn != null ? totalIn : 0L) - (totalOut != null ? totalOut : 0L);
    }
    
    public long calculateStock(Integer integer) {
    	Integer totalIn = purchaseDetailRepository.sumQuantityBySupplierProduct_Product_Id(integer);
        return totalIn != null ? totalIn : 0L;
    }

    public void save(Product product, MultipartFile[] imageFiles) throws IOException {
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

    // 防超賣庫存扣除方法（暫時移除事務控制）
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
        
        Long currentStock = product.getStock() != null ? product.getStock() : 0L;
        if (currentStock < quantity) {
            throw new RuntimeException("商品庫存不足，請稍後再試或選擇其他商品");
        }
        
        // 原子操作：扣除庫存
        product.setStock(currentStock - quantity);
        repository.save(product);
        
        // 記錄庫存扣除日誌（可選）
        System.out.println("商品ID: " + productId + " 扣除庫存: " + quantity + " 剩餘: " + (currentStock - quantity));
    }
    
    // 庫存同步：將ERP動態計算的庫存同步到電商stock欄位
    public void syncStockFromCalculated(Integer productId) {
        Long calculatedStock = getCurrentCalculatedStock(productId);
        Product product = repository.findById(productId).orElse(null);
        if (product != null) {
            product.setStock(calculatedStock);
            repository.save(product);
        }
    }
    
    // 批量庫存同步
    public void syncAllStockFromCalculated() {
        List<Product> products = repository.findAll();
        for (Product product : products) {
            Long calculatedStock = getCurrentCalculatedStock(product.getId());
            product.setStock(calculatedStock);
        }
        repository.saveAll(products);
    }
    
    // 庫存差異檢查
    public boolean hasStockDiscrepancy(Integer productId) {
        Long currentStock = getCurrentStock(productId);
        Long calculatedStock = getCurrentCalculatedStock(productId);
        return !currentStock.equals(calculatedStock);
    }

}

