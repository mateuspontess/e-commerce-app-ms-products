package br.com.ecommerce.products.unit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.ecommerce.products.model.manufacturer.Manufacturer;
import br.com.ecommerce.products.model.product.Category;
import br.com.ecommerce.products.model.product.Product;
import br.com.ecommerce.products.model.product.ProductSpec;
import br.com.ecommerce.products.model.product.Stock;

public class ProductTest {

    @Test
    @DisplayName("Test creating product with invalid data")
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
    @DisplayName("Test creating product with valid data")
    void createProductTest02() {
        assertDoesNotThrow( 
            () -> new Product("Name", "Description", BigDecimal.TEN, Category.CPU, new Stock(), new Manufacturer(), List.of(new ProductSpec())));
    }

    @Test
    @DisplayName("Test updating product with new data")
    void updateTest01() {
        // arrange
        Product productDefault = Product.builder()
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
        assertEquals(dataForUpdate.getName(), productDefault.getName());
        assertEquals(dataForUpdate.getDescription(), productDefault.getDescription());
        assertEquals(dataForUpdate.getCategory(), productDefault.getCategory());
        assertEquals(dataForUpdate.getPrice(), productDefault.getPrice());
        assertEquals(dataForUpdate.getManufacturer(), productDefault.getManufacturer());
    }

    @Test
    @DisplayName("Test updating product stock")
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
        productDefault.updateStock(+100);
        assertEquals(200, productDefault.getStock().getUnit());

        productDefault.updateStock(-200);
        assertEquals(0, productDefault.getStock().getUnit());
    }
}