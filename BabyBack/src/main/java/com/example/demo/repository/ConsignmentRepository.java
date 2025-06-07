package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Consignment;

public interface ConsignmentRepository extends JpaRepository<Consignment, Integer>{

}
