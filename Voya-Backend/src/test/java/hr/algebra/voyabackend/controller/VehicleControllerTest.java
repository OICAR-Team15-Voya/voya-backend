package hr.algebra.voyabackend.controller;

import hr.algebra.voyabackend.model.dto.VehicleCreateDto;
import hr.algebra.voyabackend.model.dto.VehicleDto;
import hr.algebra.voyabackend.service.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class VehicleControllerTest {

    private RestTestClient client;
    private VehicleService vehicleService;

    @BeforeEach
    void setUp() {
        vehicleService = Mockito.mock(VehicleService.class);
        client = RestTestClient.bindToController(new VehicleController(vehicleService)).build();
    }

    @Test
    void getAllVehicles() {
        // Arrange
        List<VehicleDto> mockVehicles = List.of(
                new VehicleDto(1, 1, "Luxury", "Toyota Camry", "Toyota", "Camry", "ZG-123-AB", true),
                new VehicleDto(2, 1, "Luxury", "Mercedes E", "Mercedes", "E-Class", "ZG-456-CD", true)
        );
        when(vehicleService.getAllVehicles()).thenReturn(mockVehicles);

        // Act + Assert
        List<VehicleDto> responseBody = client.get()
                .uri("/voya/api/vehicles/all")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<VehicleDto>>() {})
                .returnResult()
                .getResponseBody();

        assertNotNull(responseBody);
        assertEquals(2, responseBody.size());
    }

    @Test
    void getAllActiveVehicles() {
        // Arrange
        List<VehicleDto> mockVehicles = List.of(
                new VehicleDto(1, 1, "Luxury", "Toyota Camry", "Toyota", "Camry", "ZG-123-AB", true)
        );
        when(vehicleService.getALlActiveVehicles()).thenReturn(mockVehicles);

        // Act + Assert
        List<VehicleDto> responseBody = client.get()
                .uri("/voya/api/vehicles/all-active")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<VehicleDto>>() {})
                .returnResult()
                .getResponseBody();

        assertNotNull(responseBody);
        assertEquals(1, responseBody.size());
        assertTrue(responseBody.get(0).getActive());
    }

    @Test
    void getVehicleById() {
        // Arrange
        VehicleDto mockVehicle = new VehicleDto();
        mockVehicle.setId(1);
        mockVehicle.setManufacturer("Toyota");
        mockVehicle.setModel("Camry");
        mockVehicle.setRegistration("ZG-123-AB");
        when(vehicleService.getVehicleById(1)).thenReturn(mockVehicle);

        // Act + Assert
        VehicleDto responseBody = client.get()
                .uri("/voya/api/vehicles/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(VehicleDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(responseBody);
        assertEquals(1, responseBody.getId());
        assertEquals("Toyota", responseBody.getManufacturer());
    }

    @Test
    void getVehicleById_notFound() {
        // Arrange
        when(vehicleService.getVehicleById(999))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found"));

        // Act + Assert
        client.get()
                .uri("/voya/api/vehicles/999")
                .exchange()
                .expectStatus().isEqualTo(404);
    }

    @Test
    void createVehicle() {
        // Arrange
        VehicleCreateDto createDto = new VehicleCreateDto();
        createDto.setVehicleCategoryId(1);
        createDto.setName("Toyota Camry");
        createDto.setManufacturer("Toyota");
        createDto.setModel("Camry");
        createDto.setRegistration("ZG-123-AB");

        VehicleDto mockResponse = new VehicleDto();
        mockResponse.setId(1);
        mockResponse.setRegistration("ZG-123-AB");
        when(vehicleService.createVehicle(any())).thenReturn(mockResponse);

        // Act + Assert
        VehicleDto responseBody = client.post()
                .uri("/voya/api/vehicles")
                .body(createDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(VehicleDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(responseBody);
        assertEquals("ZG-123-AB", responseBody.getRegistration());
    }

    @Test
    void createVehicle_registrationAlreadyExists() {
        // Arrange
        VehicleCreateDto createDto = new VehicleCreateDto();
        createDto.setVehicleCategoryId(1);
        createDto.setManufacturer("Toyota");
        createDto.setModel("Camry");
        createDto.setRegistration("ZG-123-AB");

        when(vehicleService.createVehicle(any()))
                .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Vehicle with registration already exists"));

        // Act + Assert
        client.post()
                .uri("/voya/api/vehicles")
                .body(createDto)
                .exchange()
                .expectStatus().isEqualTo(409);
    }

    @Test
    void updateVehicle() {
        // Arrange
        VehicleCreateDto updateDto = new VehicleCreateDto();
        updateDto.setVehicleCategoryId(1);
        updateDto.setManufacturer("Mercedes");
        updateDto.setModel("E-Class");
        updateDto.setRegistration("ZG-456-CD");

        VehicleDto mockResponse = new VehicleDto();
        mockResponse.setId(1);
        mockResponse.setManufacturer("Mercedes");
        mockResponse.setModel("E-Class");
        when(vehicleService.updateVehicle(any(), any())).thenReturn(mockResponse);

        // Act + Assert
        VehicleDto responseBody = client.put()
                .uri("/voya/api/vehicles/1")
                .body(updateDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(VehicleDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(responseBody);
        assertEquals("Mercedes", responseBody.getManufacturer());
    }

    @Test
    void updateVehicle_notFound() {
        // Arrange
        VehicleCreateDto updateDto = new VehicleCreateDto();
        updateDto.setVehicleCategoryId(1);
        updateDto.setManufacturer("Mercedes");
        updateDto.setModel("E-Class");
        updateDto.setRegistration("ZG-456-CD");

        when(vehicleService.updateVehicle(any(), any()))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found"));

        // Act + Assert
        client.put()
                .uri("/voya/api/vehicles/999")
                .body(updateDto)
                .exchange()
                .expectStatus().isEqualTo(404);
    }

    @Test
    void deleteVehicle() {
        // Arrange
        doNothing().when(vehicleService).deactivateVehicle(any());

        // Act + Assert
        client.delete()
                .uri("/voya/api/vehicles/1")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void deleteVehicle_notFound() {
        // Arrange
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found"))
                .when(vehicleService).deactivateVehicle(any());

        // Act + Assert
        client.delete()
                .uri("/voya/api/vehicles/999")
                .exchange()
                .expectStatus().isEqualTo(404);
    }
}