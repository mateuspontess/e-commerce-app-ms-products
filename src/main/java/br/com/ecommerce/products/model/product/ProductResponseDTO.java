package br.com.ecommerce.products.model.product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.com.ecommerce.products.model.manufacturer.ManufacturerResponseDTO;
import br.com.ecommerce.products.model.stock.StockResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter 
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {
	
	private Long id;
    private String name;
	private String description;
	private BigDecimal price;
	private Category category;
    private StockResponseDTO stock; // cria junto com o objeto Product
    private ManufacturerResponseDTO manufacturer; // espera-se que já esteja criado no banco de dados
    private List<ProductSpecDTO> specs = new ArrayList<>(); // cria junto com o objeto Product
     
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