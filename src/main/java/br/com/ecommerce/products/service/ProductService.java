package br.com.ecommerce.products.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.ecommerce.products.model.manufacturer.Manufacturer;
import br.com.ecommerce.products.model.product.Product;
import br.com.ecommerce.products.model.product.ProductDTO;
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