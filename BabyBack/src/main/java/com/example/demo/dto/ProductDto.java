package com.example.demo.dto;

import java.math.BigDecimal;

public record ProductDto(Integer id, String name, String imageUrl, String description, BigDecimal price) {}
