package br.com.ecommerce.products.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

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
import jakarta.transaction.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
class ProductServiceIntegrationTest {

    @Autowired
    private ProductService service;
    @Autowired
    private ProductRepository repository;
    @Autowired
    private ManufacturerRepository manufacturerRepository;

    private List<Product> productsPersisted;
    private List<Manufacturer> manufacturersPersisted;

    @BeforeEach
    void setup() {
        manufacturersPersisted = manufacturerRepository.saveAll(
            List.of(new Manufacturer("AMD"), new Manufacturer("INTEL"))
        );

        Product p1 = createProduct(manufacturersPersisted.get(0), "aaa", "ddd", BigDecimal.valueOf(1000), Category.CPU, 1000, "cores", "12");
        Product p2 = createProduct(manufacturersPersisted.get(0), "bbb", "ddd", BigDecimal.valueOf(100), Category.CPU, 100, "cores", "8");
        Product p3 = createProduct(manufacturersPersisted.get(1), "ccc", "ddd", BigDecimal.TEN, Category.GPU, 10, "memory size", "8GB");
        this.productsPersisted = repository.saveAll(List.of(p1, p2, p3));
    }

    Product createProduct(Manufacturer manufacturer, String name, String description, BigDecimal price, Category category, int stockUnits, String specAttribute, String specValue) {
        ProductSpec spec = new ProductSpec(specAttribute, specValue);
        Product product = Product.builder()
            .name(name)
            .description(description)
            .price(price)
            .category(category)
            .stock(new Stock(stockUnits))
            .manufacturer(manufacturer)
            .specs(List.of(spec))
            .build();
        spec.setProduct(product);
        return product;
    }

    @Test
    @DisplayName("Integration - Must return Product details")
    void getProductTest01() {
        // act
        ProductResponseDTO result = service.getProduct(1L);

        // assert
		assertNotNull(result);
    }
    @Test
    @DisplayName("Integration - Should fail when finding non-existing Product")
    void getProductTest02() {
        assertThrows(EntityNotFoundException.class, () -> service.getProduct(1000L));
	}

