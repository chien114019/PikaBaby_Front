package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.model.ProductImage;
import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {

	// 不用自己寫，JpaRepository 的現成方法
	//productImageRepository.saveAll(list);
	
    // 加上這個方法，能透過 product_id這個欄位 找出所有的 ProductImage圖片
    List<ProductImage> findByProductId(Integer integer);
}
