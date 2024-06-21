package br.com.ecommerce.products.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import br.com.ecommerce.products.model.manufacturer.Manufacturer;
import br.com.ecommerce.products.model.manufacturer.ManufacturerDTO;
import br.com.ecommerce.products.repository.ManufacturerRepository;

@SpringBootTest
@AutoConfigureWebMvc
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ActiveProfiles("test")
@AutoConfigureTestDatabase
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class ManufacturerControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ManufacturerRepository repository;

    @Autowired
    private JacksonTester<ManufacturerDTO> manufacturerDTOJson;

    @MockBean
    private RabbitTemplate template;


    @Test
    @DisplayName("Integration - updateManufacturer - Should return status 200 and expected response body")
    void updateManufacturerTest01() throws IOException, Exception {
        // arrange
        this.saveManufacturer("Micro Start International");
        var requestBody = new ManufacturerDTO("MSI");

        // act
        mvc.perform(
            put("/manufacturers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(manufacturerDTOJson.write(requestBody).getJson())
        )
        // assert
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("1"))
        .andExpect(jsonPath("$.name").value(requestBody.getName()));
    }
    @Test
    @DisplayName("Integration - updateManufacturer - Should return status 400 when request body is invalid")
    void updateManufacturerTest02() throws IOException, Exception {
        // arrange
        this.saveManufacturer("Micro Start International");
        var invalidRequestBody = new ManufacturerDTO("");

        // act
        mvc.perform(
            put("/manufacturers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(manufacturerDTOJson.write(invalidRequestBody).getJson())
        )
        // assert
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.fields.name").exists());
    }
    
    private Manufacturer saveManufacturer(String name) {
        return this.repository.save(new Manufacturer(name));
    }
}