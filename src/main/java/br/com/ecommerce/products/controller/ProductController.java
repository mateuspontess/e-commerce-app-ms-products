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
import org.springframework.web.util.UriComponentsBuilder;

import br.com.ecommerce.products.model.product.Category;
import br.com.ecommerce.products.model.product.Product;
import br.com.ecommerce.products.model.product.ProductDTO;
import br.com.ecommerce.products.model.product.ProductIdAndUnitsDTO;
import br.com.ecommerce.products.model.product.ProductPriceDTO;
import br.com.ecommerce.products.model.product.ProductResponseDTO;
import br.com.ecommerce.products.model.product.ProductUpdateDTO;
import br.com.ecommerce.products.model.product.ProductUpdateResponseDTO;
import br.com.ecommerce.products.model.product.StockDTO;
import br.com.ecommerce.products.model.product.StockResponseDTO;
import br.com.ecommerce.products.service.ProductService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

@RestController
@RequestMapping("/products")
public class ProductController {

	@Autowired
	private ProductService service;
	
	
	@GetMapping("/{productId}")
	public ResponseEntity<ProductResponseDTO> getProduct(@PathVariable Long productId){
		return ResponseEntity.ok(service.getProduct(productId));
	}
	
	@GetMapping
	public ResponseEntity<?> getAllProductsByParams(
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
	public ResponseEntity<ProductResponseDTO> createProduct(@RequestBody @Valid ProductDTO dto, UriComponentsBuilder uriBuilder) {
		ProductResponseDTO responseBody = service.createProduct(dto);
		var uri = uriBuilder.path("/products/{productId}").buildAndExpand(responseBody.getId()).toUri();
		return ResponseEntity.created(uri).body(responseBody);
	}
	
	@PostMapping("/stocks")
	public ResponseEntity<List<StockResponseDTO>> verifyStocks(
		@RequestBody @Valid @NotEmpty List<ProductIdAndUnitsDTO> dto
		) {
		List<Product> outOfStock = service.verifyProductsStocks(dto);
		
		List<StockResponseDTO> responseBody = null;
		if(outOfStock.isEmpty()) {
			return ResponseEntity.ok(responseBody);
		}
		
		responseBody = outOfStock.stream()
			.map(StockResponseDTO::new)
			.toList();
		
		return ResponseEntity.status(HttpStatus.MULTI_STATUS).body(responseBody);
	}

	@PostMapping("/specs")
	public ResponseEntity<Page<ProductResponseDTO>> readAllBySpecs(
			@PageableDefault(size = 10) Pageable pageable,
			@RequestBody @Valid @NotEmpty List<Map<String, String>> map
			) {
		return ResponseEntity.ok(service.getAllBySpecs(pageable, map));
	}
	
	@PostMapping("/prices")
	public ResponseEntity<List<ProductPriceDTO>> getPrices(@RequestBody @Valid @NotEmpty List<Long> productsIds){
		return ResponseEntity.ok(service.getAllProductsByListOfIds(productsIds).stream()
				.map(p -> new ProductPriceDTO(p.getId(), p.getPrice()))
				.toList());
	}
	
	
	@PutMapping("/{productId}")
	@Transactional
	public ResponseEntity<ProductUpdateResponseDTO> updateProduct(@PathVariable Long productId, @RequestBody ProductUpdateDTO dto) {
		return ResponseEntity.ok().body(service.updateProductData(productId, dto));
	}
	
	@PutMapping("/{productId}/stocks")
	@Transactional
	public ResponseEntity<StockResponseDTO> updateStock(@PathVariable Long productId, @RequestBody @Valid StockDTO dto) {
		return ResponseEntity.ok().body(service.updateStockByProductId(productId, dto));
	}
}