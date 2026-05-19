package hr.algebra.voyabackend.controller;

import hr.algebra.voyabackend.model.dto.StatisticsDto;
import hr.algebra.voyabackend.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/voya/api/statistics") // please use /voya/api/ convention for all endpoints
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * Returns statistics for a given time interval.
     * Pass no params for all-time statistics.
     * Pass 'days' for last N days (7, 14, 30).
     * Pass 'from' and 'to' for a custom interval.
     * URL: /voya/api/statistics
     */

    //GET /voya/api/statistics → all time
    //GET /voya/api/statistics?days=7 → last 7 days
    //GET /voya/api/statistics?days=14 → last 14 days
    //GET /voya/api/statistics?days=30 → last 30 days
    //GET /voya/api/statistics?from=2026-01-01T00:00:00&to=2026-03-01T00:00:00 → custom

    @GetMapping
    public ResponseEntity<StatisticsDto> getStatistics(
            @RequestParam(required = false) Integer days,
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to) {

        if (days != null) {
            from = LocalDateTime.now().minusDays(days);
            to = LocalDateTime.now();
        }

        return ResponseEntity.ok(statisticsService.getStatistics(from, to));
    }
}