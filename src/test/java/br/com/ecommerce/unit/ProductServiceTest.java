package br.com.ecommerce.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import br.com.ecommerce.products.model.manufacturer.Manufacturer;
import br.com.ecommerce.products.model.manufacturer.ManufacturerDTO;
import br.com.ecommerce.products.model.product.Category;
import br.com.ecommerce.products.model.product.Product;
import br.com.ecommerce.products.model.product.ProductIdAndUnitsDTO;
import br.com.ecommerce.products.model.product.ProductResponseDTO;
import br.com.ecommerce.products.model.product.ProductSpec;
import br.com.ecommerce.products.model.product.ProductUpdateDTO;
import br.com.ecommerce.products.model.product.Stock;
import br.com.ecommerce.products.repository.ManufacturerRepository;
import br.com.ecommerce.products.repository.ProductRepository;
import br.com.ecommerce.products.service.ProductService;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository repository;
    @Mock
    private ManufacturerRepository manufacturerRepository;
    @InjectMocks
    private ProductService service;

    // manually inject ModelMapper
    @BeforeEach
    void setup() {
        this.service = new ProductService(repository, manufacturerRepository, new ModelMapper());
    }

    @Test
    void getProductTest01() {
        // arrange
        Product product = Product.builder()
                .id(1L)
                .name("Product-san")
                .description("Description-san")
                .price(BigDecimal.TEN)
                .category(Category.CPU)
                .stock(new Stock(200))
                .manufacturer(new Manufacturer("AMD"))
                .build();

        when(repository.findById(anyLong())).thenReturn(Optional.of(product));
        
        // act
        ProductResponseDTO result = service.getProduct(1L);

        // assert
		assertNotNull(result);
		assertEquals(product.getId(), result.getId());
		assertEquals(product.getName(), result.getName());
		assertEquals(product.getDescription(), result.getDescription());
		assertEquals(product.getPrice(), result.getPrice());
		assertEquals(product.getStock(), result.getStock());
		assertEquals(product.getManufacturer().getName(), result.getManufacturer().getName());
		assertNull(result.getSpecs());
	}
    @Test
    void getProductTest02() {
        assertThrows(EntityNotFoundException.class, () -> service.getProduct(10L));
	}

    @Test
    void getAllProductWithParamsTest01() {
        // arrange
        Product product = Product.builder()
                .id(1L)
                .name("Product-san")
                .description("Description-san")
                .price(BigDecimal.TEN)
                .category(Category.CPU)
                .stock(new Stock(200))
                .manufacturer(new Manufacturer("AMD"))
                .specs(List.of(new ProductSpec()))
                .build();

        when(repository.findAllByParams(any(), any(), any(), any(), any(), any()))
            .thenReturn(new PageImpl<Product>(List.of(product)));
        
        // act
        ProductResponseDTO result = service
            .getAllProductWithParams(PageRequest.of(0, 10), null, null, null, null, null)
            .getContent()
            .get(0);

        // assert
        assertNotNull(result);
        assertEquals(product.getId(), result.getId());
        assertEquals(product.getName(), result.getName());
        assertEquals(product.getDescription(), result.getDescription());
        assertEquals(product.getPrice(), result.getPrice());
        assertEquals(product.getStock(), result.getStock());
        assertEquals(product.getManufacturer().getName(), result.getManufacturer().getName());
    }

    @Test
    void getAllBySpecsTest01() {
        // arrange
        Product product = Product.builder()
                .id(1L)
                .name("Product-san")
                .description("Description-san")
                .price(BigDecimal.TEN)
                .category(Category.CPU)
                .stock(new Stock(200))
                .manufacturer(new Manufacturer("AMD"))
                .specs(List.of(new ProductSpec()))
                .build();

        when(repository.findProductsBySpecs(any(), any()))
            .thenReturn(new PageImpl<Product>(List.of(product)));
        
        // act
        ProductResponseDTO result = service
            .getAllBySpecs(PageRequest.of(0, 10), List.of(Map.of("string", "string")))
            .getContent()
            .get(0);

        // assert
        assertNotNull(result);
        assertEquals(product.getId(), result.getId());
        assertEquals(product.getName(), result.getName());
        assertEquals(product.getDescription(), result.getDescription());
        assertEquals(product.getPrice(), result.getPrice());
        assertEquals(product.getStock(), result.getStock());
        assertEquals(product.getManufacturer().getName(), result.getManufacturer().getName());
    }

    @Test
    void verifyStocksTest01() {
        // arrange
        List<Product> products = List.of(
            Product.builder().id(1L).stock(new Stock(100)).build(),
            Product.builder().id(2L).stock(new Stock(1)).build()
        );

        List<ProductIdAndUnitsDTO> requestBody = List.of(
            new ProductIdAndUnitsDTO(1L, 99),
            new ProductIdAndUnitsDTO(2L, 2)
        );

        when(repository.findAllById(any())).thenReturn(products);
        
        // act
        var result = service.verifyStocks(requestBody);

        // assert
        assertEquals(1, result.size());
        assertEquals(result.get(0).getId(), products.get(1).getId());
    }
    @Test
    void getAllProductsByListOfIds01() {
        // arrange
        List<Product> products = List.of(
            Product.builder().id(1L).stock(new Stock(100)).build()
        );

        when(repository.findAllById(any())).thenReturn(products);
        
        // act
        var result = service.getAllProductsByListOfIds(List.of(1L));

        // assert
        assertEquals(products.get(0).getId(), result.get(0).getId());
        assertEquals(products.get(0).getStock().getUnit(), result.get(0).getStock().getUnit());
    }

    @Test
    void updateProductTest01() {
        // arrange
        Manufacturer oldManufacturer = new Manufacturer("AMD");
        Product product = Product.builder()
                .name("Product-san")
                .description("Description-san")
                .price(BigDecimal.TEN)
                .category(Category.CPU)
                .manufacturer(oldManufacturer)
                .build();

        ProductUpdateDTO dataUpdate = 
            new ProductUpdateDTO("UPDATE-NAME", "UPDATE-DESCRIPTION", BigDecimal.ONE, Category.FAN.toString(), new ManufacturerDTO("INTEL"));

        when(repository.getReferenceById(any())).thenReturn(product);
        when(manufacturerRepository.getReferenceById(any())).thenReturn(oldManufacturer);
        when(manufacturerRepository.findByName(any())).thenReturn(Optional.of(new Manufacturer(dataUpdate.getManufacturer().getName())));
        
        // act
        service.updateProduct(1L, dataUpdate);

        // assert
        assertEquals(dataUpdate.getName(), product.getName());
        assertEquals(dataUpdate.getDescription(), product.getDescription());
        assertEquals(dataUpdate.getPrice(), product.getPrice());
        assertEquals(dataUpdate.getCategory().toUpperCase(), product.getCategory().toString().toUpperCase());
        assertEquals(dataUpdate.getManufacturer().getName(), product.getManufacturer().getName());
    }
    @Test
    void updateProductTest02() {
        // arrange
        Product product = Product.builder()
                .name("Product-san")
                .description("Description-san")
                .price(BigDecimal.TEN)
                .category(Category.CPU)
                .build();

        ProductUpdateDTO dataUpdate = 
            new ProductUpdateDTO("UPDATE-NAME", "UPDATE-DESCRIPTION", BigDecimal.ONE, Category.FAN.toString(), null);

        when(repository.getReferenceById(any())).thenReturn(product);
        
        // act
        service.updateProduct(1L, dataUpdate);

        // assert
        assertEquals(dataUpdate.getName(), product.getName());
        assertEquals(dataUpdate.getDescription(), product.getDescription());
        assertEquals(dataUpdate.getPrice(), product.getPrice());
        assertEquals(dataUpdate.getCategory().toUpperCase(), product.getCategory().toString().toUpperCase());
    }
}