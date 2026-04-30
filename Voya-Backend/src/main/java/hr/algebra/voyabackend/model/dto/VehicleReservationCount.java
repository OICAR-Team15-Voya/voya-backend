package hr.algebra.voyabackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleReservationCount {
    private Integer vehicleId;
    private String vehicleName;
    private String vehicleRegistration;
    private Integer reservationCount;
}
