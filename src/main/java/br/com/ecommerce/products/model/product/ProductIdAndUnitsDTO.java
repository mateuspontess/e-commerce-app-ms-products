package br.com.ecommerce.products.model.product;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProductIdAndUnitsDTO {
	
	@NotNull
	private Long id;
	@NotNull
	private Integer unit;
}