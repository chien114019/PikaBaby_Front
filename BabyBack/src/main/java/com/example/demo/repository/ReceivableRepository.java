package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Receivable;

public interface ReceivableRepository extends JpaRepository<Receivable, Long> {

}
