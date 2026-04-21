package hr.algebra.voyabackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "vehicles")
@Data
@AllArgsConstructor @NoArgsConstructor
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @OneToMany(mappedBy = "vehicle")
    private List<Reservation> reservations;
}