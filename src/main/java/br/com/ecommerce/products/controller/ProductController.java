package br.com.ecommerce.products.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.ecommerce.products.model.product.Category;
import br.com.ecommerce.products.model.product.Product;
import br.com.ecommerce.products.model.product.ProductAndPriceDTO;
import br.com.ecommerce.products.model.product.ProductDTO;
import br.com.ecommerce.products.model.product.ProductIdAndUnitsDTO;
import br.com.ecommerce.products.model.product.ProductResponseDTO;
import br.com.ecommerce.products.model.product.ProductUpdateDTO;
import br.com.ecommerce.products.model.stock.StockResponseDTO;
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
	
	
	@PostMapping("/specs")
	public ResponseEntity<?> readAllBySpecs(
			@PageableDefault(size = 10) Pageable pageable,
			@RequestBody List<Map<String, String>> map
			) {
		
		Page<ProductResponseDTO> dtos = service.getAllBySpecs(pageable, map);
		return ResponseEntity.ok(dtos);
	}

	@PostMapping("/stocks")
	public ResponseEntity<?> verifyStocks(@RequestBody @Valid List<ProductIdAndUnitsDTO> dto) {
		List<Product> outOfStock = service.verifyStocks(dto);
		
		List<StockResponseDTO> responseBody = null;
		if(outOfStock.isEmpty()) {
			return ResponseEntity.ok(responseBody);
		}
		responseBody = outOfStock.stream()
				.map(p -> new StockResponseDTO(p.getStock()))
				.toList();
		
		return ResponseEntity.status(HttpStatus.MULTI_STATUS).body(responseBody);
	}
	
	@PostMapping("/prices")
	public ResponseEntity<List<ProductAndPriceDTO>> getPrices(@RequestBody @Valid List<ProductIdAndUnitsDTO> productsIds){
		return ResponseEntity.ok(service.getAllProductsByListOfIds(productsIds).stream()
				.map(p -> new ProductAndPriceDTO(p.getId(), p.getPrice()))
				.toList());
	}
	
	
	@PutMapping("/{productId}")
	@Transactional
	public ResponseEntity<?> updateProduct(@PathVariable Long productId, @RequestBody ProductUpdateDTO dto) {
		service.updateProduct(productId, dto);
		return ResponseEntity.ok().build();
	}
	
	
	@PostMapping
	@Transactional
	public ResponseEntity<?> createProdut(@RequestBody @Valid ProductDTO dto) {
		service.createProduct(dto);
		return ResponseEntity.ok().build();
	}
}