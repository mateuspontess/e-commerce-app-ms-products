package br.com.ecommerce.products.integration;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

import br.com.ecommerce.products.model.manufacturer.Manufacturer;
import br.com.ecommerce.products.model.manufacturer.ManufacturerDTO;
import br.com.ecommerce.products.model.manufacturer.ManufacturerResponseDTO;
import br.com.ecommerce.products.repository.ManufacturerRepository;
import br.com.ecommerce.products.service.ManufacturerService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
class ManufacturerServiceIntegrationTest {

    @Autowired
    private ManufacturerService service;
    @Autowired
    private ManufacturerRepository repository;


    @Test
    @DisplayName("Integration - saveManufacturer - Must return a ManufacturerResponseDTO")
    void saveManufacturerTest01() {
        // act
        ManufacturerResponseDTO result = service.saveManufacturer(new ManufacturerDTO("LIAN LI"));

        // assert
        assertNotNull(result.getId());
        assertEquals("LIAN LI", result.getName());
    }

    @Test
    @DisplayName("Integration - updateManufacturerData - Must return a ManufacturerResponseDTO updated")
    void updateManufacturerDataTest01() {
        // arrange
        repository.save(new Manufacturer("Cooler Master"));

        // act
        String NEW_NAME = "CoolerMaster";
        ManufacturerResponseDTO updated = service.updateManufacturerData(1L, new ManufacturerDTO(NEW_NAME));

        // assert
        assertEquals(NEW_NAME.toUpperCase(), updated.getName());
    }
    @Test
    @DisplayName("Integration - updateManufacturerData - Should throw exception when not finding manufacturer")
    void updateManufacturerDataTest02() {
        assertThrows(EntityNotFoundException.class, 
        () -> service.updateManufacturerData(1000L, new ManufacturerDTO("non-existent")));
    }
}