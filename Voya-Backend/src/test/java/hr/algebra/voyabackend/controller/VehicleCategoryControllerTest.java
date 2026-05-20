package hr.algebra.voyabackend.controller;

import hr.algebra.voyabackend.model.dto.VehicleCategoryCreateDto;
import hr.algebra.voyabackend.model.dto.VehicleCategoryDto;
import hr.algebra.voyabackend.service.VehicleCategoryService;
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

class VehicleCategoryControllerTest {

    private RestTestClient client;
    private VehicleCategoryService vehicleCategoryService;

    @BeforeEach
    void setUp() {
        vehicleCategoryService = Mockito.mock(VehicleCategoryService.class);
        client = RestTestClient.bindToController(new VehicleCategoryController(vehicleCategoryService)).build();
    }

    @Test
    void getAllCategories() {
        // Arrange
        List<VehicleCategoryDto> mockCategories = List.of(
                new VehicleCategoryDto(1, "Luxury"),
                new VehicleCategoryDto(2, "Standard")
        );
        when(vehicleCategoryService.getAllCategories()).thenReturn(mockCategories);

        // Act + Assert
        List<VehicleCategoryDto> responseBody = client.get()
                .uri("/voya/api/vehicle-categories")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<VehicleCategoryDto>>() {})
                .returnResult()
                .getResponseBody();

        assertNotNull(responseBody);
        assertEquals(2, responseBody.size());
    }

    @Test
    void getCategoryById() {
        // Arrange
        VehicleCategoryDto mockCategory = new VehicleCategoryDto(1, "Luxury");
        when(vehicleCategoryService.getCategoryById(1)).thenReturn(mockCategory);

        // Act + Assert
        VehicleCategoryDto responseBody = client.get()
                .uri("/voya/api/vehicle-categories/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(VehicleCategoryDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(responseBody);
        assertEquals(1, responseBody.getId());
        assertEquals("Luxury", responseBody.getName());
    }

    @Test
    void getCategoryById_notFound() {
        // Arrange
        when(vehicleCategoryService.getCategoryById(999))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle category not found"));

        // Act + Assert
        client.get()
                .uri("/voya/api/vehicle-categories/999")
                .exchange()
                .expectStatus().isEqualTo(404);
    }

    @Test
    void createCategory() {
        // Arrange
        VehicleCategoryCreateDto createDto = new VehicleCategoryCreateDto();
        createDto.setName("Luxury");

        VehicleCategoryDto mockResponse = new VehicleCategoryDto(1, "Luxury");
        when(vehicleCategoryService.createCategory(any())).thenReturn(mockResponse);

        // Act + Assert
        VehicleCategoryDto responseBody = client.post()
                .uri("/voya/api/vehicle-categories")
                .body(createDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(VehicleCategoryDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(responseBody);
        assertEquals("Luxury", responseBody.getName());
    }

    @Test
    void createCategory_alreadyExists() {
        // Arrange
        VehicleCategoryCreateDto createDto = new VehicleCategoryCreateDto();
        createDto.setName("Luxury");

        when(vehicleCategoryService.createCategory(any()))
                .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Category already exists"));

        // Act + Assert
        client.post()
                .uri("/voya/api/vehicle-categories")
                .body(createDto)
                .exchange()
                .expectStatus().isEqualTo(409);
    }

    @Test
    void updateCategory() {
        // Arrange
        VehicleCategoryCreateDto updateDto = new VehicleCategoryCreateDto();
        updateDto.setName("Premium");

        VehicleCategoryDto mockResponse = new VehicleCategoryDto(1, "Premium");
        when(vehicleCategoryService.updateCategory(any(), any())).thenReturn(mockResponse);

        // Act + Assert
        VehicleCategoryDto responseBody = client.put()
                .uri("/voya/api/vehicle-categories/1")
                .body(updateDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(VehicleCategoryDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(responseBody);
        assertEquals("Premium", responseBody.getName());
    }

    @Test
    void updateCategory_notFound() {
        // Arrange
        VehicleCategoryCreateDto updateDto = new VehicleCategoryCreateDto();
        updateDto.setName("Premium");

        when(vehicleCategoryService.updateCategory(any(), any()))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle category not found"));

        // Act + Assert
        client.put()
                .uri("/voya/api/vehicle-categories/999")
                .body(updateDto)
                .exchange()
                .expectStatus().isEqualTo(404);
    }

    @Test
    void deleteCategory() {
        // Arrange
        doNothing().when(vehicleCategoryService).deleteCategory(any());

        // Act + Assert
        client.delete()
                .uri("/voya/api/vehicle-categories/1")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void deleteCategory_notFound() {
        // Arrange
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle category not found"))
                .when(vehicleCategoryService).deleteCategory(any());

        // Act + Assert
        client.delete()
                .uri("/voya/api/vehicle-categories/999")
                .exchange()
                .expectStatus().isEqualTo(404);
    }

    @Test
    void deleteCategory_hasVehiclesAssigned() {
        // Arrange
        doThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Cannot delete category because it has vehicles assigned to it"))
                .when(vehicleCategoryService).deleteCategory(any());

        // Act + Assert
        client.delete()
                .uri("/voya/api/vehicle-categories/1")
                .exchange()
                .expectStatus().isEqualTo(409);
    }
}