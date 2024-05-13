package br.com.ecommerce.products.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.ecommerce.products.model.product.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{
}