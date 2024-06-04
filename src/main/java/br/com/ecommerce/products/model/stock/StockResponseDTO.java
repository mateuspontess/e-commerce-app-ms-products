package br.com.ecommerce.products.model.stock;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StockResponseDTO {
	
	private Long id;
	private Long productId;
	private String name;
	private Integer unit;

	public StockResponseDTO(Stock data) {
		this.id = data.getId();
		this.productId = data.getProduct().getId();
		this.name = data.getProduct().getName();
		this.unit = data.getUnit();
	}
}