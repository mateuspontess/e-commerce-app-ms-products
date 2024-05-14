package br.com.ecommerce.products.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ecommerce.products.model.product.ProductDTO;
import br.com.ecommerce.products.service.ProductService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/products")
public class ProductController {

	@Autowired
	private ProductService service;
	
	
	@PostMapping
	@Transactional
	public ResponseEntity<?> createProdut(@RequestBody @Valid ProductDTO dto) {
		service.createProduct(dto);
		return ResponseEntity.ok().build();
	}
}