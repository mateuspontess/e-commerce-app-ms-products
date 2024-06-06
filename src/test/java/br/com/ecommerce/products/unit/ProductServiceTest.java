package br.com.ecommerce.products.unit;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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
import br.com.ecommerce.products.model.product.ProductDTO;
import br.com.ecommerce.products.model.product.ProductIdAndUnitsDTO;
import br.com.ecommerce.products.model.product.ProductResponseDTO;
import br.com.ecommerce.products.model.product.ProductSpec;
import br.com.ecommerce.products.model.product.ProductSpecDTO;
import br.com.ecommerce.products.model.product.ProductUpdateDTO;
import br.com.ecommerce.products.model.product.Stock;
import br.com.ecommerce.products.model.product.StockDTO;
import br.com.ecommerce.products.model.product.StockWriteOffDTO;
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

        ProductUpdateDTO requestBody = 
            new ProductUpdateDTO("UPDATE-NAME", "UPDATE-DESCRIPTION", BigDecimal.ONE, Category.FAN.toString(), new ManufacturerDTO("INTEL"));

        when(repository.getReferenceById(any())).thenReturn(product);
        when(manufacturerRepository.getReferenceById(any())).thenReturn(oldManufacturer);
        when(manufacturerRepository.findByName(any())).thenReturn(Optional.of(new Manufacturer(requestBody.getManufacturer().getName())));
        
        // act
        service.updateProduct(1L, requestBody);

        // assert
        assertEquals(requestBody.getName(), product.getName());
        assertEquals(requestBody.getDescription(), product.getDescription());
        assertEquals(requestBody.getPrice(), product.getPrice());
        assertEquals(requestBody.getCategory().toUpperCase(), product.getCategory().toString().toUpperCase());
        assertEquals(requestBody.getManufacturer().getName(), product.getManufacturer().getName());
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

        ProductUpdateDTO requestBody = 
            new ProductUpdateDTO("UPDATE-NAME", "UPDATE-DESCRIPTION", BigDecimal.ONE, Category.FAN.toString(), null);

        when(repository.getReferenceById(any())).thenReturn(product);
        
        // act
        service.updateProduct(1L, requestBody);

        // assert
        assertEquals(requestBody.getName(), product.getName());
        assertEquals(requestBody.getDescription(), product.getDescription());
        assertEquals(requestBody.getPrice(), product.getPrice());
        assertEquals(requestBody.getCategory().toUpperCase(), product.getCategory().toString().toUpperCase());
    }

    @Test
    void updateStockByProductIdTest01() {
        // arrange
        Product product = Product.builder()
            .stock(new Stock(100))            
            .build();

        when(repository.getReferenceById(anyLong())).thenReturn(product);

        // act and assert
        assertAll(
            () -> {
            service.updateStockByProductId(1L, new StockDTO(-100));
            assertEquals(0, product.getStock().getUnit());
            },
            
            () -> {
            service.updateStockByProductId(1L, new StockDTO(+150));
            assertEquals(+150, product.getStock().getUnit());
            },
            
            () -> {
            service.updateStockByProductId(1L, new StockDTO(-200));
            assertEquals(0, product.getStock().getUnit());
            }
        );
    }
    @Test
    void updateStocksTes01() {
        // arrange
        List<Product> products = List.of(
            Product.builder().id(1L).stock(new Stock(100)).build(),
            Product.builder().id(2L).stock(new Stock(200)).build(),
            Product.builder().id(3L).stock(new Stock(300)).build()
        );
        List<StockWriteOffDTO> stockWriteOff = List.of(
            new StockWriteOffDTO(1L, -100),
            new StockWriteOffDTO(2L, -200),
            new StockWriteOffDTO(3L, -300)
        );

        when(repository.findAllById(anyList())).thenReturn(products);

        // act
        service.updateStocks(stockWriteOff);

        // assert
        assertAll(
            () -> assertEquals(0, products.get(0).getStock().getUnit()),
            () -> assertEquals(0, products.get(1).getStock().getUnit()),
            () -> assertEquals(0, products.get(2).getStock().getUnit())
        );
    }

    @Test
    void createProductTest01() {
        // arrange
        ProductDTO requestBody = new ProductDTO(
            "Name",
            "Description",
            new BigDecimal("350"),
            Category.CPU,
            new StockDTO(150),
            new ManufacturerDTO("AMD"),
            List.of(new ProductSpecDTO("socket", "AM4"))
        );

        when(manufacturerRepository.findByName(any()))
            .thenReturn(Optional.of(new Manufacturer(requestBody.getManufacturer().getName())));

        // act
        ProductResponseDTO result = service.createProduct(requestBody);

        // assert
        assertEquals(requestBody.getName(), result.getName());
        assertEquals(requestBody.getDescription(), result.getDescription());
        assertEquals(requestBody.getPrice(), result.getPrice());
        assertEquals(requestBody.getCategory(), result.getCategory());
        assertEquals(requestBody.getManufacturer().getName(), result.getManufacturer().getName());
        assertEquals(requestBody.getSpecs().get(0).getAttribute(), result.getSpecs().get(0).getAttribute());
        assertEquals(requestBody.getSpecs().get(0).getValue(), result.getSpecs().get(0).getValue());
    }
}