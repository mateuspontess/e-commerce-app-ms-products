package br.com.ecommerce.products.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.ecommerce.products.model.stock.Stock;

public interface StockRepository extends JpaRepository<Stock, Long>{
}