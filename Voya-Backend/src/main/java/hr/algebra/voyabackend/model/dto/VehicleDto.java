package hr.algebra.voyabackend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleDto {
    private Integer id;
    private Integer vehicleCategoryId;
    private String categoryName;
    private String name;
    private String manufacturer;
    private String model;
    private String registration;
    private Boolean active;
}