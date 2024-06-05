package br.com.ecommerce.products.model.product;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
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