package hr.algebra.voyabackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DriverCreateDto {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String password;
    private LocalDate licenseValidUntil;
}