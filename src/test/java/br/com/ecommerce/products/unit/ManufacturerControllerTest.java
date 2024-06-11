package br.com.ecommerce.products.unit;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
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
class ManufacturerControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<ManufacturerDTO> manufacturerDTOJson;

    @MockBean
    private ManufacturerService service;


    @Test
    @DisplayName("Unit - Create - Should return status 200")
    void createTest01() throws IOException, Exception {
        // arrange
        ManufacturerDTO requestBody = new ManufacturerDTO("AMD");
        ManufacturerResponseDTO responseBody = new ManufacturerResponseDTO(1L, requestBody.getName());
        when(service.saveManufacturer(any())).thenReturn(responseBody);

        // act and assert
        mvc.perform(
            post("/manufacturers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(manufacturerDTOJson.write(requestBody).getJson()))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(responseBody.getId()))
            .andExpect(jsonPath("$.name").value(responseBody.getName()));
            
        verify(service).saveManufacturer(any());
    }

    @Test
    @DisplayName("Unit - Create - Should return status 400")
    void createTest02() throws IOException, Exception {
        // arrange
        ManufacturerDTO requestBody = new ManufacturerDTO("");
        ManufacturerResponseDTO responseBody = new ManufacturerResponseDTO(1L, requestBody.getName());
        when(service.saveManufacturer(any())).thenReturn(responseBody);

        // act and assert
        mvc.perform(
            post("/manufacturers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(manufacturerDTOJson.write(requestBody).getJson()))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fields.name").exists());
    }

    @Test
    @DisplayName("Unit - Read all - Should return status 200 and expected response body")
    void readAllTest01() throws IOException, Exception {
        // arrange
        List<ManufacturerResponseDTO> responseBody = List.of(
            new ManufacturerResponseDTO(1L, "AMD"),
            new ManufacturerResponseDTO(2L, "INTEL"),
            new ManufacturerResponseDTO(3L, "NVIDIA"));
        when(service.getAll(any(Pageable.class))).thenReturn(new PageImpl<>(responseBody));

        // act and assert
        mvc.perform(
            get("/manufacturers")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content", hasSize(3)))
            .andExpect(jsonPath("$.content[0].name").value(responseBody.get(0).getName()))
            .andExpect(jsonPath("$.content[1].name").value(responseBody.get(1).getName()))
            .andExpect(jsonPath("$.content[2].name").value(responseBody.get(2).getName()));

        verify(service).getAll(any());
    }
    @Test
    @DisplayName("Unit - Read by id - Should return status 200 and expected response body")
    void readByIdTest01() throws IOException, Exception {
        // arrange
        var responseBody = new ManufacturerResponseDTO(1L, "AMD");
        when(service.getById(anyLong())).thenReturn(responseBody);

        // act and assert
        mvc.perform(
            get("/manufacturers/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(service).getById(any());
    }

    @Test
    @DisplayName("Unit - Update - Should return status 200 and expected response body")
    void updateTest01() throws IOException, Exception {
        // arrange
        var requestBody = new ManufacturerDTO("MSI");
        var responseBody = new ManufacturerResponseDTO(1L, requestBody.getName());
        when(service.updateManufacturer(anyLong(), any())).thenReturn(responseBody);

        // act and assert
        mvc.perform(
            put("/manufacturers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(manufacturerDTOJson.write(requestBody).getJson()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("1"))
            .andExpect(jsonPath("$.name").value(responseBody.getName()));

        verify(service).updateManufacturer(anyLong(), any());
    }
}