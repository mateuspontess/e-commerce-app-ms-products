package br.com.ecommerce.products.unit;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import br.com.ecommerce.products.model.manufacturer.Manufacturer;
import br.com.ecommerce.products.model.manufacturer.ManufacturerDTO;
import br.com.ecommerce.products.model.product.Category;
import br.com.ecommerce.products.model.product.Product;
import br.com.ecommerce.products.model.product.ProductDTO;
import br.com.ecommerce.products.model.product.ProductIdAndUnitsDTO;
import br.com.ecommerce.products.model.product.ProductResponseDTO;
import br.com.ecommerce.products.model.product.ProductSpecDTO;
import br.com.ecommerce.products.model.product.ProductUpdateDTO;
import br.com.ecommerce.products.model.product.Stock;
import br.com.ecommerce.products.model.product.StockDTO;
import br.com.ecommerce.products.model.product.StockWriteOffDTO;
import br.com.ecommerce.products.repository.ManufacturerRepository;
import br.com.ecommerce.products.repository.ProductRepository;
import br.com.ecommerce.products.service.ProductService;
import br.com.ecommerce.products.utils.RandomUtils;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class ProductServiceUnitTest {

    @Mock
    private ProductRepository repository;
    @Mock
    private ManufacturerRepository manufacturerRepository;
    @InjectMocks
    private ProductService service;

    private final Product testProductDefault = RandomUtils.getRandomProduct(true);

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(service, "mapper", new ModelMapper());
    }

    
    @Test
    @DisplayName("Unit - getProduct - Must return product details by ID")
    void getProductTest01() {
        // arrange
        Product product = this.testProductDefault;
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
	}
    @Test
    @DisplayName("Unit - getProduct - Must not return product details by non-existent ID")
    void getProductTest02() {
        assertThrows(EntityNotFoundException.class, () -> service.getProduct(10L));
	}

    @Test
    @DisplayName("Unit - getAllProductWithParams - Must return all products by params")
    void getAllProductWithParamsTest01() {
        // arrange
        Product product = this.testProductDefault;

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
    @DisplayName("Unit - getAllBySpecs - Must return all products by specifications")
    void getAllBySpecsTest01() {
        // arrange
        Product product = RandomUtils.getRandomProduct(true);

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
    @DisplayName("Unit - verifyProductsStocks - Must return products with insufficient stock")
    void verifyProductsStocksTest01() {
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
        var result = service.verifyProductsStocks(requestBody);

        // assert
        assertEquals(1, result.size());
        assertEquals(result.get(0).getId(), products.get(1).getId());
    }
    @Test
    @DisplayName("Unit - getAllProductsByListOfIds - Must return all products by list of IDs")
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
    @DisplayName("Unit - updateProductData - Update product with full data")
    void updateProductDataTest01() {
        // arrange
        Product target = this.testProductDefault;
        
        ManufacturerDTO newManufacturer = new ManufacturerDTO("INTEL");
        ProductUpdateDTO requestBody = 
            new ProductUpdateDTO("UPDATE-NAME", "UPDATE-DESCRIPTION", BigDecimal.ONE, Category.FAN, newManufacturer);

        when(repository.getReferenceById(any())).thenReturn(target);
        when(manufacturerRepository.findByName(any())).thenReturn(Optional.of(new Manufacturer(requestBody.getManufacturer().getName())));
        
        // act
        service.updateProductData(1L, requestBody);

        // assert
        assertEquals(requestBody.getName(), target.getName());
        assertEquals(requestBody.getDescription(), target.getDescription());
        assertEquals(requestBody.getPrice(), target.getPrice());
        assertEquals(requestBody.getCategory(), target.getCategory());
        assertEquals(requestBody.getManufacturer().getName(), target.getManufacturer().getName());
    }
    @Test
    @DisplayName("Unit - updateProductData - Must update product without a new manufacturer")
    void updateProductDataTest02() {
        // arrange
        Product target = this.testProductDefault;

        ProductUpdateDTO requestBody = 
            new ProductUpdateDTO("UPDATE-NAME", "UPDATE-DESCRIPTION", BigDecimal.ONE, Category.FAN, null);
        when(repository.getReferenceById(any())).thenReturn(target);
        
        // act
        service.updateProductData(1L, requestBody);

        // assert
        assertEquals(requestBody.getName(), target.getName());
        assertEquals(requestBody.getDescription(), target.getDescription());
        assertEquals(requestBody.getPrice(), target.getPrice());
        assertEquals(requestBody.getCategory(), target.getCategory());
    }
    @Test
    @DisplayName("Unit - updateProductData - Should fail when trying to update to a manufacturer that does not yet exist")
    void updateProductDataTest03() {
        // arrange
        Product target = this.testProductDefault;

        ProductUpdateDTO input = 
            new ProductUpdateDTO(null, null, null, null, new ManufacturerDTO("non-existent"));
        when(repository.getReferenceById(any())).thenReturn(target);
        
        // act
        assertThrows(EntityNotFoundException.class, () -> service.updateProductData(1L, input));
    }

    @Test
    @DisplayName("Unit - updateStockByProductId - Must update product stock by product ID")
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
    @DisplayName("Unit - updateStocks - Must update stocks of multiple products")
    void updateStocksTes01() {
        // arrange
        List<Product> products = List.of(
            Product.builder().id(1L).stock(new Stock(100)).build(),
            Product.builder().id(2L).stock(new Stock(200)).build(),
            Product.builder().id(3L).stock(new Stock(300)).build()
        );
        List<StockWriteOffDTO> stockWriteOff = List.of(
            new StockWriteOffDTO(1L, 100),
            new StockWriteOffDTO(2L, 200),
            new StockWriteOffDTO(3L, 300)
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
    @DisplayName("Unit - createProduct - Must create product with valid data")
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