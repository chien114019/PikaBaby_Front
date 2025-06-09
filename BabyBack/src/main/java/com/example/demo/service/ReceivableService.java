package com.example.demo.service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Receivable;
import com.example.demo.repository.ReceivableRepository;

@Service
public class ReceivableService {

    @Autowired
    private ReceivableRepository receivableRepository;

    public List<Receivable> search(String keyword, LocalDate startDate, LocalDate endDate) {
        if (keyword == null) keyword = "";

        if (startDate != null && endDate != null) {
            Timestamp start = Timestamp.valueOf(startDate.atStartOfDay());
            Timestamp end = Timestamp.valueOf(endDate.atTime(23, 59, 59));
            return receivableRepository.findByCustomerNameContainingIgnoreCaseAndCreatedAtBetween(keyword, start, end);
        } else if (startDate != null) {
            Timestamp start = Timestamp.valueOf(startDate.atStartOfDay());
            return receivableRepository.findByCustomerNameContainingIgnoreCaseAndCreatedAtAfter(keyword, start);
        } else if (endDate != null) {
            Timestamp end = Timestamp.valueOf(endDate.atTime(23, 59, 59));
            return receivableRepository.findByCustomerNameContainingIgnoreCaseAndCreatedAtBefore(keyword, end);
        } else {
            return receivableRepository.findByCustomerNameContainingIgnoreCase(keyword);
        }
    }
}
