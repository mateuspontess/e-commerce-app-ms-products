package br.com.ecommerce.products.model.product;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class ProductAndPriceDTO {

	private final Long id;
	private final BigDecimal price;
}