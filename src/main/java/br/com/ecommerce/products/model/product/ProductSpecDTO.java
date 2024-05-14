package br.com.ecommerce.products.model.product;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class ProductSpecDTO {
	
	@NotBlank
	private final String attribute;
	@NotBlank
	private final String value;
	
	
	public ProductSpecDTO(ProductSpec p) {
		this.attribute = p.getAttribute();
		this.value = p.getValue();
	}
}