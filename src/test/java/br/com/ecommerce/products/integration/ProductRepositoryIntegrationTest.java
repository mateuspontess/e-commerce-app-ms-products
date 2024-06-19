package br.com.ecommerce.products.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.ecommerce.products.configs.MySQLTestContainerConfig;
import br.com.ecommerce.products.model.manufacturer.Manufacturer;
import br.com.ecommerce.products.model.product.Category;
import br.com.ecommerce.products.model.product.Product;
import br.com.ecommerce.products.model.product.ProductSpec;
import br.com.ecommerce.products.model.product.Stock;
import br.com.ecommerce.products.repository.ManufacturerRepository;
import br.com.ecommerce.products.repository.ProductRepository;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(MySQLTestContainerConfig.class)
class ProductRepositoryIntegrationTest {

    @Autowired
    private ProductRepository repository;

    private final Pageable pageable = Pageable.unpaged();

    @BeforeAll
    static void setupDatabase(@Autowired ProductRepository repository, @Autowired ManufacturerRepository manufacturerRepository) {
        List<Manufacturer> manufacturersPersisted = manufacturerRepository.saveAll(
            List.of(new Manufacturer("AMD"), new Manufacturer("INTEL"))
        );

        createAndSaveProduct(repository, manufacturersPersisted.get(0), "aaa", "ddd", BigDecimal.valueOf(1000), Category.CPU, 1000, "cores", "12");
        createAndSaveProduct(repository, manufacturersPersisted.get(0), "bbb", "ddd", BigDecimal.valueOf(100), Category.CPU, 100, "cores", "8");
        createAndSaveProduct(repository, manufacturersPersisted.get(1), "ccc", "ddd", BigDecimal.TEN, Category.GPU, 10, "memory size", "8GB");
    }


    @Test
    @DisplayName("Integration - findAllByParams - Must return correct product data based on parameters")
    void findAllByParamsTest01() {
        var sizeResult = repository.findAllByParams(pageable, null, null, null, null, null).getContent().size();
        assertEquals(repository.count(), sizeResult, "The total number of products should match the persisted products");

        sizeResult = repository.findAllByParams(pageable, "aaa", Category.CPU, BigDecimal.valueOf(999), BigDecimal.valueOf(1001), "AMD").getContent().size();
        assertEquals(1, sizeResult, "Should return 1 product with name 'aaa', category 'CPU', price between 999 and 1001, and manufacturer 'AMD'");

        sizeResult = repository.findAllByParams(pageable, "aaa", null, null, null, null).getContent().size();
        assertEquals(1, sizeResult, "The number of products with name 'aaa' should match the count");

        sizeResult = repository.findAllByParams(pageable, null, Category.CPU, null, null, null).getContent().size();
        assertEquals(2, sizeResult, "The number of products with category 'CPU' should match the count");

        sizeResult = repository.findAllByParams(pageable, null, null, BigDecimal.valueOf(999), BigDecimal.valueOf(1001), null).getContent().size();
        assertEquals(1, sizeResult, "The number of products with price between 999 and 1001 should match the count");

        sizeResult = repository.findAllByParams(pageable, null, null, null, null, "AMD").getContent().size();
        assertEquals(2, sizeResult, "The number of products with manufacturer 'AMD' should match the count");
    }

    @Test
    @DisplayName("Integration - findProductsBySpecs - Must return correct products based on specs")
    void findProductsBySpecsTest01() {
        // arrange
        List<Map<String, String>> specs = List.of(Map.of("attribute", "cores", "value", "12"));

        // act
        Page<Product> result = repository.findProductsBySpecs(pageable, specs);

        // assert
        assertEquals(1, result.getContent().size(), "Filtering by specs 'cores:12' should return 1 product.");
        Product product = result.getContent().get(0);
        assertEquals("aaa", product.getName(), "The product name should be 'aaa'.");
        assertEquals("ddd", product.getDescription(), "The product description should be 'ddd'.");
        assertEquals(BigDecimal.valueOf(1000).doubleValue(), product.getPrice().doubleValue(), "The product price should be 1000.");
        assertEquals(Category.CPU, product.getCategory(), "The product category should be 'CPU'.");
        assertEquals(1000, product.getStock().getUnit(), "The product stock units should be 1000.");
        assertEquals("AMD", product.getManufacturer().getName(), "The product manufacturer should be 'AMD'.");
    }

    private static void createAndSaveProduct(ProductRepository repository, Manufacturer manufacturer, String name, String description, BigDecimal price, Category category, int stockUnits, String specAttribute, String specValue) {
        ProductSpec spec = new ProductSpec(specAttribute, specValue);
        Product product = Product.builder()
            .name(name)
            .description(description)
            .price(price)
            .category(category)
            .stock(new Stock(stockUnits))
            .manufacturer(manufacturer)
            .specs(List.of(spec))
            .build();
        spec.setProduct(product);
        repository.save(product);
    }
}