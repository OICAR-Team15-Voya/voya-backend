package hr.algebra.voyabackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor @NoArgsConstructor
public class StatisticsDto {
    private Long totalRevenue;
    private Integer totalReservations;
    private List<DriverReservationCount> driverReservationCount;
    private List<VehicleReservationCount> vehicleReservationCount;
    private List<ClientReservationCount> clientReservationCount;
}
