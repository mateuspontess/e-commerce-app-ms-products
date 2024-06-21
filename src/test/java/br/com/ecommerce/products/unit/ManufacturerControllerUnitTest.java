package br.com.ecommerce.products.unit;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.List;

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

import br.com.ecommerce.products.controller.ManufacturerController;
import br.com.ecommerce.products.model.manufacturer.ManufacturerDTO;
import br.com.ecommerce.products.model.manufacturer.ManufacturerResponseDTO;
import br.com.ecommerce.products.service.ManufacturerService;

@WebMvcTest(ManufacturerController.class)
@AutoConfigureJsonTesters
class ManufacturerControllerUnitTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<ManufacturerDTO> manufacturerDTOJson;

    @MockBean
    private ManufacturerService service;


    @Test
    @DisplayName("Unit - createManufacturer - Must return status 200 and Manufacturer data")
    void createManufacturerTest01() throws IOException, Exception {
        // arrange
        ManufacturerDTO requestBody = new ManufacturerDTO("AMD");
        ManufacturerResponseDTO responseBody = new ManufacturerResponseDTO(1L, requestBody.getName());
        when(service.saveManufacturer(any())).thenReturn(responseBody);

        // act and assert
        mvc.perform(
            post("/manufacturers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(manufacturerDTOJson.write(requestBody).getJson())
        )
        // assert
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(responseBody.getId()))
        .andExpect(jsonPath("$.name").value(responseBody.getName()));
            
        verify(service).saveManufacturer(any());
    }
    @Test
    @DisplayName("Unit - createManufacturer - Must return status 400 and field erros")
    void createManufacturerTest02() throws IOException, Exception {
        // arrange
        ManufacturerDTO invalidRequestBody = new ManufacturerDTO("");

        // act
        mvc.perform(
            post("/manufacturers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(manufacturerDTOJson.write(invalidRequestBody).getJson())
        )
        // assert
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.fields.name").exists());

        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("Unit - getAllManufacturers - Must return status 200 and expected response body")
    void getAllManufacturersTest01() throws IOException, Exception {
        // arrange
        List<ManufacturerResponseDTO> responseBody = List.of(
            new ManufacturerResponseDTO(1L, "AMD"),
            new ManufacturerResponseDTO(2L, "INTEL")
        );
        when(service.findAllManufacturers(any(Pageable.class))).thenReturn(new PageImpl<>(responseBody));

        var EXPECTED_ID_1 = responseBody.get(0).getId();
        var EXPECTED_NAME_1 = responseBody.get(0).getName();

        var EXPECTED_ID_2 = responseBody.get(1).getId();
        var EXPECTED_NAME_2 = responseBody.get(1).getName();

        var EXPECTED_ARRAY_SIZE = responseBody.size();

        // act
        mvc.perform(
            get("/manufacturers")
                .contentType(MediaType.APPLICATION_JSON)
        )
        // assert
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content", hasSize(EXPECTED_ARRAY_SIZE)))

        .andExpect(jsonPath("$.content[0].id").value(EXPECTED_ID_1))
        .andExpect(jsonPath("$.content[0].name").value(EXPECTED_NAME_1))

        .andExpect(jsonPath("$.content[1].id").value(EXPECTED_ID_2))
        .andExpect(jsonPath("$.content[1].name").value(EXPECTED_NAME_2));

        verify(service).findAllManufacturers(any());
    }
    
    @Test
    @DisplayName("Unit - getManufacturerById - Must return status 200 and expected response body")
    void getManufacturerByIdTest01() throws IOException, Exception {
        // arrange
        var responseBody = new ManufacturerResponseDTO(1L, "AMD");
        when(service.findManufacturerById(anyLong())).thenReturn(responseBody);

        // act
        mvc.perform(
            get("/manufacturers/1")
                .contentType(MediaType.APPLICATION_JSON)
        )
        // assert
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(responseBody.getId()))
        .andExpect(jsonPath("$.name").value(responseBody.getName()));

        verify(service).findManufacturerById(any());
    }

    @Test
    @DisplayName("Unit - updateManufacturerData - Must return status 200 and expected response body")
    void updateManufacturerDataTest01() throws IOException, Exception {
        // arrange
        var requestBody = new ManufacturerDTO("MSI");
        var responseBody = new ManufacturerResponseDTO(1L, requestBody.getName());
        when(service.updateManufacturerData(anyLong(), any())).thenReturn(responseBody);

        // act and assert
        mvc.perform(
            put("/manufacturers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(manufacturerDTOJson.write(requestBody).getJson())
        )
        // assert
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("1"))
        .andExpect(jsonPath("$.name").value(responseBody.getName()));

        verify(service).updateManufacturerData(anyLong(), any());
    }
    @Test
    @DisplayName("Unit - updateManufacturerData - Must return status 400 when request body is invalid")
    void updateManufacturerDataTest02() throws IOException, Exception {
        // arrange
        var invalidRequestBody = new ManufacturerDTO("");

        // act and assert
        mvc.perform(
            put("/manufacturers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(manufacturerDTOJson.write(invalidRequestBody).getJson())
        )
        // assert
        .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }
}