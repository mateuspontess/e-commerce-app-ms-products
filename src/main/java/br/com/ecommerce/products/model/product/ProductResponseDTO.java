package br.com.ecommerce.products.model.product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.com.ecommerce.products.model.manufacturer.ManufacturerResponseDTO;
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
    private Stock stock; // cria junto com o objeto Product
    private ManufacturerResponseDTO manufacturer; // espera-se que já esteja criado no banco de dados
    private List<ProductSpecDTO> specs = new ArrayList<>(); // cria junto com o objeto Product
     
    public ProductResponseDTO(Product product) {
    	this.id = product.getId();
    	this.name = product.getName();
    	this.description = product.getDescription();
    	this.price = product.getPrice();
    	this.stock = product.getStock();
    	this.category = product.getCategory();
    	this.manufacturer = new ManufacturerResponseDTO(product.getManufacturer());
    	this.specs.addAll(product.getSpecs().stream()
			.map(s -> new ProductSpecDTO(s)).toList());
    }
}