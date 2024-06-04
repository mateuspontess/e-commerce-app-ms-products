package br.com.ecommerce.products.model.stock;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StockDTO {

	@NotNull
	private Integer unit;
	
	
	public StockDTO(StockDTO dto) {
		this.unit = dto.getUnit();
	}
}