package hr.algebra.voyabackend.model.dto;

import hr.algebra.voyabackend.model.enums.Role;
import lombok.Data;

// class for mapping user data to frontend
@Data
public class UserDto {
    private Integer id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private Role role;
    private Boolean status;
}