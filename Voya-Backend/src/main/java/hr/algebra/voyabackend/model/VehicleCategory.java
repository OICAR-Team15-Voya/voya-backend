package hr.algebra.voyabackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "vehicle_categories")
@Data
@AllArgsConstructor @NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class VehicleCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @Column(nullable = false, unique = true, length = 30)
    private String name;

    @OneToMany(mappedBy = "vehicleCategory")
    @ToString.Exclude
    private List<Vehicle> vehicles;

    @OneToMany(mappedBy = "vehicleCategory")
    @ToString.Exclude
    private List<Reservation> reservations;
}