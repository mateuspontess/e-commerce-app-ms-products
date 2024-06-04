package br.com.ecommerce.products.model.product;

import java.math.BigDecimal;

import br.com.ecommerce.products.model.manufacturer.ManufacturerDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateDTO {

	private String name;
	private String description;
	private BigDecimal price;
	private String category;
	private ManufacturerDTO manufacturer;
}