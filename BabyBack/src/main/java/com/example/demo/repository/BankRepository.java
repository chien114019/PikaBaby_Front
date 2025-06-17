package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.BankNo;

public interface BankRepository extends JpaRepository<BankNo, Integer> {

}
