package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Consignment;
import com.example.demo.repository.ConsignmentRepository;

@Service
public class ConsignmentService {

	@Autowired
	private ConsignmentRepository repository;

	public List<Consignment> getAll() {
		return repository.findAll();
	}
	
	public Consignment getById(String id) {
		Consignment consignment = repository.findById(Integer.parseInt(id)).orElse(null);
		return consignment;
	}
}
