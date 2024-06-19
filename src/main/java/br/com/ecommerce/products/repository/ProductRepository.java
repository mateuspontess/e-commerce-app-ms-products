package br.com.ecommerce.products.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
    
    @Query("""
        SELECT DISTINCT p FROM Product p WHERE p.id 
        IN 
        (SELECT DISTINCT ps.product.id FROM ProductSpec ps 
        WHERE 
        (ps.attribute = :#{#specs[0][attribute]} AND ps.value = :#{#specs[0][value]}))
        """)
     Page<Product> findProductsBySpecs(Pageable pageable, @Param("specs") List<Map<String, String>> specs);
}