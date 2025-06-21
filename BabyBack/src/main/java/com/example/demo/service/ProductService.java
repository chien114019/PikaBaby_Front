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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    public List<Product> getAllProductsWithStock() {
    	List<Product> products = repository.findAll();

        for (Product p : products) {
            Integer totalIn = purchaseDetailRepository.sumQuantityBySupplierProduct_Product_Id(p.getId());
            Long totalOut = salesOrderDetailRepository.sumQuantityBySupplierProductProductId(p.getId());
            Long stock = (totalIn != null ? totalIn : 0L) - (totalOut != null ? totalOut : 0L);
            p.setStock(stock);
        }

        return products;
    }

    public Long getCurrentStock(Integer productId) {
        Integer totalIn = purchaseDetailRepository.sumQuantityBySupplierProduct_Product_Id(productId);
        Long totalOut = salesOrderDetailRepository.sumQuantityBySupplierProductProductId(productId);
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

    
    

}

