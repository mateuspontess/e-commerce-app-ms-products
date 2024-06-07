package br.com.ecommerce.products.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

import br.com.ecommerce.products.model.product.Category;

public class StringToCategoryConverter implements Converter<String, Category>{

	@Override
	public Category convert(@NonNull String source) {
		try {
			return Category.fromString(source);
		
		} catch (IllegalArgumentException e) {
			return null;
		}
	}
}