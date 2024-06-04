package br.com.ecommerce.products.model.manufacturer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ManufacturerResponseDTO{
	
	private Long id;
	private String name;
	
	
	public ManufacturerResponseDTO(Manufacturer manufacturer) {
		this.id = manufacturer.getId();
		this.name = manufacturer.getName();
	}
}