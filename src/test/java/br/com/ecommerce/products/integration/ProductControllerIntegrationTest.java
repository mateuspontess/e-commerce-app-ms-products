package br.com.ecommerce.products.integration;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import br.com.ecommerce.products.model.manufacturer.Manufacturer;
import br.com.ecommerce.products.model.manufacturer.ManufacturerDTO;
import br.com.ecommerce.products.model.product.Category;
import br.com.ecommerce.products.model.product.Product;
import br.com.ecommerce.products.model.product.ProductDTO;
import br.com.ecommerce.products.model.product.ProductIdAndUnitsDTO;
import br.com.ecommerce.products.model.product.ProductSpec;
import br.com.ecommerce.products.model.product.ProductSpecDTO;
import br.com.ecommerce.products.model.product.ProductUpdateDTO;
import br.com.ecommerce.products.model.product.Stock;
import br.com.ecommerce.products.model.product.StockDTO;
import br.com.ecommerce.products.repository.ManufacturerRepository;
import br.com.ecommerce.products.repository.ProductRepository;

@SpringBootTest
@AutoConfigureWebMvc
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ActiveProfiles("test")
@AutoConfigureTestDatabase
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ProductRepository repository;
    @Autowired
    private ManufacturerRepository manufacturerRepository;

    @Autowired
    private JacksonTester<ProductDTO> productDTOJson;
    @Autowired
    private JacksonTester<List<ProductIdAndUnitsDTO>> productIdAndUnitsDTOJson;
    @Autowired
    private JacksonTester<List<Map<String, String>>> specsJson;
    @Autowired
    private JacksonTester<List<Long>> idsListJson;
    @Autowired
    private JacksonTester<ProductUpdateDTO> productUpdateDTOJson;
    @Autowired
    private JacksonTester<StockDTO> stockDTOJson;

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


    @Test
    @DisplayName("Integration - createProduct - Must return status 201 and product data")
        void createProductTest01() throws IOException, Exception {
        // arrange
        ProductDTO requestBody = new ProductDTO(
            "name",
            "description",
            BigDecimal.valueOf(999.99),
            Category.SSD, new StockDTO(999),
            new ManufacturerDTO("AMD"),
            List.of(new ProductSpecDTO("read", "7500MB/s")));
        
        // act
        mvc.perform(
            post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productDTOJson.write(requestBody).getJson())
        )
        // assert
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.name").value(requestBody.getName()))
        .andExpect(jsonPath("$.description").value(requestBody.getDescription()))
        .andExpect(jsonPath("$.price").value(requestBody.getPrice()))
        .andExpect(jsonPath("$.category").value(requestBody.getCategory().toString()))
        .andExpect(jsonPath("$.stock.unit").value(requestBody.getStock().getUnit().toString()))
        .andExpect(jsonPath("$.manufacturer.name").value(requestBody.getManufacturer().getName()))
        .andExpect(jsonPath("$.specs[0].attribute").value(requestBody.getSpecs().get(0).getAttribute()))
        .andExpect(jsonPath("$.specs[0].value").value(requestBody.getSpecs().get(0).getValue()))
        .andExpect( result -> {
            String redirect = result.getResponse().getRedirectedUrl();
            var nullableRedicrect = Optional.ofNullable(redirect);

            redirect = nullableRedicrect
                .orElseThrow(() -> new AssertionError("Redirect URL is null"));
            if (redirect.isBlank())
                throw new AssertionError("Redirect URL is blank");
        });
    }

    @Test
    @DisplayName("Integration - verifyStocks - Must return status 207 and product stock")
    void verifyStocksTest01() throws IOException, Exception {
        // arrange
        Product product_1 = productsPersisted.get(0);
        Product product_2 = productsPersisted.get(1);

        var EXPECTED_ID_1 = product_1.getId();
        var EXPECTED_NAME_1 = product_1.getName();
        int CURRENT_PRODUCT_STOCK_1 = product_1.getStock().getUnit();
        
        var EXPECTED_ID_2 = product_2.getId();
        var EXPECTED_NAME_2 = product_2.getName();
        int CURRENT_PRODUCT_STOCK_2 = product_2.getStock().getUnit();

        List<ProductIdAndUnitsDTO> requestBody = List.of(
            new ProductIdAndUnitsDTO(1L, CURRENT_PRODUCT_STOCK_1 + 1), // passes quantities beyond what is available
            new ProductIdAndUnitsDTO(2L, CURRENT_PRODUCT_STOCK_2 + 1)
        );

        // act
        mvc.perform(
            post("/products/stocks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productIdAndUnitsDTOJson.write(requestBody).getJson())
        )
        // assert
        .andExpect(status().isMultiStatus())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(requestBody.size())))

        .andExpect(jsonPath("$[0].productId").value(EXPECTED_ID_1))
        .andExpect(jsonPath("$[0].name").value(EXPECTED_NAME_1))
        .andExpect(jsonPath("$[0].unit").value(CURRENT_PRODUCT_STOCK_1))

        .andExpect(jsonPath("$[1].productId").value(EXPECTED_ID_2))
        .andExpect(jsonPath("$[1].name").value(EXPECTED_NAME_2))
        .andExpect(jsonPath("$[1].unit").value(CURRENT_PRODUCT_STOCK_2));
    }
    @Test
    @DisplayName("Integration - verifyStocks - Must return status 200 and empty body")
    void verifyStocksTest02() throws IOException, Exception {
        // arrange
        List<ProductIdAndUnitsDTO> requestBody = 
            List.of(new ProductIdAndUnitsDTO(1L, 1), new ProductIdAndUnitsDTO(2L, 1));
        
        // act
        mvc.perform(
            post("/products/stocks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productIdAndUnitsDTOJson.write(requestBody).getJson())
        )
        // assert
        .andExpect(status().isOk())
        .andExpect(content().string(""));
    }

    @Test
    @DisplayName("Integration - readAllBySpecs - Must return status 201 and product data that correspond to the specifications")
    void readAllBySpecsTest01() throws IOException, Exception {
        // arrange
        Product productExpected = this.productsPersisted.get(0);
        var EXPECTED_ID = productExpected.getId();
        var EXPECTED_NAME = productExpected.getName();
        var EXPECTED_DESCRIPTION = productExpected.getDescription();
        var EXPECTED_PRICE = productExpected.getPrice().doubleValue();
        var EXPECTED_CATEGORY = productExpected.getCategory().toString();
        var EXPECTED_UNITS = productExpected.getStock().getUnit();
        var EXPECTED_MANUFACTURER_NAME = productExpected.getManufacturer().getName();
        var EXPECTED_SPEC_ATTRIBUTE = productExpected.getSpecs().get(0).getAttribute();
        var EXPECTED_SPEC_VALUE = productExpected.getSpecs().get(0).getValue();
        List<Map<String, String>> input = List.of(Map.of("attribute", "cores", "value", "12"));
        
        // act
        mvc.perform(
            post("/products/specs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(specsJson.write(input).getJson())
        )
        // assert
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content[0].id").value(EXPECTED_ID))
        .andExpect(jsonPath("$.content[0].name").value(EXPECTED_NAME))
        .andExpect(jsonPath("$.content[0].description").value(EXPECTED_DESCRIPTION))
        .andExpect(jsonPath("$.content[0].price").value(EXPECTED_PRICE))
        .andExpect(jsonPath("$.content[0].category").value(EXPECTED_CATEGORY))
        .andExpect(jsonPath("$.content[0].stock.unit").value(EXPECTED_UNITS))
        .andExpect(jsonPath("$.content[0].manufacturer.name").value(EXPECTED_MANUFACTURER_NAME))
        .andExpect(jsonPath("$.content[0].specs[0].attribute").value(EXPECTED_SPEC_ATTRIBUTE))
        .andExpect(jsonPath("$.content[0].specs[0].value").value(EXPECTED_SPEC_VALUE));
    }

    @Test
    @DisplayName("Integration - getPrices - Must return status 200 and product prices")
    void getPricesTest01() throws IOException, Exception {
        // arrange
        var ID_PRODUCT_1 = productsPersisted.get(0).getId();
        var EXPECTED_PRICE_1 = productsPersisted.get(0).getPrice().doubleValue();

        var ID_PRODUCT_2 = productsPersisted.get(1).getId();
        var EXPECTED_PRICE_2 = productsPersisted.get(1).getPrice().doubleValue();
        
        var requestBody = List.of(ID_PRODUCT_1, ID_PRODUCT_2);
        
        // act
        mvc.perform(
            post("/products/prices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(idsListJson.write(requestBody).getJson())
        )
        // assert
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(requestBody.size())))

        .andExpect(jsonPath("$[0].id").value(ID_PRODUCT_1))
        .andExpect(jsonPath("$[1].id").value(ID_PRODUCT_2))
        
        .andExpect(jsonPath("$[0].price").value(EXPECTED_PRICE_1))
        .andExpect(jsonPath("$[1].price").value(EXPECTED_PRICE_2));
    }
    
    @Test
    @DisplayName("Integration - updateProduct - Must return status 200 and product data updated")
    void updateProductTest01() throws IOException, Exception {
        // assert
        Long ID = 1L;
        var EXPECTED_NAME = "update name";
        var EXPECTED_DESCRIPTION = "update description";
        var EXPECTED_PRICE = BigDecimal.valueOf(500);
        var EXPECTED_CATEGORY = Category.SSD.toString();
        var EXPECTED_MANUFACTURER_NAME = this.manufacturersPersisted.get(0).getName();

        ProductUpdateDTO requestBody = new ProductUpdateDTO(
            EXPECTED_NAME,
            EXPECTED_DESCRIPTION,
            EXPECTED_PRICE,
            Category.fromString(EXPECTED_CATEGORY), 
            new ManufacturerDTO(EXPECTED_MANUFACTURER_NAME)
        );
        
        // act
        mvc.perform(
            put("/products/" + ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(productUpdateDTOJson.write(requestBody).getJson())
        )
        // assert
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(ID))
        .andExpect(jsonPath("$.name").value(EXPECTED_NAME))
        .andExpect(jsonPath("$.description").value(EXPECTED_DESCRIPTION))
        .andExpect(jsonPath("$.price").value(EXPECTED_PRICE))
        .andExpect(jsonPath("$.category").value(EXPECTED_CATEGORY))
        .andExpect(jsonPath("$.manufacturer.name").value(EXPECTED_MANUFACTURER_NAME));
    }
    @Test
    @DisplayName("Integration - updateStock - Must reduce stock and return status 200")
    void updateStockTest01() throws IOException, Exception {
        // arrange
        StockDTO requestBody = new StockDTO(2);

        Long productId = 1L;
        var EXPECTED_PRODUCT_NAME = this.productsPersisted.get(0).getName();
        var ORIGINAL_STOCK = this.productsPersisted.get(0).getStock().getUnit();
        var EXPECTED_UNITS = ORIGINAL_STOCK + requestBody.getUnit();
        
        // act
        mvc.perform(
            put("/products/1/stocks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(stockDTOJson.write(requestBody).getJson())
        )
        // assert
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.productId").value(productId))
        .andExpect(jsonPath("$.name").value(EXPECTED_PRODUCT_NAME))
        .andExpect(jsonPath("$.unit").value(EXPECTED_UNITS));
    }
    @Test
    @DisplayName("Integration - updateStock - Must increase stock and return status 200")
    void updateStockTest02() throws IOException, Exception {
        // arrange
        StockDTO requestBody = new StockDTO(2);

        Long productId = 1L;
        var EXPECTED_PRODUCT_NAME = this.productsPersisted.get(0).getName();
        var ORIGINAL_STOCK = this.productsPersisted.get(0).getStock().getUnit();
        var EXPECTED_UNITS = ORIGINAL_STOCK + requestBody.getUnit();
        
        // act
        mvc.perform(
            put("/products/1/stocks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(stockDTOJson.write(requestBody).getJson())
        )
        // assert
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.productId").value(productId))
        .andExpect(jsonPath("$.name").value(EXPECTED_PRODUCT_NAME))
        .andExpect(jsonPath("$.unit").value(EXPECTED_UNITS));
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
}