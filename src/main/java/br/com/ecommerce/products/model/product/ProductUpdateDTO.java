package br.com.ecommerce.products.model.product;

import java.math.BigDecimal;

import br.com.ecommerce.products.model.manufacturer.ManufacturerDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class ProductUpdateDTO {

	private final String name;
	private final String description;
	private final BigDecimal price;
	private final String category;
	private final ManufacturerDTO manufacturer;
}