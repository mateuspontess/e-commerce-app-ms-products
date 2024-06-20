package br.com.ecommerce.products;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import br.com.ecommerce.products.testcontainers.MySQLTestContainerConfig;
import br.com.ecommerce.products.testcontainers.RabbitMQTestContainerConfig;

@SpringBootTest
@TestPropertySource(properties = "classpath:application-test.properties")
@ContextConfiguration(classes = {MySQLTestContainerConfig.class, RabbitMQTestContainerConfig.class})
class ProductsApplicationTests {
	
	@Test
	void contextLoads() {
	}
}