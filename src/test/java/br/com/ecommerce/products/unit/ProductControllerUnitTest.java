package br.com.ecommerce.products.unit;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import br.com.ecommerce.products.controller.ProductController;
import br.com.ecommerce.products.model.manufacturer.ManufacturerDTO;
import br.com.ecommerce.products.model.manufacturer.ManufacturerResponseDTO;
import br.com.ecommerce.products.model.product.Category;
import br.com.ecommerce.products.model.product.Product;
import br.com.ecommerce.products.model.product.ProductDTO;
import br.com.ecommerce.products.model.product.ProductIdAndUnitsDTO;
import br.com.ecommerce.products.model.product.ProductResponseDTO;
import br.com.ecommerce.products.model.product.ProductSpecDTO;
import br.com.ecommerce.products.model.product.ProductUpdateDTO;
import br.com.ecommerce.products.model.product.ProductUpdateResponseDTO;
import br.com.ecommerce.products.model.product.Stock;
import br.com.ecommerce.products.model.product.StockDTO;
import br.com.ecommerce.products.model.product.StockResponseDTO;
import br.com.ecommerce.products.service.ProductService;
import br.com.ecommerce.products.utils.RandomUtils;

@WebMvcTest(ProductController.class)
@AutoConfigureJsonTesters
class ProductControllerUnitTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ProductService service;

    @Autowired
    private JacksonTester<ProductDTO> productDTOJson;
    @Autowired
    private JacksonTester<List<ProductIdAndUnitsDTO>> productIdAndUnitsDTOJson;
    @Autowired
    private JacksonTester<List<Map<String, String>>> specsJson;
    @Autowired
    private JacksonTester<List<Long>> idListJson;
    @Autowired
    private JacksonTester<ProductUpdateDTO> productUpdateDTOJson;
    @Autowired
    private JacksonTester<StockDTO> stockDTOJson;


    @Test
    @DisplayName("Unit - createProduct - Must return status 201 and product data")
        void createProductTest01() throws IOException, Exception {
        // arrange
        ProductDTO requestBody = new ProductDTO(
            "name",
            "description",
            BigDecimal.valueOf(999.99),
            Category.SSD, new StockDTO(999),
            new ManufacturerDTO("AMD"),
            List.of(new ProductSpecDTO("read", "7500MB/s")));
        
        ProductResponseDTO responseBody = new ProductResponseDTO(
            1L,
            requestBody.getName(),
            requestBody.getDescription(),
            requestBody.getPrice(),
            requestBody.getCategory(),
            new Stock(requestBody.getStock().getUnit()),
            new ManufacturerResponseDTO(1L, requestBody.getManufacturer().getName()),
            requestBody.getSpecs()
        );
        when(service.createProduct(any())).thenReturn(responseBody);
        
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

        verify(service).createProduct(any());
    }
    @Test
    @DisplayName("Unit - createProduct - Should return status 400 when data is invalid")
    void createProductTest02() throws IOException, Exception {
        // arrange
        ProductDTO requestBody = new ProductDTO(
            "",
            "",
            null,
            null,
            null,
            null,
            null);
        
        // act
        mvc.perform(
            post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productDTOJson.write(requestBody).getJson())
        )
        // assert
        .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("Unit - verifyStocks - Must return status 207 and product stock")
    void verifyStocksTest01() throws IOException, Exception {
        // arrange
        List<ProductIdAndUnitsDTO> requestBody = List.of(
            new ProductIdAndUnitsDTO(1L, 1), // passes quantities beyond what is available
            new ProductIdAndUnitsDTO(2L, 1)
        );

        List<Product> serviceReturnMock = RandomUtils.getListOfRandomProducts(2, true);
        when(service.verifyProductsStocks(anyList())).thenReturn(serviceReturnMock);
        
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

        .andExpect(jsonPath("$[0].productId").exists())
        .andExpect(jsonPath("$[0].name").exists())
        .andExpect(jsonPath("$[0].unit").exists())

        .andExpect(jsonPath("$[1].productId").exists())
        .andExpect(jsonPath("$[1].name").exists())
        .andExpect(jsonPath("$[1].unit").exists());

        verify(service).verifyProductsStocks(anyList());
    }
    @Test
    @DisplayName("Unit - verifyStocks - Must return status 200 and empty body")
    void verifyStocksTest02() throws IOException, Exception {
        // arrange
        List<ProductIdAndUnitsDTO> requestBody = 
            List.of(new ProductIdAndUnitsDTO(1L, 1), new ProductIdAndUnitsDTO(2L, 1));

        List<Product> emptyList = List.of();
        when(service.verifyProductsStocks(anyList())).thenReturn(emptyList);
        
        // act
        mvc.perform(
            post("/products/stocks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productIdAndUnitsDTOJson.write(requestBody).getJson())
        )
        // assert
        .andExpect(status().isOk())
        .andExpect(content().string(""));

        verify(service).verifyProductsStocks(anyList());
    }
    @Test
    @DisplayName("Unit - verifyStocks - Must return status 400 when data is invalid")
    void verifyStocksTest03() throws IOException, Exception {
        // arrange
        List<ProductIdAndUnitsDTO> emptyRequestBody = List.of();

        // act
        mvc.perform(
            post("/products/stocks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productIdAndUnitsDTOJson.write(emptyRequestBody).getJson())
        )
        // assert
        .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("Unit - readAllBySpecs - Must return status 201 and product data")
    void readAllBySpecsTest01() throws IOException, Exception {
        // arrange
        Product productExpected = RandomUtils.getRandomProduct(true);
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
        when(service.getAllBySpecs(any(Pageable.class), anyList()))
            .thenReturn(new PageImpl<>(List.of(new ProductResponseDTO(productExpected))));

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

        verify(service).getAllBySpecs(any(), anyList());
    }
    @Test
    @DisplayName("Unit - readAllBySpecs - Must return status 400 when data is invalid")
    void readAllBySpecsTest02() throws IOException, Exception {
        // arrange
        List<Map<String, String>> requestBodyEmptyList = List.of();

        // act
        mvc.perform(
            post("/products/specs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(specsJson.write(requestBodyEmptyList).getJson())
        )
        // assert
        .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("Unit - getPrices - Must return status 200 and product prices")
    void getPricesTest01() throws IOException, Exception {
        // arrange
        var requestBody = List.of(1L, 2L);

        List<Product> serviceReturnMock = RandomUtils.getListOfRandomProducts(requestBody.size(), true);
        when(service.getAllProductsByListOfIds(anyList())).thenReturn(serviceReturnMock);
        
        // act
        mvc.perform(
            post("/products/prices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(idListJson.write(requestBody).getJson())
        )
        // assert
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(requestBody.size())));

        verify(service).getAllProductsByListOfIds(anyList());
    }
    @Test
    @DisplayName("Unit - getPrices - Must return status 400 when data is invalid")
    void getPricesTest02() throws IOException, Exception {
        // arrange
        List<Long> requestBodyEmptyList = List.of();
        
        // act
        mvc.perform(
            post("/products/prices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(idListJson.write(requestBodyEmptyList).getJson())
        )
        // assert
        .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }
    
    @Test
    @DisplayName("Unit - updateProduct - Must return status 200 and product data updated")
    void updateProductTest01() throws IOException, Exception {
        // assert
        ProductUpdateDTO requestBody = new ProductUpdateDTO(
            RandomUtils.getRandomString(),
            RandomUtils.getRandomString(),
            RandomUtils.getRandomBigDecimal(),
            RandomUtils.getRandomCategory(),
            new ManufacturerDTO(RandomUtils.getRandomString())
        );

        ProductUpdateResponseDTO responseBody = new ProductUpdateResponseDTO(
            null,
            requestBody.getName(),
            requestBody.getDescription(),
            requestBody.getPrice(),
            requestBody.getCategory(),
            requestBody.getManufacturer()
        );
        when(service.updateProductData(anyLong(), any())).thenReturn(responseBody);

        var EXPECTED_NAME = requestBody.getName();
        var EXPECTED_DESCRIPTION = requestBody.getDescription();
        var EXPECTED_PRICE = requestBody.getPrice();
        var EXPECTED_CATEGORY = requestBody.getCategory().toString();
        var EXPECTED_MANUFACTURER_NAME = requestBody.getManufacturer().getName();

        // act
        mvc.perform(
            put("/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productUpdateDTOJson.write(requestBody).getJson())
        )
        // assert
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value(EXPECTED_NAME))
        .andExpect(jsonPath("$.description").value(EXPECTED_DESCRIPTION))
        .andExpect(jsonPath("$.price").value(EXPECTED_PRICE))
        .andExpect(jsonPath("$.category").value(EXPECTED_CATEGORY))
        .andExpect(jsonPath("$.manufacturer.name").value(EXPECTED_MANUFACTURER_NAME));

        verify(service).updateProductData(anyLong(), any());
    }
    
    @Test
    @DisplayName("Unit - updateStock - Must return status 200 and stock data")
    void updateStockTest01() throws IOException, Exception {
        // arrange
        StockDTO requestBody = new StockDTO(2);
        StockResponseDTO serviceReturnMock = new StockResponseDTO(1L, "mock", 0);
        when(service.updateStockByProductId(anyLong(), any())).thenReturn(serviceReturnMock);

        var EXPECTED_PRODUCT_ID = serviceReturnMock.getProductId();
        var EXPECTED_NAME = serviceReturnMock.getName();
        var EXPECTED_UNIT = serviceReturnMock.getUnit();
        
        // act
        mvc.perform(
            put("/products/1/stocks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(stockDTOJson.write(requestBody).getJson())
        )
        // assert
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.productId").value(EXPECTED_PRODUCT_ID))
        .andExpect(jsonPath("$.name").value(EXPECTED_NAME))
        .andExpect(jsonPath("$.unit").value(EXPECTED_UNIT));

        verify(service).updateStockByProductId(anyLong(), any());
    }
    @Test
    @DisplayName("Unit - updateStock - Must return status 400 when request body is invalid")
    void updateStockTest02() throws IOException, Exception {
        // arrange
        StockDTO emptyRequestBody = new StockDTO();
        
        // act
        mvc.perform(
            put("/products/1/stocks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(stockDTOJson.write(emptyRequestBody).getJson())
        )
        // assert
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.fields.unit").exists());

        verifyNoInteractions(service);
    }
}