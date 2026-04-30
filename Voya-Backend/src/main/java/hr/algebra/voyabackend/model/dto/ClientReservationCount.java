package hr.algebra.voyabackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor @NoArgsConstructor
public class ClientReservationCount {
    private Integer clientId;
    private String clientFirstName;
    private String clientLastName;
    private Integer reservationCount;
}
