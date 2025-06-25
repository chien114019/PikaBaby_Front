package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Customer;
import com.example.demo.model.CustomerFavorites;
import com.example.demo.model.Product;
import com.example.demo.repository.CustomerFavoritesRepository;

@Service
public class CustomerFavoritesService {
	 @Autowired
	    private CustomerFavoritesRepository favoritesRepo;
	 
	// 新增收藏（如果還沒收藏）
	    public void addFavorite(Customer customer, Product product) {
	        boolean alreadyExists = favoritesRepo.findByCustomerAndProduct(customer, product).isPresent();
	        if (!alreadyExists) {
	            CustomerFavorites favorite = new CustomerFavorites();
	            favorite.setCustomer(customer);
	            favorite.setProduct(product);
	            favoritesRepo.save(favorite);
	        }
	    }

	    // 移除收藏
	    @Transactional
	    public void removeFavorite(Customer customer, Product product) {
	        favoritesRepo.deleteByCustomerAndProduct(customer, product);
	    }

	    // 查詢會員所有收藏
	    public List<CustomerFavorites> getFavoritesByCustomer(Customer customer) {
	        return favoritesRepo.findByCustomer(customer);
	    }

	    // 檢查是否已收藏
	    public boolean isFavorite(Customer customer, Product product) {
	        return favoritesRepo.findByCustomerAndProduct(customer, product).isPresent();
	    }
	}

