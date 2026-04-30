package hr.algebra.voyabackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor @NoArgsConstructor
public class DriverReservationCount {
    private Integer driverId;
    private String driverName;
    private String driverLastName;
    private Integer count;
}
