package br.com.ecommerce.products.model.product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.com.ecommerce.products.model.manufacturer.ManufacturerResponseDTO;
import br.com.ecommerce.products.model.stock.StockResponseDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class ProductResponseDTO {
	
	private final Long id;
    private final String name;
	private final String description;
	private final BigDecimal price;
	private final Category category;
    private final StockResponseDTO stock; // cria junto com o objeto Product
    private final ManufacturerResponseDTO manufacturer; // espera-se que já esteja criado no banco de dados
    private final List<ProductSpecDTO> specs = new ArrayList<>(); // cria junto com o objeto Product
     
    public ProductResponseDTO(Product data) {
    	this.id = data.getId();
    	this.name = data.getName();
    	this.description = data.getDescription();
    	this.price = data.getPrice();
    	this.stock = new StockResponseDTO(data.getStock());
    	this.category = data.getCategory();
    	this.manufacturer = new ManufacturerResponseDTO(data.getManufacturer());
    	this.specs.addAll(data.getSpecs().stream()
    			.map(s -> new ProductSpecDTO(s)).toList());
    }
}