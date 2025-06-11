package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.model.Consignment;
import com.example.demo.model.ProductType;
import com.example.demo.model.Response;
import com.example.demo.repository.ConsignmentRepository;
import com.example.demo.repository.ProductTypeRepository;

@Service
public class ConsignmentService {

	@Autowired
	private ConsignmentRepository repository;
	
	@Autowired
	private ProductTypeRepository ptRepository;
	
	public List<Consignment> getAll() {
		return repository.findAll();
	}
	
	public Consignment getById(String id) {
		Consignment consignment = repository.findById(Integer.parseInt(id)).orElse(null);
		System.out.println(consignment);
		return consignment;
	}
	
//	getAllByTypeAndReviewAndDelivery
	public List<Consignment> getAllByProductTypeAndReviewAndDelivery(String type, String review, String delivery) {
		ProductType pType = ptRepository.findById(Integer.parseInt(type)).orElse(null);
		List<Consignment> consigments = null;
		
		if (pType != null && review != null && delivery != null) {
			consigments = repository.findAllByProductTypeAndReviewAndDelivery(pType, Integer.parseInt(review), 
					Integer.parseInt(delivery));
		}
		
		return consigments;
	}
	
//	getAllByTypeAndReview
	public List<Consignment> getAllByProductTypeAndReview(String type, String review) {
		ProductType pType = ptRepository.findById(Integer.parseInt(type)).orElse(null);
		List<Consignment> consigments = null;
		
		if (pType != null && review != null) {
			consigments = repository.findAllByProductTypeAndReview(pType, Integer.parseInt(review));
		}
		
		return consigments;
	}
	
//	getAllByTypeAndDelivery
	public List<Consignment> getAllByProductTypeAndDelivery(String type, String delivery) {
		ProductType pType = ptRepository.findById(Integer.parseInt(type)).orElse(null);
		List<Consignment> consigments = null;
		
		if (pType != null && delivery != null) {
			consigments = repository.findAllByProductTypeAndDelivery(pType, Integer.parseInt(delivery));
		}
		
		return consigments;
	}
	
//	getAllByProductType
	public List<Consignment> getAllByProductType(String type) {
		ProductType pType = ptRepository.findById(Integer.parseInt(type)).orElse(null);
		List<Consignment> consigments = null;
		
		if (pType != null) {
			consigments = repository.findAllByProductType(pType);
		}
		
		return consigments;
	}
	
//	getAllByReviewAndDelivery
	public List<Consignment> getAllByReviewAndDelivery(String review, String delivery) {
		List<Consignment> consigments = null;
		
		if (review != null && delivery != null) {
			consigments = repository.findAllByReviewAndDelivery(Integer.parseInt(review), Integer.parseInt(delivery));
		}
		
		return consigments;
	}
	
//	getAllByReview
	public List<Consignment> getAllByReview(String review) {
		List<Consignment> consigments = null;
		
		if (review != null) {
			consigments = repository.findAllByReview(Integer.parseInt(review));
		}
		
		return consigments;
	}
	
//	getAllByDelivery
	public List<Consignment> getAllByDelivery(String delivery) {
		List<Consignment> consigments = null;
		
		if (delivery != null) {
			consigments = repository.findAllByDelivery(Integer.parseInt(delivery));
		}
		
		return consigments;
	}
	
//	editConsigment
	public Response editConsignment(String id, String review, String price) {
		Response response = new Response();
		Consignment consign = repository.findById(Integer.parseInt(id)).orElse(null);
				
		if (consign != null) {
			consign.setReview(Integer.parseInt(review));
			if (price != "") {
				consign.setPrice(Integer.parseInt(price));
			} else {
				consign.setPrice(null);
			}
			repository.save(consign);
			
			response.setSuccess(true);
			response.setMesg("修改成功");
			
		} else {
			response.setSuccess(false);
			response.setMesg("查無紀錄");
			
		}
		
		return response;
	}
}
