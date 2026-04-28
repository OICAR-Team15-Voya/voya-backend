package hr.algebra.voyabackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverDto {
    private Integer id;
    private Integer userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate licenseValidUntil;
}