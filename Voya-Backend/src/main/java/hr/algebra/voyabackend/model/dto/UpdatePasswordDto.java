package hr.algebra.voyabackend.model.dto;

import lombok.Data;

@Data
public class UpdatePasswordDto {
    private String oldPassword;
    private String newPassword;
}