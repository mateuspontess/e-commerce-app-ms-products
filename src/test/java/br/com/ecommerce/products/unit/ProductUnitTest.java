package br.com.ecommerce.products.unit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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
import br.com.ecommerce.products.utils.RandomUtils;

public class ProductUnitTest {

    @Test
    @DisplayName("Unit - createProduct - Product must not be created with blank/null name entry")
    void createProductTest01() {
        assertThrows(IllegalArgumentException.class, 
            () -> new Product(null, "Description", BigDecimal.TEN, Category.CPU, new Stock(), new Manufacturer(), List.of(new ProductSpec())));

        assertThrows(IllegalArgumentException.class, 
            () -> new Product("", "Description", BigDecimal.TEN, Category.CPU, new Stock(), new Manufacturer(), List.of(new ProductSpec())));
    }
    @Test
    @DisplayName("Unit - createProduct - Product must not be created with blank/null description entry")
    void createProductTest02() {
        assertThrows(IllegalArgumentException.class, 
            () -> new Product("Name", null, BigDecimal.TEN, Category.CPU, new Stock(), new Manufacturer(), List.of(new ProductSpec())));

        assertThrows(IllegalArgumentException.class, 
            () -> new Product("Name", "", BigDecimal.TEN, Category.CPU, new Stock(), new Manufacturer(), List.of(new ProductSpec())));
    }
    @Test
    @DisplayName("Unit - createProduct - Product must not be created with null/negative price entry")
    void createProductTest03() {
        assertThrows(IllegalArgumentException.class, 
            () -> new Product("Name", "Description", null, Category.CPU, new Stock(), new Manufacturer(), List.of(new ProductSpec())));

        assertThrows(IllegalArgumentException.class, 
            () -> new Product("Name", "Description", BigDecimal.valueOf(-1), Category.CPU, new Stock(), new Manufacturer(), List.of(new ProductSpec())));
    }
    @Test
    @DisplayName("Unit - createProduct - Product must not be created with null category entry")
    void createProductTest04() {
        assertThrows(IllegalArgumentException.class, 
            () -> new Product("Name", "Description", BigDecimal.TEN, null, new Stock(), new Manufacturer(), List.of(new ProductSpec())));
    }
    @Test
    @DisplayName("Unit - createProduct - Product must not be created with null/negative stock, manufacturer and specs entries")
    void createProductTest06() {
        assertThrows(IllegalArgumentException.class, 
            () -> new Product("Name", "Description", BigDecimal.TEN, Category.CPU, null, null, null));
        assertThrows(IllegalArgumentException.class, 
            () -> new Product("Name", "Description", BigDecimal.TEN, Category.CPU, new Stock(-100), null, null));
    }
    @Test
    @DisplayName("Unit - createProduct - Product must be created with valid entries")
    void createProductTest08() {
        assertDoesNotThrow( 
            () -> new Product("Name", "Description", BigDecimal.TEN, Category.CPU, new Stock(), new Manufacturer(), List.of(new ProductSpec())));
    }

    @Test
    @DisplayName("Unit - update - Product must be updated with valid entries")
    void updateTest01() {
        // arrange
        Product productDefault = RandomUtils.getRandomProduct(true);
        Product dataForUpdate = RandomUtils.getRandomProduct(true);

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
    @DisplayName("Unit - update - Only name and description must not be updated")
    void updateTest02() {
        // arrange
        Product productDefault = RandomUtils.getRandomProduct(true);
        Product dataForUpdate = Product.builder()
            .name("")
            .description("")
            .price(BigDecimal.TEN)
            .category(Category.CPU)
            .stock(new Stock(200))
            .manufacturer(new Manufacturer("AMD"))
            .build();

        // act
        productDefault.update(dataForUpdate);
        
        // assert
        assertNotEquals(dataForUpdate.getName(), productDefault.getName());
        assertNotEquals(dataForUpdate.getDescription(), productDefault.getDescription());
        assertEquals(dataForUpdate.getCategory(), productDefault.getCategory());
        assertEquals(dataForUpdate.getPrice(), productDefault.getPrice());
        assertEquals(dataForUpdate.getManufacturer(), productDefault.getManufacturer());
    }
    @Test
    @DisplayName("Unit - update - Product must not updated with invalid entries")
    void updateTest03() {
        // arrange
        Product productDefault = RandomUtils.getRandomProduct(true);
        Product dataForUpdate = Product.builder()
            .name("")
            .description("")
            .price(null)
            .category(null)
            .manufacturer(null)
            .build();

        // act
        productDefault.update(dataForUpdate);
        
        // assert
        assertNotEquals(dataForUpdate.getName(), productDefault.getName());
        assertNotEquals(dataForUpdate.getDescription(), productDefault.getDescription());
        assertNotEquals(dataForUpdate.getCategory(), productDefault.getCategory());
        assertNotEquals(dataForUpdate.getPrice(), productDefault.getPrice());
        assertNotEquals(dataForUpdate.getManufacturer(), productDefault.getManufacturer());
    }

    @Test
    @DisplayName("Unit - updateStockTest - Updating product stock")
    void updateStockTest01() {
        // arrange
        Product productDefault = Product.builder()
            .stock(new Stock(100))
            .build();

        // act and assert
        productDefault.updateStock(+100);
        assertEquals(200, productDefault.getStock().getUnit());

        productDefault.updateStock(-200);
        assertEquals(0, productDefault.getStock().getUnit());
    }
}