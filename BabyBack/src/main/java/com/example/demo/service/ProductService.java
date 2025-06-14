package com.example.demo.service;

import com.example.demo.model.Product;
import com.example.demo.model.ProductImage;
import com.example.demo.repository.ProductImageRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.PurchaseOrderDetailRepository;
import com.example.demo.repository.SalesOrderDetailRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository repository;
    
    //0611喬新增
    @Autowired
    private PurchaseOrderDetailRepository purchaseDetailRepository; 
    @Autowired
    private SalesOrderDetailRepository salesOrderDetailRepository;
    
    //0614喬新增
    @Autowired
    private ProductImageRepository imageRepository;
    

    public List<Product> listAll() {
        return repository.findAll();
    }

    public Product getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void save(Product product) {
        repository.save(product);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
    public List<Product> getAllProducts() {
        return repository.findAll();
    }
    public List<Product> searchByName(String keyword) {
        return repository.findByNameContainingIgnoreCase(keyword);
    }
 // 修正後：計算「進貨 - 出貨」作為目前庫存
    public List<Product> getAllProductsWithStock() {
        List<Product> products = repository.findAll();
        for (Product p : products) {
            Long totalIn = purchaseDetailRepository.sumQuantityByProductId(p.getId());
            Long totalOut = salesOrderDetailRepository.sumQuantityByProductId(p.getId());
            Long stock = (totalIn != null ? totalIn : 0L) - (totalOut != null ? totalOut : 0L);
            p.setStock(stock); // 將計算結果暫存進 product.stock 提供畫面顯示
        }
        return products;
    }
    public Long getCurrentStock(Long productId) {
        Long totalIn = purchaseDetailRepository.sumQuantityByProductId(productId);
        Long totalOut = salesOrderDetailRepository.sumQuantityByProductId(productId);
        return (totalIn != null ? totalIn : 0L) - (totalOut != null ? totalOut : 0L);
    }
    
    //0614喬新增商品上團圖片邏輯
 // 新增商品 + 多張圖片
    public void save(Product product, MultipartFile[] imageFiles) throws IOException {
        // 儲存商品基本資料
        Product savedProduct = repository.save(product);

        List<ProductImage> imageList = new ArrayList<>();

        if (imageFiles != null) {
            for (MultipartFile file : imageFiles) {
                if (!file.isEmpty()) {
                    ProductImage img = new ProductImage();
                    img.setProduct(savedProduct); // 關聯商品
                    img.setImagePath(file.getOriginalFilename()); // 可選：存檔名
                    img.setImageData(file.getBytes()); //  存入 byte[]
                    imageList.add(img);
                }
            }
        }


        // 批次儲存圖片
        imageRepository.saveAll(imageList);
    }

    
}
