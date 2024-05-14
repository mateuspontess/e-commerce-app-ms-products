package br.com.ecommerce.products.model.manufacturer;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class ManufacturerDTO{
	
	@NotBlank
	private final String name;
}