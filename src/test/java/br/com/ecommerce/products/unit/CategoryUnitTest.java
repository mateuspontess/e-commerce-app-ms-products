package br.com.ecommerce.products.unit;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import br.com.ecommerce.products.model.product.Category;

class CategoryUnitTest {

	@Test
	void fromStringTest01() {
		assertThrows(IllegalArgumentException.class, () -> Category.fromString("non-existent"));
	}

	@Test
	void fromStringTest02() {
		var values = Arrays.asList(Category.values());
		values.forEach(value -> {
			assertDoesNotThrow(() -> Category.fromString(value.toString()));
		});
	}
}