package br.com.ecommerce.products.repository;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.ecommerce.products.model.product.Category;
import br.com.ecommerce.products.model.product.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{
	
    @Query("""
    		SELECT p FROM Product p WHERE
    		(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
    		AND (:category IS NULL OR p.category = :category)
    		AND (:minPrice IS NULL OR p.price >= :minPrice)
    		AND (:maxPrice IS NULL OR p.price <= :maxPrice)
    		AND (:manufacturerName IS NULL OR LOWER(p.manufacturer.name) = LOWER(:manufacturerName))
    		""")
    Page<Product> findAllByParams(
            Pageable pageable,
            String name,
            Category category,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String manufacturerName
    );
}