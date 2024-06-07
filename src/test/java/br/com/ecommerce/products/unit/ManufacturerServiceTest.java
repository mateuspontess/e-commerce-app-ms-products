package br.com.ecommerce.products.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import br.com.ecommerce.products.model.manufacturer.Manufacturer;
import br.com.ecommerce.products.model.manufacturer.ManufacturerDTO;
import br.com.ecommerce.products.model.manufacturer.ManufacturerResponseDTO;
import br.com.ecommerce.products.repository.ManufacturerRepository;
import br.com.ecommerce.products.service.ManufacturerService;

@ExtendWith(MockitoExtension.class)
class ManufacturerServiceTest {

    @Mock
    private ManufacturerRepository repository;
    @InjectMocks
    private ManufacturerService service;

    @Test
    @DisplayName("Test retrieving all manufacturers")
    void getAllTest01() {
        // arrange
        Manufacturer manufacturer = new Manufacturer("AMD");
        when(repository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(manufacturer)));
        
        // act
        ManufacturerResponseDTO result = service.getAll(PageRequest.of(0, 10))
            .getContent()
            .get(0);

        // assert
		assertNotNull(result);
		assertEquals(manufacturer.getName(), result.getName());
	}

    @Test
    @DisplayName("Test retrieving manufacturer by ID")
    void getByIdTest01() {
        // arrange
        Manufacturer manufacturer = new Manufacturer("AMD");
        when(repository.findById(any())).thenReturn(Optional.of(manufacturer));
        
        // act
        ManufacturerResponseDTO result = service.getById(1L);

        // assert
		assertNotNull(result);
		assertEquals(manufacturer.getName(), result.getName());
	}

    @Test
    @DisplayName("Test saving manufacturer")
    void saveManufacturerTest01() {
        // arrange
        ManufacturerDTO requestBody = new ManufacturerDTO("AMD");
        when(repository.save(any())).thenReturn(new Manufacturer(requestBody.getName()));
        
        // act
        ManufacturerResponseDTO result = service.saveManufacturer(requestBody);

        // assert
        assertEquals(requestBody.getName(), result.getName());
	}

    @Test
    @DisplayName("Test updating manufacturer")
    void updateManufacturerTest01() {
        // arrange
        Manufacturer target = new Manufacturer("AMD");
        ManufacturerDTO requestBody = new ManufacturerDTO("INTEL");
        when(repository.getReferenceById(any())).thenReturn(target);
        
        // act
        service.updateManufacturer(1L, requestBody);

        assertEquals(requestBody.getName(), target.getName());
	}
}