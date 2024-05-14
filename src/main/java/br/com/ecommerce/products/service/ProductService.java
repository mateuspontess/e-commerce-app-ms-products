package br.com.ecommerce.products.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.ecommerce.products.model.manufacturer.Manufacturer;
import br.com.ecommerce.products.model.product.Category;
import br.com.ecommerce.products.model.product.Product;
import br.com.ecommerce.products.model.product.ProductDTO;
import br.com.ecommerce.products.model.product.ProductIdAndUnitsDTO;
import br.com.ecommerce.products.model.product.ProductResponseDTO;
import br.com.ecommerce.products.model.product.ProductSpec;
import br.com.ecommerce.products.model.product.ProductUpdateDTO;
import br.com.ecommerce.products.model.stock.Stock;
import br.com.ecommerce.products.model.stock.StockDTO;
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
	
	public List<Product> verifyStocks(List<ProductIdAndUnitsDTO> productsRequest) {
		List<Product> products = this.getAllProductsByListOfIds(productsRequest);
		if (products.isEmpty())
			throw new EntityNotFoundException("No entities were found");
		
		List<Product> outOfStocks = new ArrayList<Product>();
		Map<Long, ProductIdAndUnitsDTO> map = productsRequest.stream().collect(Collectors.toMap(p -> p.getId(), p -> p));
		
		products.forEach(p -> {
			if(p.getStock().getUnit() < map.get(p.getId()).getUnit()) {
				outOfStocks.add(p);
			}
		});
		
		return outOfStocks;
	}
	
	public List<Product> getAllProductsByListOfIds(List<ProductIdAndUnitsDTO> productsRequest) {
		List<Long> productsIds = productsRequest.stream().map(ProductIdAndUnitsDTO::getId).toList();
		return productRepository.findAllById(productsIds);
	}
	
	
	public void updateProduct(Long id, ProductUpdateDTO dto) {
		Product original = productRepository.getReferenceById(id);
		Product updateData = mapper.map(dto, Product.class);
		
		if (updateData.getManufacturer() != null) {
			Manufacturer previousManufacturer = mRepository
					.getReferenceById(original.getManufacturer().getId());
			previousManufacturer.getProducts().remove(original);	
			
			Manufacturer newManufacturer = mRepository
					.findByName(updateData.getManufacturer().getName())
					.orElseThrow(EntityNotFoundException::new);
			
			updateData.setManufacturer(newManufacturer);
			newManufacturer.getProducts().add(original);
			original.update(updateData);
			mRepository.save(newManufacturer);
			return;
		}
		original.update(updateData);
	}
	
	public void subtractUnitsInStock(Long id, StockDTO dto) {
		Product original = productRepository.getReferenceById(id);
		Stock stockUpdate = mapper.map(dto, Stock.class);
		
		original.getStock().updateStock(stockUpdate.getUnit());
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