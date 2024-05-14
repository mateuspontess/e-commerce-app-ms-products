package br.com.ecommerce.products.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.ecommerce.products.model.manufacturer.Manufacturer;
import br.com.ecommerce.products.model.product.Category;
import br.com.ecommerce.products.model.product.Product;
import br.com.ecommerce.products.model.product.ProductDTO;
import br.com.ecommerce.products.model.product.ProductResponseDTO;
import br.com.ecommerce.products.model.product.ProductSpec;
import br.com.ecommerce.products.model.stock.Stock;
import br.com.ecommerce.products.repository.ManufacturerRepository;
import br.com.ecommerce.products.repository.ProductRepository;
import br.com.ecommerce.products.repository.StockRepository;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private StockRepository stockRepository;
	@Autowired
	private ManufacturerRepository mRepository;
	@Autowired
	private ModelMapper mapper;


	public ProductResponseDTO getProduct(Long id) {
		Product product = productRepository
				.findById(id)
				.orElseThrow(EntityNotFoundException::new);
		
		return mapper.map(product, ProductResponseDTO.class);
	}
	
	public Page<ProductResponseDTO> getAllProductWithParams(
			Pageable pageable, 
			String name, 
			Category category, 
			BigDecimal minPrice, 
			BigDecimal maxPrice,
			String manufacturer) {
		
		return productRepository
				.findAllByParams(pageable, name, category, minPrice, maxPrice, manufacturer)
				.map(p -> new ProductResponseDTO(p));
	}
	
	public Page<ProductResponseDTO> getAllBySpecs(Pageable pageable, List<Map<String, String>> map) {
		return productRepository
				.findProductsBySpecs(pageable, map)
				.map(p -> new ProductResponseDTO(p));
		
	}
	
	
	public void createProduct(ProductDTO dto) {
		Product produto = mapper.map(dto, Product.class);
		
		this.setManufacturer(produto);
		this.createStock(produto);
		this.createSpec(produto);
		
		productRepository.save(produto);
	}
	private void setManufacturer(Product produto) {
		Manufacturer mf = mRepository
				.findByName(produto.getManufacturer().getName().toUpperCase())
				.orElseThrow(EntityNotFoundException::new);
		
		produto.setManufacturer(mf);
	}
	private void createStock(Product product) {
		Stock stock = product.getStock();
		stock.setProduct(product);
		
		stockRepository.save(stock);
	}
	private void createSpec(Product product) {
		List<ProductSpec> specs = product.getSpecs();
		specs.forEach(spec -> spec.setProduct(product));
	}
}