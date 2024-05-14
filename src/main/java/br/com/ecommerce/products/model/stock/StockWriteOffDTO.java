package br.com.ecommerce.products.model.stock;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class StockWriteOffDTO {

	private final Long productId;
	private final Integer unit;
}