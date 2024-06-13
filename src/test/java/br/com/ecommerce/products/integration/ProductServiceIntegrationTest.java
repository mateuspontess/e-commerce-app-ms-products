package br.com.ecommerce.products.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import br.com.ecommerce.products.configs.MySQLTestContainerConfig;
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
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import(MySQLTestContainerConfig.class)
@Transactional
class ProductServiceIntegrationTest {

    @Autowired
    private ProductService service;
    @Autowired
    private ProductRepository repository;
    @Autowired
    private ManufacturerRepository manufacturerRepository;

    @Autowired
    private EntityManager entityManager;
    private List<Product> productsPersisted;

    @BeforeEach
    void setup() throws UnsupportedOperationException, IOException, InterruptedException {
        this.seedDatabase();
    }
    @AfterEach
    void teardown() throws UnsupportedOperationException, IOException, InterruptedException {
        productsPersisted = null;
        this.truncateAllTables();
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
        assertNotEquals(updateData.getName(), result.getName());
        assertNotEquals(updateData.getDescription(), result.getDescription());
        assertNotEquals(updateData.getPrice(), result.getPrice());
        assertNotEquals(updateData.getCategory(), result.getCategory().toString());
        assertNotEquals(null, result.getManufacturer().getName());
    }
    @Test
    @DisplayName("Integration - Should update all attributes")
    void updateProductTest02() {
        // arrange
        Long ID = 1L;
        ProductUpdateDTO updateData = new ProductUpdateDTO("cooler", "cooler", BigDecimal.valueOf(500), "COOLER", new ManufacturerDTO("INTEL"));

        // act
        var result = service.updateProduct(ID, updateData);
        
        // assert
        assertEquals(updateData.getName(), result.getName());
        assertEquals(updateData.getDescription(), result.getDescription());
        assertEquals(updateData.getCategory(), result.getCategory().toString());
        assertEquals(updateData.getManufacturer().getName(), result.getManufacturer().getName());
    }
    @Test
    @DisplayName("Integration - Should only update the manufacturer")
    void updateProductTest03() {
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
    @DisplayName("Integration - Should fail when trying to update to a manufacturer that does not yet exist")
    void updateProductTest04() {
        // act
        Long ID = 1L;
        ProductUpdateDTO updateData = new ProductUpdateDTO(null, null, null, null, new ManufacturerDTO("RANDOM"));

        // act and assert
        assertThrows(EntityNotFoundException.class, () -> service.updateProduct(ID, updateData));
    }
    @Test
    @DisplayName("Integration - Should only update the category")
    void updateProductTest05() {
        // arrange
        Long ID = 1L;
        ProductUpdateDTO updateData = new ProductUpdateDTO(null, null, null, "COOLER", null);

        // act
        var result = service.updateProduct(ID, updateData);
        
        // assert
        assertEquals(updateData.getCategory(), result.getCategory().toString());
        assertNotEquals(updateData.getName(), result.getName());
        assertNotEquals(updateData.getDescription(), result.getDescription());
        assertNotEquals(updateData.getPrice(), result.getPrice());
    }
    @Test
    @DisplayName("Integration - Should only update the price")
    void updateProductTest06() {
        // arrange
        Long ID = 1L;
        ProductUpdateDTO updateData = new ProductUpdateDTO(null, null, BigDecimal.valueOf(500), null, null);

        // act
        var result = service.updateProduct(ID, updateData);
        
        // assert
        assertEquals(updateData.getPrice(), result.getPrice());
        assertNotEquals(updateData.getCategory(), result.getCategory().toString());
        assertNotEquals(updateData.getName(), result.getName());
        assertNotEquals(updateData.getDescription(), result.getDescription());
    }
    @Test
    @DisplayName("Integration - Should only update the description")
    void updateProductTest07() {
        // arrange
        Long ID = 1L;
        ProductUpdateDTO updateData = new ProductUpdateDTO(null, "updated", null, null, null);

        // act
        var result = service.updateProduct(ID, updateData);
        
        // assert
        assertEquals(updateData.getDescription(), result.getDescription());
        assertNotEquals(updateData.getPrice(), result.getPrice());
        assertNotEquals(updateData.getCategory(), result.getCategory().toString());
        assertNotEquals(updateData.getName(), result.getName());
    }
    @Test
    @DisplayName("Integration - Should only update the name")
    void updateProductTest08() {
        // arrange
        Long ID = 1L;
        ProductUpdateDTO updateData = new ProductUpdateDTO("updated", null, null, null, null);

        // act
        var result = service.updateProduct(ID, updateData);
        
        // assert
        assertEquals(updateData.getName(), result.getName());
        assertNotEquals(updateData.getDescription(), result.getDescription());
        assertNotEquals(updateData.getPrice(), result.getPrice());
        assertNotEquals(updateData.getCategory(), result.getCategory().toString());

    }
    @Test
    @DisplayName("Integration - Must reduce the units in stock")
    void updateStockByProductIdTest01() {
        // arrange
        Long ID = 1L;
        StockDTO writeOffStock = new StockDTO(-2);
        Product target = repository.findById(ID).get();

        Long TARGET_ID = target.getId();
        String TARGET_NAME = target.getName();
        Integer TARGET_UNITS = target.getStock().getUnit();

        // act
        var result = service.updateStockByProductId(ID, writeOffStock);

        // assert
        assertEquals(TARGET_UNITS - 2, result.getUnit());
        assertNotEquals(TARGET_UNITS, result.getUnit());

        assertEquals(TARGET_ID, result.getProductId());
        assertEquals(TARGET_NAME, result.getName());
    }
    @Test
    @DisplayName("Integration - Must increase the units in stock")
    void updateStockByProductIdTest02() {
        // arrange
        Long ID = 1L;
        StockDTO writeOffStock = new StockDTO(2);
        Product target = repository.findById(ID).get();

        Long TARGET_ID = target.getId();
        String TARGET_NAME = target.getName();
        Integer TARGET_UNITS = target.getStock().getUnit();

        // act
        var result = service.updateStockByProductId(ID, writeOffStock);

        // assert
        assertEquals(TARGET_UNITS + 2, result.getUnit());
        assertNotEquals(TARGET_UNITS, result.getUnit());

        assertEquals(TARGET_ID, result.getProductId());
        assertEquals(TARGET_NAME, result.getName());
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

    void seedDatabase() {
        var manufacturers = manufacturerRepository.saveAll(
            List.of(new Manufacturer("AMD"), new Manufacturer("INTEL")));

            ProductSpec spec1 = new ProductSpec("cores", "12");
            Product product1 = Product.builder()
                .name("aaa")
                .description("ddd")
                .price(BigDecimal.valueOf(1000))
                .category(Category.CPU)
                .stock(new Stock(1000))
                .manufacturer(this.getManufacturer(manufacturers, "AMD"))
                .specs(List.of(spec1))
                .build();

            ProductSpec spec2 = new ProductSpec("cores", "8");
            Product product2 = Product.builder()
                .name("bbb")
                .description("ddd")
                .price(BigDecimal.valueOf(100))
                .category(Category.CPU)
                .stock(new Stock(100))
                .manufacturer(this.getManufacturer(manufacturers, "AMD"))
                .specs(List.of(spec2))
                .build();

            ProductSpec spec3 = new ProductSpec("memmory size", "8GB");
            Product product3 = Product.builder()
                .name("ccc")
                .description("ddd")
                .price(BigDecimal.TEN)
                .category(Category.GPU)
                .stock(new Stock(10))
                .manufacturer(this.getManufacturer(manufacturers, "INTEL"))
                .specs(List.of(spec3))
                .build();
        spec1.setProduct(product1);
        spec2.setProduct(product2);
        spec3.setProduct(product3);
        this.productsPersisted = repository.saveAll(List.of(product1, product2, product3));
    }
    private Manufacturer getManufacturer(List<Manufacturer> manufacturers, String name) {
        return manufacturers.stream().filter(m -> m.getName().equalsIgnoreCase(name)).findFirst().get();
    }

    void truncateAllTables() {
        Query set1 = entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0");

        set1.executeUpdate();
        Query s = entityManager.createNativeQuery("TRUNCATE TABLE product_specs");
        s.executeUpdate();
        Query p = entityManager.createNativeQuery("TRUNCATE TABLE products");
        p.executeUpdate();
        Query m = entityManager.createNativeQuery("TRUNCATE TABLE manufacturers");
        m.executeUpdate();
        
        Query set2 = entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1");
        set2.executeUpdate();
    }
}