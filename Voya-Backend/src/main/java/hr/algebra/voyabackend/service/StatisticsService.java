package hr.algebra.voyabackend.service;

import hr.algebra.voyabackend.model.Reservation;
import hr.algebra.voyabackend.model.dto.ClientReservationCount;
import hr.algebra.voyabackend.model.dto.DriverReservationCount;
import hr.algebra.voyabackend.model.dto.StatisticsDto;
import hr.algebra.voyabackend.model.dto.VehicleReservationCount;
import hr.algebra.voyabackend.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    private final ReservationRepository reservationRepository;

    public StatisticsService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public StatisticsDto getStatistics(LocalDateTime from, LocalDateTime to) {

        List<Reservation> reservations;

        // if there is no start time specified, return all reservations
        if (from == null) {
            reservations = reservationRepository.findAll();
        }
        else {
            // if there is no end time specified, set the end time to now
            LocalDateTime end = to != null ? to : LocalDateTime.now();
            reservations = reservationRepository.findByTimeBetween(from, end);
        }

        return buildStatistics(reservations);
    }

    private StatisticsDto buildStatistics(List<Reservation> reservations) {

        StatisticsDto dto = new StatisticsDto();

        BigDecimal totalRevenue = reservations.stream()
                .map(Reservation::getPrice)      // get price from each reservation
                .filter(Objects::nonNull)         // skip null prices
                .reduce(BigDecimal.ZERO, BigDecimal::add);  // sum them all

        dto.setTotalRevenue(totalRevenue.toBigInteger().longValue());

        // total reservations
        dto.setTotalReservations(reservations.size());

        // reservations by driver
        dto.setDriverReservationCount(
                reservations.stream()
                        .filter(r -> r.getDriver() != null)
                        .collect(Collectors.groupingBy(Reservation::getDriver, Collectors.counting()))
                        .entrySet().stream()
                        .map(e -> new DriverReservationCount(
                                e.getKey().getId(),
                                e.getKey().getUser().getFirstName(),
                                e.getKey().getUser().getLastName(),
                                e.getValue().intValue()
                        ))
                        .toList()
        );

        // reservations by vehicle
        dto.setVehicleReservationCount(
                reservations.stream()
                        .filter(r -> r.getVehicle() != null)
                        .collect(Collectors.groupingBy(Reservation::getVehicle, Collectors.counting()))
                        .entrySet().stream()
                        .map(e -> new VehicleReservationCount(
                                e.getKey().getId(),
                                e.getKey().getName(),
                                e.getKey().getRegistration(),
                                e.getValue().intValue()
                        ))
                        .toList()
        );

        // reservations by client
        dto.setClientReservationCount(
                reservations.stream()
                        .collect(Collectors.groupingBy(Reservation::getUser, Collectors.counting()))
                        .entrySet().stream()
                        .map(e -> new ClientReservationCount(
                                e.getKey().getId(),
                                e.getKey().getFirstName(),
                                e.getKey().getLastName(),
                                e.getValue().intValue()
                        ))
                        .toList()
        );

        return dto;

    }
}
