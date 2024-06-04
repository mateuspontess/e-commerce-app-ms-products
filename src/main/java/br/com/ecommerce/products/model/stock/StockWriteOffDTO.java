package br.com.ecommerce.products.model.stock;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StockWriteOffDTO {

	private Long productId;
	private Integer unit;
}