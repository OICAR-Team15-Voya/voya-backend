package hr.algebra.voyabackend.model.dto;

import lombok.Data;

// class for registering a new user
@Data
public class UserRegisterDto {
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String password; // password is hashed in Service
}