package br.com.ecommerce.products.model.manufacturer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class ManufacturerResponseDTO{
	
	private final Long id;
	private final String name;
	
	
	public ManufacturerResponseDTO(Manufacturer manufacturer) {
		this.id = manufacturer.getId();
		this.name = manufacturer.getName();
	}
}