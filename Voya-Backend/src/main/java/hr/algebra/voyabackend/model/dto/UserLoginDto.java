package hr.algebra.voyabackend.model.dto;

import lombok.Data;

// class for logging in user
@Data
public class UserLoginDto {
    private String email;
    private String password; // compared against hash in Service
}
