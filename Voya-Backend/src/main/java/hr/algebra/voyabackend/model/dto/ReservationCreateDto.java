package hr.algebra.voyabackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationCreateDto {

    private Integer userId;
    private Integer vehicleCategoryId;
    private LocalDateTime time;
    private String pickupLocation;
    private String dropoffLocation;
    private Integer passengerNumber;
    private Integer luggageNumber;
    private String welcomeSign;
    private String additionalNotes;
}
