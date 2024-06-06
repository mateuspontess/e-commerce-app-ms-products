package br.com.ecommerce.unit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import br.com.ecommerce.products.model.manufacturer.Manufacturer;

public class ManufacturerTest {

    @Test
    void createManufacturerTest01() {
        assertThrows(IllegalArgumentException.class, () -> new Manufacturer(null));
        assertThrows(IllegalArgumentException.class, () -> new Manufacturer(""));
        
        assertDoesNotThrow(() -> new Manufacturer("AMD"));
    }

    @Test
    void updateName() {
        Manufacturer manufacturer = new Manufacturer("AMD");
        assertThrows(IllegalArgumentException.class, () -> manufacturer.updateName(null));
        assertThrows(IllegalArgumentException.class, () -> manufacturer.updateName(""));
        
        assertDoesNotThrow(() -> manufacturer.updateName("INTEL"));
    }
}