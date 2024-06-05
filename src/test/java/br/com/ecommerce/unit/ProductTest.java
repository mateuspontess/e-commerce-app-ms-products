package br.com.ecommerce.unit;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import br.com.ecommerce.products.model.manufacturer.Manufacturer;
import br.com.ecommerce.products.model.product.Category;
import br.com.ecommerce.products.model.product.Product;
import br.com.ecommerce.products.model.product.ProductSpec;
import br.com.ecommerce.products.model.product.Stock;

public class ProductTest {

    Product product = Product.builder()
        .id(1L)
        .name("Product-san")
        .description("Description-san")
        .price(BigDecimal.TEN)
        .category(Category.CPU)
        .stock(new Stock(200))
        .manufacturer(new Manufacturer("AMD"))
        .build();

    @Test
    void createProductTest01() {
        assertThrows(IllegalArgumentException.class, 
            () -> new Product("", "", BigDecimal.TEN, Category.CPU, new Stock(), new Manufacturer(), List.of(new ProductSpec())));
        assertThrows(IllegalArgumentException.class, 
            () -> new Product("Name", "Description", new BigDecimal("-1"), Category.CPU, new Stock(), new Manufacturer(), List.of(new ProductSpec())));
        assertThrows(IllegalArgumentException.class, 
            () -> new Product("Name", "Description", BigDecimal.TEN, Category.CPU, null, null, null));

        assertDoesNotThrow( 
            () -> new Product("Name", "Description", BigDecimal.TEN, Category.CPU, new Stock(), new Manufacturer(), List.of(new ProductSpec())));
    }
    @Test
    void createProductTest02() {
        Product product1 = new Product("Name", "Description", BigDecimal.TEN, Category.CPU, new Stock(0), new Manufacturer(), List.of(new ProductSpec()));
        Product product2 = new Product("Name", "Description", BigDecimal.TEN, Category.CPU, new Stock(100), new Manufacturer(), List.of(new ProductSpec()));

        assertEquals(0, product1.getStock().getUnit());
        assertEquals(100, product2.getStock().getUnit());
    }

    @Test
    void updateTest01() {
        // arrange
        Product productDefault = Product.builder()
            .id(1L)
            .name("Product-san")
            .description("Description-san")
            .price(BigDecimal.TEN)
            .category(Category.CPU)
            .stock(new Stock(200))
            .manufacturer(new Manufacturer("AMD"))
            .build();
        Product dataForUpdate = Product.builder()
            .name("Updated Name-san")
            .description("Updated Description-san")
            .price(BigDecimal.TEN)
            .category(Category.CPU)
            .manufacturer(new Manufacturer("INTEL"))
            .build();

        // act
        productDefault.update(dataForUpdate);
        
        // assert
        assertAll(() -> {
            assertEquals(productDefault.getName(), dataForUpdate.getName());
            assertEquals(productDefault.getDescription(), dataForUpdate.getDescription());
            assertEquals(productDefault.getCategory(), dataForUpdate.getCategory());
            assertEquals(productDefault.getPrice(), dataForUpdate.getPrice());
            assertEquals(productDefault.getManufacturer(), dataForUpdate.getManufacturer());
        });
    }

    @Test
    void updateStockTest01() {
        // arrange
        Product productDefault = Product.builder()
            .id(1L)
            .name("Product-san")
            .description("Description-san")
            .price(BigDecimal.TEN)
            .category(Category.CPU)
            .stock(new Stock(100))
            .manufacturer(new Manufacturer("AMD"))
            .build();

        // act and assert
        assertAll(() -> {
            productDefault.updateStock(100);
            assertEquals(200, productDefault.getStock().getUnit());

            productDefault.updateStock(-200);
            assertEquals(0, productDefault.getStock().getUnit());
        });
    }
}