package br.com.ecommerce.products.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import br.com.ecommerce.products.configs.MySQLTestContainerConfig;
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

    @BeforeEach
    void setup() throws UnsupportedOperationException, IOException, InterruptedException {
        System.out.println("REGISTRO ANTES DE TRUNCAR: " + repository.count());
        this.setProducts();
    }

    @AfterEach
    @Transactional
    void teardown() throws UnsupportedOperationException, IOException, InterruptedException {
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

    void setProducts() {
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
        repository.saveAll(List.of(product1, product2, product3));
    }
    private Manufacturer getManufacturer(List<Manufacturer> list, String name) {
        return list.stream().filter(m -> m.getName().equalsIgnoreCase(name)).findFirst().get();
    }

    
    @Test
    @DisplayName("Integration - Getting product details")
    void getProductTest01() {
        // act
        ProductResponseDTO result = service.getProduct(1L);

        // assert
		assertNotNull(result);
    }
    @Test
    @DisplayName("Integration - Getting product details by non-existent ID")
    void getProductTest02() {
        assertThrows(EntityNotFoundException.class, () -> service.getProduct(1000L));
	}

    @Test
    @DisplayName("Integration - Getting all products by params - With all parameters")
    void getAllProductWithParamsTest01() {
        // act
        var result = service
            .getAllProductWithParams(
                PageRequest.of(0, 10), "aaa", Category.CPU, BigDecimal.ZERO, BigDecimal.valueOf(1000), "AMD").getContent();

        // assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }
    @Test
    @DisplayName("Integration - Getting all products by params - With all parameters null")
    void getAllProductWithParamsTest02() {
        // act
        var result = service
            .getAllProductWithParams(PageRequest.of(0, 10), null, null, null, null, null)
            .getContent();

        // assert
        assertNotNull(result);
        assertEquals(3, result.size());
    }
    @Test
    @DisplayName("Integration - Getting all products by params - With name")
    void getAllProductWithParamsTest03() {
        // act
        var result = service
            .getAllProductWithParams(PageRequest.of(0, 10), "aaa", null, null, null, null)
            .getContent();

        // assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }
    @Test
    @DisplayName("Integration - Getting all products by params - With category")
    void getAllProductWithParamsTest04() {
        // act
        var result = service
            .getAllProductWithParams(PageRequest.of(0, 10), null, Category.CPU, null, null, null)
            .getContent();

        // assert
        assertEquals(2, result.size());
    }
    @Test
    @DisplayName("Integration - Getting all products by params - With minPrice")
    void getAllProductWithParamsTest05() {
        // act
        var result1 = service
            .getAllProductWithParams(PageRequest.of(0, 10), null, null, BigDecimal.TEN, null, null)
            .getContent();
        var result2 = service
            .getAllProductWithParams(PageRequest.of(0, 10), null, null, BigDecimal.valueOf(100), null, null)
            .getContent();
        var result3 = service
            .getAllProductWithParams(PageRequest.of(0, 10), null, null, BigDecimal.valueOf(1000), null, null)
            .getContent();
        var result4 = service
            .getAllProductWithParams(PageRequest.of(0, 10), null, null, BigDecimal.valueOf(1001), null, null)
            .getContent();


        // assert
        assertEquals(3, result1.size());
        assertEquals(2, result2.size());
        assertEquals(1, result3.size());
        assertEquals(0, result4.size());
    }
    @Test
    @DisplayName("Integration - Getting all products by params - With maxPrice")
    void getAllProductWithParamsTest06() {
        // act
        var result1 = service
            .getAllProductWithParams(PageRequest.of(0, 10), null, null, null, BigDecimal.valueOf(1000), null)
            .getContent();
        var result2 = service
            .getAllProductWithParams(PageRequest.of(0, 10), null, null, null, BigDecimal.valueOf(100), null)
            .getContent();
        var result3 = service
            .getAllProductWithParams(PageRequest.of(0, 10), null, null, null, BigDecimal.ONE, null)
            .getContent();

        // assert
        assertEquals(3, result1.size());
        assertEquals(2, result2.size());
        assertEquals(0, result3.size());
    }
    @Test
    @DisplayName("Integration - Getting all products by params - With manufacturer name")
    void getAllProductWithParamsTest07() {
        // act
        var result = service
            .getAllProductWithParams(PageRequest.of(0, 10), null, null, null, null, "AMD")
            .getContent();

        // assert
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Integration - Getting all products by specs - Getting all products by specs")
    void getAllBySpecsTest01() {
        // assert
        List<Map<String, String>> specs = List.of(Map.of("attribute", "cores", "value", "12"));

        // act
        var result = service.getAllBySpecs(PageRequest.of(0, 10), specs).getContent();

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
    @DisplayName("Integration - Getting all products by list of non-existent IDs")
    void updateProductTest01() {
        // act
        Long ID = 1L;
        ProductUpdateDTO updateData = new ProductUpdateDTO("cooler", "cooler", BigDecimal.valueOf(500), "COOLER", new ManufacturerDTO("INTEL"));

        // act
        var result = service.updateProduct(ID, updateData);
        
        // assert
        assertEquals(result.getName(), updateData.getName());
        assertEquals(result.getDescription(), updateData.getDescription());
        assertEquals(result.getCategory().toString(), updateData.getCategory());
        assertEquals(result.getManufacturer().getName(), updateData.getManufacturer().getName());
    }
    @Test
    @DisplayName("Integration - Getting all products by list of non-existent IDs")
    void updateProductTest02() {
        // act
        Long ID = 1L;
        ProductUpdateDTO updateData = new ProductUpdateDTO(null, null, null, null, new ManufacturerDTO("INTEL"));

        // act
        var result = service.updateProduct(ID, updateData);
        
        // assert
        assertNotEquals(result.getName(), updateData.getName());
        assertNotEquals(result.getDescription(), updateData.getDescription());
        assertNotEquals(result.getCategory().toString(), updateData.getCategory());
        assertNotEquals(result.getPrice(), updateData.getPrice());
        assertEquals(result.getManufacturer().getName(), updateData.getManufacturer().getName());
    }
    @Test
    @DisplayName("Integration - Getting all products by list of non-existent IDs")
    void updateProductTest03() {
        // act
        Long ID = 1L;
        ProductUpdateDTO updateData = new ProductUpdateDTO(null, null, null, null, new ManufacturerDTO("RANDOM"));

        // act and assert
        assertThrows(EntityNotFoundException.class, () -> service.updateProduct(ID, updateData));
    }
    @Test
    @DisplayName("Integration - Getting all products by list of non-existent IDs")
    void updateProductTest04() {
        // act
        Long ID = 1L;
        ProductUpdateDTO updateData = new ProductUpdateDTO(null, null, null, "COOLER", null);

        // act
        var result = service.updateProduct(ID, updateData);
        
        // assert
        assertEquals(result.getCategory().toString(), updateData.getCategory());
        assertNotEquals(result.getName(), updateData.getName());
        assertNotEquals(result.getDescription(), updateData.getDescription());
        assertNotEquals(result.getPrice(), updateData.getPrice());
    }
    @Test
    @DisplayName("Integration - Getting all products by list of non-existent IDs")
    void updateProductTest05() {
        // act
        Long ID = 1L;
        ProductUpdateDTO updateData = new ProductUpdateDTO(null, null, BigDecimal.valueOf(500), null, null);

        // act
        var result = service.updateProduct(ID, updateData);
        
        // assert
        assertEquals(result.getPrice(), updateData.getPrice());
        assertNotEquals(result.getCategory().toString(), updateData.getCategory());
        assertNotEquals(result.getName(), updateData.getName());
        assertNotEquals(result.getDescription(), updateData.getDescription());
    }
    @Test
    @DisplayName("Integration - Getting all products by list of non-existent IDs")
    void updateProductTest06() {
        // act
        Long ID = 1L;
        ProductUpdateDTO updateData = new ProductUpdateDTO(null, "updated", null, null, null);

        // act
        var result = service.updateProduct(ID, updateData);
        
        // assert
        assertEquals(result.getDescription(), updateData.getDescription());
        assertNotEquals(result.getPrice(), updateData.getPrice());
        assertNotEquals(result.getCategory().toString(), updateData.getCategory());
        assertNotEquals(result.getName(), updateData.getName());
    }
    @Test
    @DisplayName("Integration - Getting all products by list of non-existent IDs")
    void updateProductTest07() {
        // act
        Long ID = 1L;
        ProductUpdateDTO updateData = new ProductUpdateDTO("updated", null, null, null, null);

        // act
        var result = service.updateProduct(ID, updateData);
        
        // assert
        assertEquals(result.getName(), updateData.getName());
        assertNotEquals(result.getDescription(), updateData.getDescription());
        assertNotEquals(result.getPrice(), updateData.getPrice());
        assertNotEquals(result.getCategory().toString(), updateData.getCategory());

    }
    // @Test
    // @DisplayName("Integration - Getting all products by list of non-existent IDs")
    // void updateStockByProductIdTest01() {
    //     // act
    //     Long ID = 1L;
    //     StockDTO writeOffStock = new StockDTO(-2);

    //     // act
    //     var result = service.updateProduct(ID, updateData);
        
    //     // assert
    //     assertEquals(result.getName(), updateData.getName());
    //     assertNotEquals(result.getDescription(), updateData.getDescription());
    //     assertNotEquals(result.getPrice(), updateData.getPrice());
    //     assertNotEquals(result.getCategory().toString(), updateData.getCategory());
    // }
}