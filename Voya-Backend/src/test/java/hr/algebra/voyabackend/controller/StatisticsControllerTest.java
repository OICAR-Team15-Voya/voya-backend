package hr.algebra.voyabackend.controller;

import hr.algebra.voyabackend.model.dto.StatisticsDto;
import hr.algebra.voyabackend.service.StatisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class StatisticsControllerTest {

    private RestTestClient client;
    private StatisticsService statisticsService;

    @BeforeEach
    void setUp() {
        statisticsService = Mockito.mock(StatisticsService.class);
        client = RestTestClient.bindToController(new StatisticsController(statisticsService)).build();
    }

    @Test
    void getStatistics_allTime() {
        // Arrange
        StatisticsDto mockStats = new StatisticsDto();
        mockStats.setTotalReservations(10);
        mockStats.setTotalRevenue(1000L);
        when(statisticsService.getStatistics(any(), any())).thenReturn(mockStats);

        // Act + Assert
        StatisticsDto responseBody = client.get()
                .uri("/voya/api/statistics")
                .exchange()
                .expectStatus().isOk()
                .expectBody(StatisticsDto.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(responseBody);
        assertEquals(10, responseBody.getTotalReservations());
    }

    @Test
    void getStatistics_last7Days() {
        when(statisticsService.getStatistics(any(), any())).thenReturn(new StatisticsDto());

        client.get()
                .uri("/voya/api/statistics?days=7")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getStatistics_customInterval() {
        when(statisticsService.getStatistics(any(), any())).thenReturn(new StatisticsDto());

        client.get()
                .uri("/voya/api/statistics?from=2026-01-01T00:00:00&to=2026-03-01T00:00:00")
                .exchange()
                .expectStatus().isOk();
    }
}