package br.com.ecommerce.products.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

import br.com.ecommerce.products.model.manufacturer.Manufacturer;
import br.com.ecommerce.products.model.manufacturer.ManufacturerDTO;
import br.com.ecommerce.products.model.manufacturer.ManufacturerResponseDTO;
import br.com.ecommerce.products.repository.ManufacturerRepository;
import br.com.ecommerce.products.service.ManufacturerService;
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
    @DisplayName("Integration - Get all - Must return a list of ManufacturerResponseDTO")
    void getAllTest01() {
        // arrange
        var persistedNames = repository.saveAll(List.of(new Manufacturer("Corsair"), new Manufacturer("Logitech"), new Manufacturer("Redragon")))
                .stream()
                    .map(m -> m.getName())
                    .toList();

        // act
        List<String> resultNames = service.getAll(PageRequest.of(0, 10))
            .getContent()
            .stream()
            .map(result -> result.getName())
            .toList();

        // assert
        assertTrue(persistedNames.containsAll(resultNames));
    }

    @Test
    @DisplayName("Integration - Get by id - Must return a ManufacturerResponseDTO")
    void getByIdTest01() {
        // arrange
        var persisted = repository.save(new Manufacturer("Corsair"));

        // act
        ManufacturerResponseDTO result = service.getById(1L);

        // assert
        assertEquals(persisted.getName(), result.getName());
    }

    @Test
    @DisplayName("Integration - Save manufacturer - Must return a ManufacturerResponseDTO")
    void saveManufacturerTest01() {
        // act
        ManufacturerResponseDTO result = service.saveManufacturer(new ManufacturerDTO("LIAN LI"));

        // assert
        assertNotNull(result.getId());
        assertEquals("LIAN LI", result.getName());
    }

    @Test
    @DisplayName("Integration - Update manufacturer - Must return a ManufacturerResponseDTO updated")
    void updateManufacturerTest01() {
        // arrange
        repository.save(new Manufacturer("Cooler Master"));

        // act
        String NEW_NAME = "CoolerMaster";
        ManufacturerResponseDTO updated = service.updateManufacturer(1L, new ManufacturerDTO(NEW_NAME));

        // assert
        assertEquals(NEW_NAME.toUpperCase(), updated.getName());
    }
}