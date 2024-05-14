package br.com.ecommerce.products.model.stock;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class StockDTO {

	@NotNull
	private final Integer unit;
	
	
	public StockDTO(StockDTO dto) {
		this.unit = dto.getUnit();
	}
}