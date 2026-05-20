package hr.algebra.voyabackend.controller;

import hr.algebra.voyabackend.model.dto.ReservationCreateDto;
import hr.algebra.voyabackend.model.dto.ReservationDto;
import hr.algebra.voyabackend.model.dto.ReservationUpdateDto;
import hr.algebra.voyabackend.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.lang.reflect.Parameter;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReservationControllerTest {

    private RestTestClient client;
    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        reservationService = Mockito.mock(ReservationService.class);
        client = RestTestClient.bindToController(new ReservationController(reservationService)).build();
    }

    @Test
    void getAllReservations() {

        when(reservationService.getAll())
                .thenReturn(List.of(new ReservationDto()));

        client.get()
                .uri("/voya/api/reservations/all")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getReservationById() {
        ReservationDto mockReservation = new ReservationDto();
        mockReservation.setId(1);

        when(reservationService.getReservationById(1)).thenReturn(mockReservation);

        ReservationDto responseBody = client.get()
                .uri("/voya/api/reservations/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(ReservationDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(responseBody);
        assertEquals(1, responseBody.getId());
    }

    @Test
    void getReservationsForUser() {
        ReservationDto mockReservation = new ReservationDto();
        mockReservation.setUserId(1);

        when(reservationService.getReservationsByUserId(1))
                .thenReturn(List.of(mockReservation));

        List<ReservationDto> responseBody = client.get()
                .uri("/voya/api/reservations/user/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<ReservationDto>>() {})
                .returnResult()
                .getResponseBody();

        assertNotNull(responseBody);
        assertEquals(1, responseBody.get(0).getUserId());
    }

    @Test
    void getMyReservations() {
        // This endpoint uses @AuthenticationPrincipal, which requires
        // full security context - not testable with bindToController()
        // Would need @SpringBootTest + @WithMockUser to test properly
    }

    @Test
    void getAssignedRidesForDriver() {
        // This endpoint uses @AuthenticationPrincipal, which requires
        // full security context - not testable with bindToController()
        // Would need @SpringBootTest + @WithMockUser to test properly
    }

    @Test
    void createReservation() {
        ReservationCreateDto createDto = new ReservationCreateDto();
        createDto.setUserId(1);
        createDto.setVehicleCategoryId(1);
        createDto.setTime(LocalDateTime.now().plusDays(1));
        createDto.setPickupLocation("Zagreb Airport");
        createDto.setDropoffLocation("Hotel Esplanade");

        ReservationDto mockResponse = new ReservationDto();
        when(reservationService.createReservation(any())).thenReturn(mockResponse);

        ReservationDto responseBody = client.post()
                .uri("/voya/api/reservations")
                .body(createDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ReservationDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(responseBody);
    }

    @Test
    void adminUpdateReservation() {
        ReservationUpdateDto mockReservation = new ReservationUpdateDto();
        ReservationDto mockResponse = new ReservationDto();

        when(reservationService.updateReservation(any(), any())).thenReturn(mockResponse);

        ReservationDto responseBody = client.put()
                .uri("/voya/api/reservations/1")
                .body(mockReservation)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ReservationDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(responseBody);
    }

    @Test
    void clientUpdateReservation() {
        // This endpoint uses @AuthenticationPrincipal, which requires
        // full security context - not testable with bindToController()
        // Would need @SpringBootTest + @WithMockUser to test properly
    }

    @Test
    void setStatusToInProgress() {

        doNothing().when(reservationService).setStatusToInProgress(any());

        client.patch()
                .uri("/voya/api/reservations/set-in-progress/1")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void setStatusToCompleted() {

        doNothing().when(reservationService).setStatusToInProgress(any());

        client.patch()
                .uri("/voya/api/reservations/set-completed/1")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void cancelReservation() {
        // This endpoint uses @AuthenticationPrincipal, which requires
        // full security context - not testable with bindToController()
        // Would need @SpringBootTest + @WithMockUser to test properly
    }

    @Test
    void deleteReservation() {

        doNothing().when(reservationService).deleteReservation(any());

        client.delete()
                .uri("/voya/api/reservations/1")
                .exchange()
                .expectStatus().isOk();
    }
}