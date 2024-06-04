package br.com.ecommerce.products.model.product;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProductSpecDTO {
	
	@NotBlank
	private String attribute;
	@NotBlank
	private String value;
	
	
	public ProductSpecDTO(ProductSpec p) {
		this.attribute = p.getAttribute();
		this.value = p.getValue();
	}
}