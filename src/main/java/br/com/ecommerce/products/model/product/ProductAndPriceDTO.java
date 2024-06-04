package br.com.ecommerce.products.model.product;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProductAndPriceDTO {

	private Long id;
	private BigDecimal price;
}