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
    void updateStock_withNegativeOne_shouldHaveOneUnitLeft() {
        // arran
        Stock stock = new Stock(2);

        // act
        stock.update(-1);

        // assert
        int remainingUnits = stock.getUnit();
        assertEquals(1, remainingUnits, "Stock should be updated to 1 unit");
    }
    @Test
    void updateStock_withNegativeTwo_shouldHaveZeroUnitsLeft() {
        // arrange
        Stock stock = new Stock(2);

        // act
        stock.update(-2);

        // assert
        int remainingUnits = stock.getUnit();
        assertEquals(0, remainingUnits, "Stock should be updated to 0 units");
    }
    @Test
    void updateStock_withExcessivelyNegativeValue_shouldHaveZeroUnitsLeft() {
        // arrange
        Stock stock = new Stock(2);

        // act
        stock.update(-1000);

        // assert
        int remainingUnits = stock.getUnit();
        assertEquals(0, remainingUnits, "Stock should be updated to 0 units even when the update value is excessively negative");
    }
}