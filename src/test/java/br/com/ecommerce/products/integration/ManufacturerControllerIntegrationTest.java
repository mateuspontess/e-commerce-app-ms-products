package br.com.ecommerce.products.integration;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.List;

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
    @DisplayName("Integration - createManufacturer - Should return status 200")
    void createManufacturerTest01() throws IOException, Exception {
        // arrange
        String MANUFACTURER_NAME = "AMD";
        ManufacturerDTO requestBody = new ManufacturerDTO(MANUFACTURER_NAME);

        // act and assert
        mvc.perform(
            post("/manufacturers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(manufacturerDTOJson.write(requestBody).getJson()))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.name").value(MANUFACTURER_NAME));
    }
    @Test
    @DisplayName("Integration - createManufacturer - Should return status 400 and field error")
    void createManufacturerTest01Test02() throws IOException, Exception {
        // arrange
        ManufacturerDTO requestBody = new ManufacturerDTO("");

        // act and assert
        mvc.perform(
            post("/manufacturers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(manufacturerDTOJson.write(requestBody).getJson()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fields.name").exists());
    }

    @Test
    @DisplayName("Integration - getAllManufacturers - Should return status 200 and expected response body")
    void getAllManufacturersTest01() throws IOException, Exception {
        // arrange
        var manufacturers = List.of(new Manufacturer("AMD"), new Manufacturer("INTEL"), new Manufacturer("NVIDIA"));
        repository.saveAll(manufacturers);
        
        // act and assert
        mvc.perform(
            get("/manufacturers")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content", hasSize(3)))
            .andExpect(jsonPath("$.content[0].name").value(manufacturers.get(0).getName()))
            .andExpect(jsonPath("$.content[1].name").value(manufacturers.get(1).getName()))
            .andExpect(jsonPath("$.content[2].name").value(manufacturers.get(2).getName()));
    }

    @Test
    @DisplayName("Integration - getManufacturerById - Should return status 200 and expected response body")
    void getManufacturerById() throws IOException, Exception {
        // arrange
        repository.save(new Manufacturer("MSI"));

        // act and assert
        mvc.perform(
            get("/manufacturers/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Integration - updateManufacturer - Should return status 200 and expected response body")
    void updateManufacturerTest01() throws IOException, Exception {
        // arrange
        repository.save(new Manufacturer("Micro Start International"));
        var requestBody = new ManufacturerDTO("MSI");

        // act and assert
        mvc.perform(
            put("/manufacturers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(manufacturerDTOJson.write(requestBody).getJson()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("1"))
            .andExpect(jsonPath("$.name").value(requestBody.getName()));
    }
    @Test
    @DisplayName("Integration - updateManufacturer - Should return status 400 and field error")
    void updateManufacturerTest02() throws IOException, Exception {
        // arrange
        repository.save(new Manufacturer("Micro Star International"));
        var requestBody = new ManufacturerDTO("");

        // act and assert
        mvc.perform(
            put("/manufacturers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(manufacturerDTOJson.write(requestBody).getJson()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fields.name").exists());
    }
}