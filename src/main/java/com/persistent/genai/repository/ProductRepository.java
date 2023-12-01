package com.persistent.genai.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.persistent.genai.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

	List<Product> findByProductNameStartsWithIgnoreCase(String productName);
}
