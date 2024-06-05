package br.com.ecommerce.products.service;

import java.math.BigDecimal;
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
import br.com.ecommerce.products.model.product.Stock;
import br.com.ecommerce.products.model.product.StockDTO;
import br.com.ecommerce.products.model.product.StockWriteOffDTO;
import br.com.ecommerce.products.repository.ManufacturerRepository;
import br.com.ecommerce.products.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepository;
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
				.map(ProductResponseDTO::new);
	}
	
	public Page<ProductResponseDTO> getAllBySpecs(Pageable pageable, List<Map<String, String>> map) {
		return productRepository
				.findProductsBySpecs(pageable, map)
				.map(ProductResponseDTO::new);
		
	}
	
	public List<Product> verifyStocks(List<ProductIdAndUnitsDTO> productsRequest) {
		Map<Long, Integer> unitiesRequested = productsRequest.stream()
				.collect(Collectors.toMap(p -> p.getId(), p -> p.getUnit()));
		
		return this.getAllProductsByListOfIds(productsRequest.stream()
			.map(ProductIdAndUnitsDTO::getId)
			.toList())
			.stream()
				.filter(p -> p != null && p.getStock().getUnit() < unitiesRequested.get(p.getId())) 
				.collect(Collectors.toList());
	}
	
	public List<Product> getAllProductsByListOfIds(List<Long> productsIds) {
		return productRepository.findAllById(productsIds);
	}
	
	public void updateProduct(Long id, ProductUpdateDTO dto) {
		Product currentProduct = productRepository.getReferenceById(id);
		Product updateData = mapper.map(dto, Product.class);
		
		if (updateData.getManufacturer() != null) {
			Manufacturer previousManufacturer = mRepository
					.getReferenceById(currentProduct.getManufacturer().getId());
			previousManufacturer.removeProduct(currentProduct);	
			
			Manufacturer newManufacturer = mRepository
					.findByName(updateData.getManufacturer().getName())
					.orElseThrow(EntityNotFoundException::new);
			
			updateData.setManufacturer(newManufacturer);
			newManufacturer.addProduct(currentProduct);

			currentProduct.update(updateData);
			mRepository.save(newManufacturer);
			return;
		}
		currentProduct.update(updateData);
	}
	
	public void updateStockByProductId(Long productId, StockDTO dto) {
		Product original = productRepository.getReferenceById(productId);
		Stock stockUpdate = mapper.map(dto, Stock.class);
		
		original.updateStock(stockUpdate.getUnit());
	}
	public void updateStocks(List<StockWriteOffDTO> dto) {
		Map<Long, Integer> writeOffValueMap = dto.stream()
				.collect(Collectors.toMap(StockWriteOffDTO::getProductId, StockWriteOffDTO::getUnit));
		
		productRepository.findAllById(dto.stream()
				.map(StockWriteOffDTO::getProductId)
				.toList()
				)
				.forEach(p -> p.updateStock(writeOffValueMap.get(p.getId())));
	}
	
	
	public ProductResponseDTO createProduct(ProductDTO dto) {
		Product product = mapper.map(dto, Product.class);
		System.out.println("VALOR DO PRODUCT ASSIM QUE É CONVERTIDO:" + product);
		
		this.setManufacturer(product);
		this.createSpec(product);
		productRepository.save(product);
		
		return new ProductResponseDTO(product);
	}
	private void setManufacturer(Product product) {
		Manufacturer mf = mRepository
				.findByName(product.getManufacturer().getName().toUpperCase())
				.orElseThrow(EntityNotFoundException::new);
		
		product.setManufacturer(mf);
	}
	private void createSpec(Product product) {
		List<ProductSpec> specs = product.getSpecs();
		specs.forEach(spec -> spec.setProduct(product));
	}
}