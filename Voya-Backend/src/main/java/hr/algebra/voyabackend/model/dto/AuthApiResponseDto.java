package hr.algebra.voyabackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthApiResponseDto {
    private String token;
    private Integer userId;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
}
