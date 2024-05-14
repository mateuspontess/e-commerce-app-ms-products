package br.com.ecommerce.products.model.stock;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class StockResponseDTO {
	
	private final Long id;
	private final Long productId;
	private final String name;
	private final Integer unit;
	
	public StockResponseDTO(Stock data) {
		this.id = data.getId();
		this.productId = data.getProduct().getId();
		this.name = data.getProduct().getName();
		this.unit = data.getUnit();
	}
}