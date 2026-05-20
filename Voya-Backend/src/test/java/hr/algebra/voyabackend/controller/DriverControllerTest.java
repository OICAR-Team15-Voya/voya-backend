package hr.algebra.voyabackend.controller;

import hr.algebra.voyabackend.model.dto.DriverCreateDto;
import hr.algebra.voyabackend.model.dto.DriverDto;
import hr.algebra.voyabackend.service.DriverService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.server.ResponseStatusException;

import java.lang.module.ResolutionException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DriverControllerTest {

    private RestTestClient client;
    private DriverService driverService;

    @BeforeEach
    void setUp() {
        driverService = Mockito.mock(DriverService.class);
        client = RestTestClient.bindToController(new DriverController(driverService)).build();
    }

    @Test
    void getAllDrivers_success() {
        List<DriverDto> mockDrivers = List.of(
                new DriverDto(1, 1, "Ivan", "Horvat", "ivan@driver.com", "091234567", LocalDate.now()),
                new DriverDto(2, 2, "Marko", "Kovač", "marko@driver.com", "092345678", LocalDate.now())
        );
        when(driverService.getAllDrivers()).thenReturn(mockDrivers);

        List<DriverDto> responseBody = client.get()
                .uri("/voya/api/drivers/all")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<DriverDto>>() {})
                .returnResult()
                .getResponseBody();

        assertNotNull(responseBody);
        assertEquals(2, responseBody.size());
    }

    @Test
    void getDriverById() {
        DriverDto mockDriver = new DriverDto(1, 1, "Ivan", "Horvat", "ivan@driver.com", "091234567", LocalDate.now());
        when(driverService.getDriverById(1)).thenReturn(mockDriver);

        DriverDto responseBody = client.get()
                .uri("/voya/api/drivers/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(DriverDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(responseBody);
        assertEquals(1, responseBody.getId());
    }

    @Test
    void getDriverById_failed_wrongId() {
        int id = 1;
        when(driverService.getDriverById(id))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver not found with id: " + id));

        client.get()
                .uri("/voya/api/drivers/1")
                .exchange()
                .expectStatus().isEqualTo(404);
    }

    @Test
    void createDriver() {
        DriverDto mockDriver = new DriverDto(1, 1, "Ivan", "Horvat", "ivan@driver.com", "091234567", LocalDate.now());

        when(driverService.createDriver(any())).thenReturn(mockDriver);

        DriverDto responseBody = client.post()
                .uri("/voya/api/drivers")
                .body(mockDriver)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(DriverDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(responseBody);
    }

    @Test
    void createDriver_failed_emailInUse() {
        when(driverService.createDriver(any()))
                .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use"));

        DriverCreateDto createDto = new DriverCreateDto();
        createDto.setFirstName("Ivan");
        createDto.setLastName("Horvat");
        createDto.setEmail("ivan@driver.com");
        createDto.setPhone("091234567");
        createDto.setPassword("password");
        createDto.setLicenseValidUntil(LocalDate.now().plusYears(2));

        client.post()
                .uri("/voya/api/drivers")
                .body(createDto)
                .exchange()
                .expectStatus().isEqualTo(409);
    }

    @Test
    void updateDriver() {

        DriverDto mockDriver = new DriverDto();
        mockDriver.setFirstName("Ivan");
        mockDriver.setLastName("Horvat");
        mockDriver.setEmail("ivan@driver.com");
        mockDriver.setPhone("091234567");
        mockDriver.setLicenseValidUntil(LocalDate.now().plusYears(2));

        when(driverService.updateDriver(any(), any())).thenReturn(mockDriver);

        DriverDto responseBody = client.put()
                .uri("/voya/api/drivers/1")
                .body(mockDriver)
                .exchange()
                .expectStatus().isOk()
                .expectBody(DriverDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(responseBody);
    }

    @Test
    void deleteDriver() {
        client.delete()
                .uri("/voya/api/drivers/1")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void deleteDriver_failed_notFound() {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver not found"))
                .when(driverService).deleteDriver(any());

        client.delete()
                .uri("/voya/api/drivers/999")
                .exchange()
                .expectStatus().isEqualTo(404);
    }
}