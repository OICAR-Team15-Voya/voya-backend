package hr.algebra.voyabackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleCreateDto {
    private Integer vehicleCategoryId;
    private String name;
    private String manufacturer;
    private String model;
    private String registration;
}