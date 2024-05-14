package br.com.ecommerce.products.model.product;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class ProductIdAndUnitsDTO {
	
	@NotNull
	private final Long id;
	@NotNull
	private final Integer unit;
}