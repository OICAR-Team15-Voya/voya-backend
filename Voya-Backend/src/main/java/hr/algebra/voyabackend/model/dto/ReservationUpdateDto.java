package hr.algebra.voyabackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationUpdateDto {
    private Integer vehicleCategoryId;
    private Integer driverId;
    private Integer vehicleId;
    private LocalDateTime time;
    private String pickupLocation;
    private String dropoffLocation;
    private Integer passengerNumber;
    private Integer luggageNumber;
    private String welcomeSign;
    private String additionalNotes;
    private String status;
    private BigDecimal price;
    private Boolean isPaid;
}
