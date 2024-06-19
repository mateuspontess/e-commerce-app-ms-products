package br.com.ecommerce.products;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import br.com.ecommerce.products.config.Configs;

@SpringBootTest
@ContextConfiguration(classes = Configs.class)
class ProductsApplicationTests {
	@Test
	void contextLoads() {
	}
}