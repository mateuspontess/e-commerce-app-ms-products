package br.com.ecommerce.products.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.ecommerce.products.model.product.Category;
import br.com.ecommerce.products.model.product.ProductDTO;
import br.com.ecommerce.products.model.product.ProductResponseDTO;
import br.com.ecommerce.products.service.ProductService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/products")
public class ProductController {

	@Autowired
	private ProductService service;
	
	
	
	@GetMapping("/{productId}")
	public ResponseEntity<ProductResponseDTO> read(@PathVariable Long productId){
		return ResponseEntity.ok(service.getProduct(productId));
	}
	
	@GetMapping
	public ResponseEntity<?> readAllWithParams(
			@PageableDefault(size = 10) Pageable pageable,
	        @RequestParam(required = false) String name,
	        @RequestParam(required = false) Category category,
	        @RequestParam(required = false) BigDecimal minPrice,
	        @RequestParam(required = false) BigDecimal maxPrice,
	        @RequestParam(required = false) String manufacturer
	        ){
		
		return ResponseEntity.ok(service
				.getAllProductWithParams(pageable, name, category, minPrice, maxPrice, manufacturer));
	}
	
	@PostMapping
	@Transactional
	public ResponseEntity<?> createProdut(@RequestBody @Valid ProductDTO dto) {
		service.createProduct(dto);
		return ResponseEntity.ok().build();
	}
}