    @Test
    @DisplayName("Integration - Must return all products according to all parameters")
    void getAllProductWithParamsTest01() {
        // act
        var result = service
            .getAllProductWithParams(
                Pageable.unpaged(), "aaa", Category.CPU, BigDecimal.ZERO, BigDecimal.valueOf(1000), "AMD").getContent();

        // assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }
    @Test
    @DisplayName("Integration - Must return all products when all parameters are null")
    void getAllProductWithParamsTest02() {
        // act
        var result = service
            .getAllProductWithParams(Pageable.unpaged(), null, null, null, null, null)
            .getContent();

        // assert
        assertNotNull(result);
        assertEquals(3, result.size());
    }
    @Test
    @DisplayName("Integration - Must return all products with the name parameter")
    void getAllProductWithParamsTest03() {
        // act
        var result = service
            .getAllProductWithParams(Pageable.unpaged(), "aaa", null, null, null, null)
            .getContent();

        // assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }
    @Test
    @DisplayName("Integration - Must return all products with the category parameter")
    void getAllProductWithParamsTest04() {
        // act
        var result = service
            .getAllProductWithParams(Pageable.unpaged(), null, Category.CPU, null, null, null)
            .getContent();

        // assert
        assertEquals(2, result.size());
    }
    @Test
    @DisplayName("Integration - Must return all products with the minPrice parameter")
    void getAllProductWithParamsTest05() {
        // act
        var result1 = service
            .getAllProductWithParams(Pageable.unpaged(), null, null, BigDecimal.TEN, null, null)
            .getContent();
        var result2 = service
            .getAllProductWithParams(Pageable.unpaged(), null, null, BigDecimal.valueOf(100), null, null)
            .getContent();
        var result3 = service
            .getAllProductWithParams(Pageable.unpaged(), null, null, BigDecimal.valueOf(1000), null, null)
            .getContent();
        var result4 = service
            .getAllProductWithParams(Pageable.unpaged(), null, null, BigDecimal.valueOf(1001), null, null)
            .getContent();

        // assert
        assertEquals(3, result1.size());
        assertEquals(2, result2.size());
        assertEquals(1, result3.size());
        assertEquals(0, result4.size());
    }
    @Test
    @DisplayName("Integration - Must return all products with the maxPrice parameter")
    void getAllProductWithParamsTest06() {
        // act
        var result1 = service
            .getAllProductWithParams(Pageable.unpaged(), null, null, null, BigDecimal.valueOf(1000), null)
            .getContent();
        var result2 = service
            .getAllProductWithParams(Pageable.unpaged(), null, null, null, BigDecimal.valueOf(100), null)
            .getContent();
        var result3 = service
            .getAllProductWithParams(Pageable.unpaged(), null, null, null, BigDecimal.ONE, null)
            .getContent();

        // assert
        assertEquals(3, result1.size());
        assertEquals(2, result2.size());
        assertEquals(0, result3.size());
    }
    @Test
    @DisplayName("Integration - Must return all products with the manufacturer name parameter")
    void getAllProductWithParamsTest07() {
        // act
        var result = service
            .getAllProductWithParams(Pageable.unpaged(), null, null, null, null, "AMD")
            .getContent();

        // assert
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Integration - Must return all products according to specs")
    void getAllBySpecsTest01() {
        // assert
        List<Map<String, String>> specs = List.of(Map.of("attribute", "cores", "value", "12"));

        // act
        var result = service.getAllBySpecs(Pageable.unpaged(), specs).getContent();

        // assert
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Integration - Getting all products with insuficient stock")
    void verifyStocksTest01() {
        // assert
        List<ProductIdAndUnitsDTO> stockRequest = 
            List.of(new ProductIdAndUnitsDTO(1L, 1001), new ProductIdAndUnitsDTO(2L, 99));

        // act
        var result = service.verifyStocks(stockRequest);

        // assert
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Integration - Getting all products by ID list")
    void getAllProductsByListOfIdsTest01() {
        // act
        var result = service.getAllProductsByListOfIds(List.of(1L, 2L, 3L));

        // assert
        assertEquals(3, result.size());
    }
    @Test
    @DisplayName("Integration - Getting all products by list of non-existent IDs")
    void getAllProductsByListOfIdsTest02() {
        // act
        var result = service.getAllProductsByListOfIds(List.of(1000L, 2000L, 3000L));

        // assert
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Integration - Should not update all attributes")
    void updateProductTest01() {
        // arrange
        Long ID = 1L;
        ProductUpdateDTO updateData = new ProductUpdateDTO(null, null, null, null, null);

        // act
        var result = service.updateProduct(ID, updateData);
        
        // assert
        assertNotNull(result.getName());
        assertNotNull(result.getDescription());
        assertNotNull(result.getPrice());
        assertNotNull(result.getCategory());
        assertNotNull(result.getManufacturer().getName());
    }
    @Test
    @DisplayName("Integration - Should update all attributes")
    void updateProductTest02() {
        // arrange
        Long ID = 1L;
        var EXPECTED_NAME = "update name";
        var EXPECTED_DESCRIPTION = "update description";
        var EXPECTED_PRICE = BigDecimal.valueOf(500);
        var EXPECTED_CATEGORY = Category.COOLER.toString();
        var EXPECTED_MANUFACTURER_NAME = this.manufacturersPersisted.get(0).getName();
        ProductUpdateDTO updateData = new ProductUpdateDTO(EXPECTED_NAME, EXPECTED_DESCRIPTION, EXPECTED_PRICE, Category.fromString(EXPECTED_CATEGORY), new ManufacturerDTO(EXPECTED_MANUFACTURER_NAME));

        // act
        var result = service.updateProduct(ID, updateData);
        
        // assert
        assertEquals(EXPECTED_NAME, result.getName());
        assertEquals(EXPECTED_DESCRIPTION, result.getDescription());
        assertEquals(EXPECTED_CATEGORY, result.getCategory().toString());
        assertEquals(EXPECTED_PRICE, result.getPrice());
        assertEquals(EXPECTED_MANUFACTURER_NAME, result.getManufacturer().getName());
    }
    @Test
    @DisplayName("Integration - Should fail when trying to update to a manufacturer that does not yet exist")
    void updateProductTest03() {
        // act
        Long ID = 1L;
        ProductUpdateDTO updateData = new ProductUpdateDTO(null, null, null, null, new ManufacturerDTO("non-existent"));

        // act and assert
        assertThrows(EntityNotFoundException.class, () -> service.updateProduct(ID, updateData));
    }
    @Test
    @DisplayName("Integration - Should only update the manufacturer")
    void updateProductTest04() {
        // arrange
        Long ID = 1L;
        ProductUpdateDTO updateData = new ProductUpdateDTO(null, null, null, null, new ManufacturerDTO("INTEL"));

        // act
        var result = service.updateProduct(ID, updateData);
        
        // assert
        assertNotEquals(updateData.getName(), result.getName());
        assertNotEquals(updateData.getDescription(), result.getDescription());
        assertNotEquals(updateData.getCategory(), result.getCategory().toString());
        assertNotEquals(updateData.getPrice(), result.getPrice());
        assertEquals(updateData.getManufacturer().getName(), result.getManufacturer().getName());
    }
    @Test
    @DisplayName("Integration - Should only update the category")
    void updateProductTest05() {
        // arrange
        Long ID = 1L;
        var EXPECTED_CATEGORY = Category.COOLER;
        ProductUpdateDTO updateData = new ProductUpdateDTO(null, null, null, EXPECTED_CATEGORY, null);

        // act
        var result = service.updateProduct(ID, updateData);
        
        // assert
        assertEquals(EXPECTED_CATEGORY.toString(), result.getCategory().toString());
        assertNotNull(result.getName());
        assertNotNull(result.getDescription());
        assertNotNull(result.getPrice());
    }
    @Test
    @DisplayName("Integration - Should only update the price")
    void updateProductTest06() {
        // arrange
        Long ID = 1L;
        var EXPECTED_PRICE = BigDecimal.valueOf(500);
        ProductUpdateDTO updateData = new ProductUpdateDTO(null, null, EXPECTED_PRICE, null, null);

        // act
        var result = service.updateProduct(ID, updateData);
        
        // assert
        assertEquals(EXPECTED_PRICE, result.getPrice());
        assertNotNull(result.getCategory().toString());
        assertNotNull(result.getName());
        assertNotNull(result.getDescription());
    }
    @Test
    @DisplayName("Integration - Should only update the description")
    void updateProductTest07() {
        // arrange
        Long ID = 1L;
        var EXPECTED_DESCRIPTION = "update description";
        ProductUpdateDTO updateData = new ProductUpdateDTO(null, EXPECTED_DESCRIPTION, null, null, null);

        // act
        var result = service.updateProduct(ID, updateData);
        
        // assert
        assertEquals(EXPECTED_DESCRIPTION, result.getDescription());
        assertNotNull(result.getPrice());
        assertNotNull(result.getCategory().toString());
        assertNotNull(result.getName());
    }
    @Test
    @DisplayName("Integration - Should only update the name")
    void updateProductTest08() {
        // arrange
        Long ID = 1L;
        var EXPECTED_NAME = "updated name";
        ProductUpdateDTO updateData = new ProductUpdateDTO(EXPECTED_NAME, null, null, null, null);

        // act
        var result = service.updateProduct(ID, updateData);
        
        // assert
        assertEquals(EXPECTED_NAME, result.getName());
        assertNotNull(result.getDescription());
        assertNotNull(result.getPrice());
        assertNotNull(result.getCategory());

    }
    @Test
    @DisplayName("Integration - Must reduce the units in stock")
    void updateStockByProductIdTest01() {
        // arrange
        StockDTO input = new StockDTO(-2);
        
        Product target = this.productsPersisted.get(0);
        Long TARGET_ID = target.getId();
        String EXPECTED_NAME = target.getName();
        Integer EXPECTED_UNITS = target.getStock().getUnit() + input.getUnit();

        // act
        var result = service.updateStockByProductId(TARGET_ID, input);

        // assert
        assertEquals(EXPECTED_UNITS, result.getUnit());
        assertEquals(EXPECTED_NAME, result.getName());
        assertEquals(TARGET_ID, result.getProductId());
    }
    @Test
    @DisplayName("Integration - Must increase the units in stock")
    void updateStockByProductIdTest02() {
        // arrange
        StockDTO input = new StockDTO(2);
        
        Product target = this.productsPersisted.get(0);
        Long TARGET_ID = target.getId();
        String EXPECTED_NAME = target.getName();
        Integer EXPECTED_UNITS = target.getStock().getUnit() + input.getUnit();

        // act
        var result = service.updateStockByProductId(TARGET_ID, input);

        // assert
        assertEquals(EXPECTED_UNITS, result.getUnit());
        assertEquals(EXPECTED_NAME, result.getName());
        assertEquals(TARGET_ID, result.getProductId());
    }
    @Test
    @DisplayName("Integration - Should update all stocks")
    void updateStocksTest01() {
        // arrange
        Map<Long, Integer> ORIGINAL_STOCKS_UNITS = this.productsPersisted.stream()
          .collect(Collectors.toMap(Product::getId, p -> p.getStock().getUnit()));

        List<StockWriteOffDTO> input = productsPersisted.stream()
            .map(p -> new StockWriteOffDTO(p.getId(), p.getStock().getUnit())).toList();

        // act
        service.updateStocks(input);

        // assert
        this.productsPersisted.forEach(p -> {
            Integer ORIGINAL_VALUE = ORIGINAL_STOCKS_UNITS.get(p.getId());
            Integer EXPECTED = ORIGINAL_VALUE - ORIGINAL_VALUE;
            Integer CURRENT = p.getStock().getUnit();

            assertEquals(EXPECTED, CURRENT);
            assertNotEquals(ORIGINAL_VALUE, CURRENT);
        });
    }
    @Test
    @DisplayName("Integration - Should create a Product")
    void createProductTest01() {
        // arrange
        ProductDTO input = new ProductDTO(
            "name",
            "description",
            BigDecimal.valueOf(999.99),
            Category.SSD, new StockDTO(999),
            new ManufacturerDTO("AMD"),
            List.of(new ProductSpecDTO("read", "7500MB/s")));

        // act
        service.createProduct(input);
        var result = repository.findById(repository.count()).get();

        // assert
        assertEquals(input.getName(), result.getName());
        assertEquals(input.getDescription(), result.getDescription());
        assertEquals(input.getPrice(), result.getPrice());
        assertEquals(input.getCategory(), result.getCategory());
        assertEquals(input.getManufacturer().getName(), result.getManufacturer().getName());
        assertEquals(input.getSpecs().get(0).getAttribute(), result.getSpecs().get(0).getAttribute());
        assertEquals(input.getSpecs().get(0).getValue(), result.getSpecs().get(0).getValue());
    }
    @Test
    @DisplayName("Integration - Should fail when passing a non-existent Manufacturer")
    void createProductTest02() {
        // arrange
        ProductDTO input = new ProductDTO(
            "name",
            "description",
            BigDecimal.valueOf(999.99),
            Category.SSD, new StockDTO(999),
            new ManufacturerDTO("non-existent"),
            List.of(new ProductSpecDTO("read", "7500MB/s")));

        // act
        assertThrows(EntityNotFoundException.class, () -> service.createProduct(input));
    }
}