package br.com.ecommerce.products.unit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.ecommerce.products.model.product.Stock;

public class StockUnitTest {

    @Test
    @DisplayName("Unit - Must create a stock")
    void createStockTest01() {
        assertDoesNotThrow(() -> new Stock(100));
    }
    @Test
    @DisplayName("Unit - Should not create a stock with negative units")
    void createStockTest02() {
        assertThrows(IllegalArgumentException.class, () -> new Stock(-100));
    }

    @Test
    void updateStockTest01() {
        Stock stock1 = new Stock(2);
        stock1.update(-1);
        var result1 = stock1.getUnit();
        assertEquals(1, result1);
        
        Stock stock2 = new Stock(2);
        stock2.update(-2);
        var result2 = stock2.getUnit();
        assertEquals(0, result2);
        
        Stock stock3 = new Stock(2);
        stock3.update(-1000);
        var result3 = stock3.getUnit();
        assertEquals(0, result3);
    }
}