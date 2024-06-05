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
public class StockWriteOffDTO {

	@NotNull
	private Long productId;
	@NotNull
	private Integer unit;
}