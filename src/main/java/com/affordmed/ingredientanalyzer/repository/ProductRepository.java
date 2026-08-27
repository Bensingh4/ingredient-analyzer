package com.affordmed.ingredientanalyzer.repository;

import com.affordmed.ingredientanalyzer.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}