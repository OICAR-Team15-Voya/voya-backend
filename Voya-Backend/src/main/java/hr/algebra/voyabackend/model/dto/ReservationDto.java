package hr.algebra.voyabackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDto {
    private Integer id;

    // User info
    private Integer userId;
    private String userFirstName;
    private String userLastName;
    private String userEmail;

    // Vehicle category
    private Integer vehicleCategoryId;
    private String vehicleCategoryName;

    // Driver info (nullable)
    private Integer driverId;
    private String driverFirstName;
    private String driverLastName;

    // Vehicle info (nullable)
    private Integer vehicleId;
    private String vehicleName;
    private String vehicleRegistration;

    // Reservation details
    private LocalDateTime time;
    private String pickupLocation;
    private String dropoffLocation;
    private Integer passengerNumber;
    private Integer luggageNumber;
    private String welcomeSign;
    private String additionalNotes;
    private String status;

    // Payment
    private BigDecimal price;
    private Boolean isPaid;
}