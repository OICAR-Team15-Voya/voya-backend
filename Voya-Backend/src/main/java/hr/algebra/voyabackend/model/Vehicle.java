package hr.algebra.voyabackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "vehicles")
@Data
@AllArgsConstructor @NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "vehicle_category_id", nullable = false)
    private VehicleCategory vehicleCategory;

    @Column(length = 50)
    private String name;

    @Column(nullable = false, length = 50)
    private String manufacturer;

    @Column(nullable = false, length = 50)
    private String model;

    @Column(nullable = false, length = 50)
    private String registration;

    @Column(nullable = false)
    private Boolean active = true;

    @OneToMany(mappedBy = "vehicle")
    @ToString.Exclude
    private List<Reservation> reservations;

}