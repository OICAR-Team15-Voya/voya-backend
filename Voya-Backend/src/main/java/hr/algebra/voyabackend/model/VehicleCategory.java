package hr.algebra.voyabackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "vehicle_categories")
@Data
@AllArgsConstructor @NoArgsConstructor
public class VehicleCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 30)
    private String name;

    @OneToMany(mappedBy = "vehicleCategory")
    private List<Vehicle> vehicles;

    @OneToMany(mappedBy = "vehicleCategory")
    private List<Reservation> reservations;
}